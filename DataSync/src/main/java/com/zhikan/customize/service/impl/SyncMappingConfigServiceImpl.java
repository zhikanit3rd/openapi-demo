package com.zhikan.customize.service.impl;

import com.alibaba.fastjson.JSON;
import com.zhikan.customize.config.AppConfig;
import com.zhikan.customize.model.SyncMappingConfig;
import com.zhikan.customize.service.SyncMappingConfigService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author: Ferry
 * @create: 2023/5/22
 * @description:
 **/
@Slf4j
@Service
public class SyncMappingConfigServiceImpl implements SyncMappingConfigService {

    @Autowired
    private AppConfig appConfig;
    /** 项目同步配置映射  */
    private Map<Long, List<SyncMappingConfig>> syncMappingConfigMap = new HashMap<>(100);

    @Autowired
    private ResourceLoader resourceLoader;

    @PostConstruct
    public void init() throws IOException {
        Resource resource = resourceLoader.getResource(appConfig.getSyncMappingPath());
        String json = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
        log.info("映射文件配置：{}", json);
        List<SyncMappingConfig>  syncMappingConfigs = JSON.parseArray(json, SyncMappingConfig.class);
        syncMappingConfigMap.putAll( syncMappingConfigs.stream().collect(Collectors.groupingBy(SyncMappingConfig::getSourceProjectId, Collectors.toList())));
        log.info("解析后的映射文件配置： {}", syncMappingConfigMap);
    }


    @Override
    public Optional<List<SyncMappingConfig>> getSyncMappingConfig(Long sourceProjectId) {
        return Optional.ofNullable(syncMappingConfigMap.get(sourceProjectId));
    }
}
