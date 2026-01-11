package com.demo.config.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// 扫描 mapper 接口所在的包
@MapperScan("com.demo.config.admin.mapper")
public class ConfigAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigAdminApplication.class, args);
    }
}