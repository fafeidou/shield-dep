package com.shield.learn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(value={"com.shield.model.task","com.shield.model.learning"})//扫描实体类
public class ShieldLearnApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShieldLearnApplication.class, args);
    }
}
