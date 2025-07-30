package com.easypan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author areslee
 */
// 异步调用
@EnableAsync
// 开启事务
@EnableTransactionManagement
// 定时任务
@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.easypan")
@MapperScan(basePackages = {"com.easypan.mappers"})
public class EasyPanApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyPanApplication.class, args);
    }

}
