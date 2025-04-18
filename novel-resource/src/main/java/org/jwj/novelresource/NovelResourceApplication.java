package org.jwj.novelresource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"org.jwj.*"})
@EnableDiscoveryClient
public class NovelResourceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NovelResourceApplication.class, args);
	}

}
