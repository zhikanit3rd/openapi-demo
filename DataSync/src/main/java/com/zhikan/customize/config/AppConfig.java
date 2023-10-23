package com.zhikan.customize.config;

import com.liga.api.Liga;
import com.liga.api.LigaConfig;
import com.liga.api.method.LigaApiException;
import com.liga.api.method.request.authorize.AccessTokenRequest;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;

/**
 * @author: Ferry
 * @create: 2023/5/22
 * @description: 配置文件
 **/
@Configuration
@Data
@ConfigurationProperties(prefix = "app.config", ignoreUnknownFields = true)
@PropertySource(encoding = "UTF-8", value = "classpath:application.properties", ignoreResourceNotFound = true)
public class AppConfig {

    /** openAPI配置 */
    private LigaOpenApiConfig openApi;
    /** openAPI token */
    private String openApiAccessToken;

    /** 映射配置文件加载 */
    private String syncMappingPath;
    private Liga liga;
    @Setter
    @Getter
    public static class LigaOpenApiConfig{
        private String clientId;
        private String secretKey;
        private String host;
    }

    @PostConstruct
    public void initToken(){
        try {
            LigaConfig ligaConfig = new LigaConfig();
            ligaConfig.setMethodsEndpointUrlPrefix(openApi.host);
            liga = Liga.getInstance(ligaConfig);
            openApiAccessToken = liga.authorize().accessPermanentToken(AccessTokenRequest.builder()
                    .clientId(openApi.clientId)
                    .secretKey(openApi.secretKey)
                    .build()).getData().getAccessToken();
        } catch (IOException| LigaApiException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
