package com.zhikan.customize.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author: Ferry
 * @create: 2023/5/22
 * @description:
 **/
@Setter
@Getter
public class SyncMappingConfig {

    /**
     * 数据来源项目
     */
    private Long sourceProjectId;
    /**
     * 同步目标项目
     */
    private Long targetProjectId;

    /**
     * 基于工作类型的映射关系配置
     */
    private List<IssueTypeMapping> issueTypeMappings;

    @Getter
    @Setter
    public static class IssueTypeMapping{
        /**
         * 来源项目的工作类型ID
         */
        private Long sourceTypeId;
        /**
         * 目标项目的工作类型ID
         */
        private Long targetTypeId;
        /**
         * 映射条件，满足条件才会进行映射，支持源项目1个类型，映射到目标多个工作类型
         * 等于运算
         */
        private List<Condition> conditions;
        /**
         * 字段映射配置
         */
        private FieldMappingConfig statusMapping;
    }

    @Getter
    @Setter
    public static class Condition{
        /**
         * 字段编码
         */
        private String fieldCode;
        /**
         * 字段值
         */
        private Long value;
    }


    private List<FieldMappingConfig> fieldMappingConfigs;

    @Getter
    @Setter
    public static class FieldMappingConfig{
        /**
         * 源数据的字段名字
         */
        private String sourceField;
        /**
         * 目标数据的字段名字
         */
        private String targetField;
        /** key原字段选项值，value需要映射的模板字段选项值 */
        private Map<Long, Long> optionMapping;
        /** 是否单个，当原返回值是数组时会用到 */
        private Boolean single;
    }
}
