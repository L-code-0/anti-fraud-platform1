package com.anti.fraud.drill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DrillServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrillServiceApplication.class, args);
    }
}
