package com.sunyw.xyz.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.sunyw.xyz.vo.Mail;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.nio.cs.ext.MS874;

import java.io.IOException;

/**
 * 名称: XX定义
 * 功能: <功能详细描述>
 * 方法: <方法简述-方法描述>
 * 版本: 1.0
 * 作者: sunyw
 * 说明: 说明描述
 * 时间: 2021/3/15 10:28
 */
@Component

public class ProducerListen {
    @Autowired
    private ObjectMapper objectMapper;


    @RabbitListener(queues = "testue")
    public void testue(Channel channel, Message message) {
        //当前消息的标签
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String messageStr = new String(message.getBody());
            System.out.println("testue接受到消息!!!" + messageStr);
            channel.basicAck(deliveryTag, true);
        } catch (Exception e) {
            try {
                channel.basicNack(deliveryTag, true, true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "test2", durable = "true"),
            exchange = @Exchange(name = "test2", type = "topic"),
            key = "test2.*")
    )
    @RabbitHandler
    public void test2(Channel channel, Message message) {
        System.out.println("消费者接受到消息");
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        System.out.println(deliveryTag);
        try {
            byte[] body = message.getBody();
            Mail mail = objectMapper.readValue(body, Mail.class);
            System.out.println("test2接收到的信息为:" + mail.toString());
            channel.basicAck(deliveryTag, true);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                channel.basicNack(deliveryTag, true, true);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
    }
}
