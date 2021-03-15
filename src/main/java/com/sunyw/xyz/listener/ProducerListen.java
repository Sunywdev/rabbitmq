package com.sunyw.xyz.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.sunyw.xyz.sec.RabbitTask;
import com.sunyw.xyz.vo.Mail;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.nio.cs.ext.MS874;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
            value = @Queue(value = "test2", durable = "true", arguments = {
                    //绑定死信队列的交换机名称
                    @Argument(name = "x-dead-letter-exchange", value = "deadExchange"),
                    //死信队列的key
                    @Argument(name = "x-dead-letter-routing-key", value = "deadKey"),
                    //最大存活时间单位:ms,超过这个时间就会从当前队列中移除,也就是放到死信队列中去
                    @Argument(name = "x-message-ttl", value = "1000", type = "java.lang.Integer")
                    //最大长度 超过长度后移除, 注意:rabbitmq修改交换器类型或者修改队列的时间,都会导致服务启动失败,解决方法:要不重新新建一个
                    // ,要不访问web控制台将之前旧的移除掉
                    //@Argument(name = "x-max-length",value = "5",type = "java.lang.Integer")
            }),
            exchange = @Exchange(name = "test2", type = "topic"),
            key = "test2.*")
    )
    @RabbitHandler
    public void test2(Channel channel, Message message) {
        System.out.println("消费者接受到消息");
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        System.out.println(deliveryTag);
        Mail mail = null;
        try {
            byte[] body = message.getBody();
            mail = objectMapper.readValue(body, Mail.class);
            int a = 1 / 0;
            System.out.println("test2接收到的信息为:" + mail.toString());
            channel.basicAck(deliveryTag, true);
        } catch (Exception e) {
            RabbitTask.deadListAdd(mail);
        }
    }

    /**
     * 死信队列处理
     *
     * @param channel
     * @param message
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "deadQueue"),
            exchange = @Exchange(name = "deadExchange", type = "topic"),
            key = "deadKey"))
    public void test2Dead(Channel channel, Message message) {

        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        Mail mail = null;
        try {
            byte[] body = message.getBody();
            mail = objectMapper.readValue(body, Mail.class);
            System.out.println("test2接收到的信息为:" + mail.toString());
            System.out.println("死信队列接收到消息");
            System.out.println(deliveryTag);
            channel.basicAck(deliveryTag, true);
        } catch (Exception e) {
            System.out.println("开始进入数组,等待唤醒");
            RabbitTask.deadListAdd(mail);
        }
    }

}
