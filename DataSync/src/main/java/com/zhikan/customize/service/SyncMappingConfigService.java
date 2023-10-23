package com.zhikan.customize.service;

import com.zhikan.customize.model.SyncMappingConfig;

import java.util.List;
import java.util.Optional;

/**
 * @author: Ferry
 * @create: 2023/5/22
 * @description: 同步映射配置服务
 **/
public interface SyncMappingConfigService {

    /**
     * 根据源数据的项目ID获取同步的配置
     * @param sourceProjectId
     * @return
     */
    Optional<List<SyncMappingConfig>> getSyncMappingConfig(Long sourceProjectId);
}
