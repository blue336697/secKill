/*
package com.lhjitem.seckilldemo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

*/
/**
 * @author lhj
 * @create 2022/3/8 23:56
 *//*

@Configuration
@Slf4j
public class RabbitMQConfig {
    @Bean
    public Queue queue(){
        return new Queue("queue",true);
    }


    @Bean
    public HeadersExchange headersExchange(){
        return new HeadersExchange("xx");
    }


    @Bean
    public Binding binding01(){
        Map<String, Object> map = new HashMap<>();
        map.put("cqiLor" , "red");
        map.put("speed","low");
        return BindingBuilder.bind(queue()).to(headersExchange()).w
    }

    public void send05(Object msg) {
        log.info("发送消息(被两个queue接收):" + msg);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("color" , "red");
        properties.setHeader( "speed" , "fast");
        rabbitTemplate.convertAndSend("headersExchange","",msg);
    }


}
*/
