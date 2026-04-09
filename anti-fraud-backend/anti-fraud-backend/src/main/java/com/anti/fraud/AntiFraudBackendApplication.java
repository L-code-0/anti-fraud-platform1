package com.anti.fraud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.anti.fraud.modules.*.mapper")
public class AntiFraudBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(AntiFraudBackendApplication.class, args);
		System.out.println("====================================");
		System.out.println("  高校反诈安全知识普及平台启动成功！  ");
		System.out.println("  API文档: http://localhost:8080/api/doc.html");
		System.out.println("====================================");
	}
}
