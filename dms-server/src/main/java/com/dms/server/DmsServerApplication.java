package com.dms.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 社員寮管理システム 启动类
 *
 * @author dms
 */
@SpringBootApplication
@MapperScan("com.dms.server.mapper")
public class DmsServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DmsServerApplication.class, args);
    }
}
