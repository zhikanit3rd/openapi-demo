package com.zhikan.customize.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.liga.api.method.LigaApiResponse;
import com.liga.api.method.field.IssueFieldEnum;
import com.liga.api.method.request.IdRequest;
import com.liga.api.method.request.issue.IssueAddRequest;
import com.liga.api.method.request.issue.IssueRelationshipRequest;
import com.liga.api.method.response.IdResponse;
import com.liga.api.method.response.issue.IssueResponse;
import com.zhikan.customize.config.AppConfig;
import com.zhikan.customize.model.SyncMappingConfig;
import com.zhikan.customize.service.IssueSyncService;
import com.zhikan.customize.service.SyncMappingConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: Ferry
 * @create: 2023/5/22
 * @description: issue同步服务
 **/
@Slf4j
@Service
public class IssueSyncServiceImpl implements IssueSyncService {

    @Autowired
    private SyncMappingConfigService syncMappingConfigService;
    @Autowired
    private AppConfig appConfig;

    @Override
    public void syncCreateIssue(JSONObject event) {
    }

    @Override
    public void syncUpdateIssue(JSONObject event) {

    }

    @Override
    public void syncDeleteIssue(JSONObject event) {

    }

    @Override
    public void automationSyncCreateIssue(JSONObject event) {
        try {
            JSONObject issueJson = event.getJSONArray("events").getJSONObject(0);
            long issueId = issueJson.getLong("issueId");
            LigaApiResponse<IssueResponse> issueResponseLigaApiResponse = appConfig.getLiga().issue().get(IdRequest.builder().token(appConfig.getOpenApiAccessToken()).id(issueId).build());
            if (!issueResponseLigaApiResponse.successFlag()){
                log.error("get issue failed, issueId = {}, error = {}", issueId, issueResponseLigaApiResponse.getMsg());
                return;
            }
            IssueResponse issueResponse = issueResponseLigaApiResponse.getData();
            long issueTypeId = Long.valueOf(issueResponse.getData().get(IssueFieldEnum.ISSUE_TYPE_ID.getCode()).toString());

            Optional<List<SyncMappingConfig>> optionalSyncMappingConfigList = syncMappingConfigService.getSyncMappingConfig(issueResponse.getProjectId());
            if (!optionalSyncMappingConfigList.isPresent()){
                log.info("no mapping config found~~~，end process!!");
                return;
            }
            List<SyncMappingConfig> syncMappingConfigList = optionalSyncMappingConfigList.get();
            syncMappingConfigList.stream().forEach(syncMappingConfig -> {
                try {
                    Optional<SyncMappingConfig.IssueTypeMapping> issueTypeMappingOptional = syncMappingConfig.getIssueTypeMappings().stream()
                            .filter(x -> {
                                if (!x.getSourceTypeId().equals(issueTypeId)){
                                    return false;
                                }
                                if (CollectionUtils.isNotEmpty(x.getConditions())){
                                    boolean notMatch = x.getConditions().stream()
                                            .filter(y -> {
                                                Object sourceValue = issueResponse.getData().get(y.getFieldCode());
                                                if (sourceValue.getClass().isAssignableFrom(JSONArray.class)){
                                                    return !y.getValue().equals(Long.valueOf(((JSONArray) sourceValue).getLong(0)));
                                                } else {
                                                    return !y.getValue().equals(Long.valueOf(sourceValue.toString()));
                                                }
                                            })
                                            .findAny()
                                            .isPresent();
                                    return !notMatch;
                                }
                                return true;
                            }).findFirst();

                    if (!issueTypeMappingOptional.isPresent()){
                        log.info("issueType mapping not found~~~，end process!! config = {}", JSON.toJSONString(syncMappingConfig));
                        return;
                    }
                    Long targetIssueType = issueTypeMappingOptional.get().getTargetTypeId();

                    Map<String, Object> issueDataMap = new HashMap<>(100);

                    Stream.concat(syncMappingConfig.getFieldMappingConfigs().stream(), Stream.of(issueTypeMappingOptional.get().getStatusMapping())).forEach(
                            fieldMappingConfig -> {
                                    Map<Long, Long> mappingMap = fieldMappingConfig.getOptionMapping();

                                    Object sourceValue = issueResponse.getData().get(fieldMappingConfig.getSourceField());
                                    if (Objects.isNull(sourceValue)){
                                        return;
                                    }
                                    if (sourceValue.getClass().isAssignableFrom(JSONArray.class)){
                                        //数组处理
                                        JSONArray jsonArray = (JSONArray) sourceValue;
                                        if (CollectionUtils.isNotEmpty(jsonArray)){
                                            if (MapUtils.isEmpty(mappingMap)){
                                                if (Boolean.TRUE.equals(fieldMappingConfig.getSingle())){
                                                    //没有映射配置，返回是JSONArray，但实际是单个值的
                                                    issueDataMap.put(fieldMappingConfig.getTargetField(), jsonArray.get(0));
                                                } else {
                                                    //没有映射配置，直接传递过去
                                                    issueDataMap.put(fieldMappingConfig.getTargetField(), jsonArray);
                                                }
                                            } else {

                                                List<Long> values = jsonArray.stream()
                                                        .map(x -> mappingMap.get(Long.valueOf(x.toString())))
                                                        .filter(Objects::nonNull)
                                                        .collect(Collectors.toList());
                                                if (Boolean.TRUE.equals(fieldMappingConfig.getSingle())){
                                                    //有映射配置，返回是JSONArray，但实际是单个值的
                                                    issueDataMap.put(fieldMappingConfig.getTargetField(), values.get(0));
                                                } else {
                                                    //有映射配置，逐个替换
                                                    issueDataMap.put(fieldMappingConfig.getTargetField(), values);
                                                }
                                            }
                                        }
                                    } else {
                                        //单个值处理：没有映射直接传递，有映射则替换
                                        Object value = MapUtils.isEmpty(mappingMap) ?
                                                issueResponse.getData().get(fieldMappingConfig.getSourceField()) :
                                                mappingMap.get(Long.valueOf(issueResponse.getData().get(fieldMappingConfig.getSourceField()).toString()));
                                        issueDataMap.put(fieldMappingConfig.getTargetField(), value);
                                    }
                    });

                    IssueAddRequest issueAddRequest = IssueAddRequest.builder()
                            .projectId(syncMappingConfig.getTargetProjectId())
                            .issueTypeId(targetIssueType)
                            .data(issueDataMap)
                            .token(appConfig.getOpenApiAccessToken())
                            .build();
                    log.info("issueDataMap = {}", JSON.toJSONString(issueAddRequest));
                    LigaApiResponse<IdResponse> ligaApiResponse = appConfig.getLiga().issue().add(issueAddRequest);

                    appConfig.getLiga().issue().addRelationship(IssueRelationshipRequest.builder()
                                    .type("IS_CAUSED_BY")
                                    .linkedIssueId(ligaApiResponse.getData().getId())
                                    .token(appConfig.getOpenApiAccessToken())
                                    .issueId(issueId)
                            .build());
                }catch (Exception e){
                    log.error("sync failed, config = " + JSON.toJSONString(syncMappingConfig) + ", event = " + JSON.toJSONString(event), e);
                }
            });
        } catch (Exception e){
            log.error("event data = {}", event.toJSONString());
            log.error("sync create issue failed : " + e);
        }
    }
}
