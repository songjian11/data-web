package com.cs.mobile.api;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * API接口启动类
 * 
 * @author wells.wong
 * @date 2018年11月18日
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@EnableTransactionManagement
public class ApiApplication {
	public static void main(String[] args) throws InterruptedException, IOException {
		SpringApplication.run(ApiApplication.class, args);
		System.out.println("-------------------API启动成功-------------------");
	}
}
