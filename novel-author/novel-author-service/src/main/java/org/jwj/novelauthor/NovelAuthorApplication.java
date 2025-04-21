package org.jwj.novelauthor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"org.jwj.*"})
@MapperScan("org.jwj.novelauthor.dao.mapper")
@EnableCaching
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"org.jwj.novelbookapi.feign"})
public class NovelAuthorApplication {

    public static void main(String[] args) {
        SpringApplication.run(NovelAuthorApplication.class, args);
    }

}
