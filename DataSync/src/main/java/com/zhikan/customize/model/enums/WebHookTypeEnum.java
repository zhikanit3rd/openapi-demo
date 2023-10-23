package com.zhikan.customize.model.enums;

import lombok.NonNull;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author: Ferry
 * @create: 2023/5/22
 * @description:
 **/
public enum WebHookTypeEnum {
    ISSUE_CREATE("Issue.Create"),
    WEBHOOK("webhook"),
    ;

    private String code;

    WebHookTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Optional<WebHookTypeEnum> findByCode(@NonNull String code){
        return Arrays.stream(values())
                .filter(webHookTypeEnum -> code.equals(webHookTypeEnum.code))
                .findFirst();
    }

}
