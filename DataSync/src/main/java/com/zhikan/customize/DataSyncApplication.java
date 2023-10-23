package com.zhikan.customize;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: Ferry
 * @create: 2023/5/22
 * @description:
 **/
@SpringBootApplication
public class DataSyncApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DataSyncApplication.class);
        application.run(args);
    }
}
