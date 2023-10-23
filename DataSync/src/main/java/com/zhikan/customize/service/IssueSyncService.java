package com.zhikan.customize.service;

import com.alibaba.fastjson.JSONObject;

/**
 * @author: Ferry
 * @create: 2023/5/22
 * @description:
 **/
public interface IssueSyncService {

    /**
     * 同步创建
     * @param event
     */
    void syncCreateIssue(JSONObject event);

    /**
     * 同步更新issue
     * @param event
     */
    void syncUpdateIssue(JSONObject event);

    /**
     * 同步删除issue
     * @param event
     */
    void syncDeleteIssue(JSONObject event);

    /**
     * 智能助理webhook同步创建
     * @param event
     */
    void automationSyncCreateIssue(JSONObject event);
}
