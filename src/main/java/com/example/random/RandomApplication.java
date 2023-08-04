package com.example.random;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("com.example.random.interfaces.mapper")
@SpringBootApplication
@EnableFeignClients
public class RandomApplication {

	public static void main(String[] args) {
		SpringApplication.run(RandomApplication.class, args);
	}

}
