package com.zhikan.customize.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhikan.customize.model.WebhookResult;
import com.zhikan.customize.model.enums.WebHookTypeEnum;
import com.zhikan.customize.service.IssueSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * @author: Ferry
 * @create: 2023/5/22
 * @description:
 *   接收ligaAI的webhook请求
 **/
@RestController
@RequestMapping("/webhook")
@Slf4j
public class LigaWebhookController {

    @Autowired
    private IssueSyncService issueSyncService;

    /**
     * 工作相关事件
     * @param event
     * @return
     */
    @PostMapping("/issue")
    public WebhookResult issueEventWebhook(@RequestBody JSONObject event){

        System.out.println(event.toJSONString());
        String type = event.getString("type");
        Optional<WebHookTypeEnum> webHookTypeEnumOptional = WebHookTypeEnum.findByCode(type);
        if (!webHookTypeEnumOptional.isPresent()){
            log.info("event not support : {}", type);
            return WebhookResult.success();
        }
        log.info("event = {}", JSON.toJSONString(event));
        switch (webHookTypeEnumOptional.get()){
            case WEBHOOK -> issueSyncService.automationSyncCreateIssue(event);
          //  case ISSUE_CREATE -> issueSyncService.syncCreateIssue(event);
            default -> throw new IllegalArgumentException("event type not support");
        }
        return WebhookResult.success();
    }
/*
    *//**
     * 评论相关事件
     * @param event
     * @return
     *//*
    @PostMapping("/comment")
    public WebhookResult commentEventWebhook(@RequestBody JSONObject event){

        return WebhookResult.success();
    }

    *//**
     * 附件相关事件
     * @param event
     * @return
     *//*
    @PostMapping("/attachment")
    public WebhookResult attachmentEventWebhook(@RequestBody JSONObject event){

        return WebhookResult.success();
    }
    *//**
     * 子任务相关事件
     * @param event
     * @return
     *//*
    @PostMapping("/subtask")
    public WebhookResult subtaskEventWebhook(@RequestBody JSONObject event){

        return WebhookResult.success();
    }*/
}
