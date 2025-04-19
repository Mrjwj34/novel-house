package org.jwj.novelbookservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"org.jwj"})
@MapperScan("org.jwj.novelbookservice.dao.mapper")
@EnableCaching
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"org.jwj.noveluserapi.feign"})
public class NovelBookServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NovelBookServiceApplication.class, args);
    }

}
