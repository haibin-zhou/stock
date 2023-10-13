package com.koder.stock.coreservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication
@MapperScan(basePackages = "com.koder.stock.coreservice.domain.mapper")
@ComponentScan(basePackages = "com.koder.stock")
@EnableScheduling
@ServletComponentScan(basePackages = "com.koder.stock.web")
@EnableCaching
public class CoreServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreServiceApplication.class, args);
	}

}
