package com.example.random;

import com.example.random.interfaces.mq.message.UploadImgMessage;
import com.example.random.interfaces.mq.producer.UploadImgProducer;
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
