package com.sunyw.xyz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunyw.xyz.RabbitMqUtils;
import com.sunyw.xyz.vo.Mail;
import com.sunyw.xyz.vo.SendData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


/**
 * 名称: XX定义
 * 功能: <功能详细描述>
 * 方法: <方法简述-方法描述>
 * 版本: 1.0
 * 作者: sunyw
 * 说明: 说明描述
 * 时间: 2021/3/15 10:41
 */
@RestController
public class RabbitmqWeb {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMqUtils rabbitMqUtils;


    @RequestMapping("test/{quque}")
    public void test1(@PathVariable String quque) {
        //rabbitTemplate.setQueue(quque);
        rabbitTemplate.convertAndSend(quque, "23333");
    }

    @PostMapping("test2")
    public void test2(@RequestBody Mail mail){
        SendData sendData = new SendData("test2", "test2.222", "test2", mail, UUID.randomUUID().toString());
        rabbitMqUtils.send(sendData);
    }

}
