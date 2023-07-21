package com.example.random;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("com.example.random.interfaces.mapper")
@SpringBootApplication
public class RandomApplication {

	public static void main(String[] args) {
		SpringApplication.run(RandomApplication.class, args);
	}

}
