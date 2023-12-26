package com.example.random.interfaces.mq.producer;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


@Component
public class UploadImgProducer {

//    @Value("${rocketmq.topic}")
//    private String topic;

//    @Autowired

//
//
//
//    public void SendMessage(String message) {
//        rocketMQTemplate.syncSendOrderly(topic, message, String.valueOf(System.currentTimeMillis()));
//    }
}
