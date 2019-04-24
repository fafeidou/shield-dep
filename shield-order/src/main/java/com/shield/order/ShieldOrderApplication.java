package com.shield.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(value={"com.shield.model.task"})//扫描实体类
public class ShieldOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShieldOrderApplication.class, args);
    }

}
