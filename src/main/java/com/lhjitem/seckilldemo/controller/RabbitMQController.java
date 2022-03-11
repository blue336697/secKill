package com.lhjitem.seckilldemo.controller;

import com.lhjitem.seckilldemo.rabbitmq.MQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author lhj
 * @create 2022/3/10 20:04
 */
@Controller
public class RabbitMQController {

    @Autowired
    private MQSender mqSender;

    @RequestMapping("/mq")
    @ResponseBody
    public void mq(){
        mqSender.send("你好");
    }
}
