package com.zhikan.customize.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author: Ferry
 * @create: 2023/5/22
 * @description:
 **/
public class WebhookResult {

    public static final Integer SUCCESS = 0;

    @JsonProperty("errcode")
    private Integer errorCode;

    public WebhookResult() {
    }

    public static WebhookResult success(){
        WebhookResult webhookResult = new WebhookResult();
        webhookResult.setErrorCode(SUCCESS);
        return webhookResult;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "WebhookResult{" +
                "errorCode='" + errorCode + '\'' +
                '}';
    }
}
