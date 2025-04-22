package org.jwj.novelai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
//@EnableDiscoveryClient
public class NovelAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(NovelAiApplication.class, args);
    }

}
