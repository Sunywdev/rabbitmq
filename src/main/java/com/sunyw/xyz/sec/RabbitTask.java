package com.sunyw.xyz.sec;

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
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 名称: XX定义
 * 功能: <功能详细描述>
 * 方法: <方法简述-方法描述>
 * 版本: 1.0
 * 作者: sunyw
 * 说明: 说明描述
 * 时间: 2021/3/15 17:37
 */
@Component
public class RabbitTask {

    private Logger logger = LoggerFactory.getLogger(RabbitMqUtils.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 非死信存储
     */
    public static final List<SendData> rabbitList = new ArrayList<>();
    /**
     * 死信队列存储
     */
    public static final List<Mail> deadList = new ArrayList<>();

    public synchronized static void rabbitListAdd(SendData sendData) {
        if (!rabbitList.contains(sendData)) {
            rabbitList.add(sendData);
        }
    }

    public synchronized static void deadListAdd(Mail mail) {
        if (!deadList.contains(mail)) {
            deadList.add(mail);
        }
    }

    @Scheduled(cron = "*/5 * * * * ?")
    public void rabbitTask() {
        logger.info("[正常消息]队列长度为:{}", rabbitList.size());

        try {
            Iterator<SendData> iterator = rabbitList.iterator();
            while (iterator.hasNext()) {
                SendData obj = iterator.next();
                logger.info("[正常消息]开始通过消息队列进行消息发送,发送信息为:{}", obj.toString());
                Message build = MessageBuilder.withBody(objectMapper.writeValueAsBytes(obj.getReqData())).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
                rabbitTemplate.setConfirmCallback(confirmCallback);
                CorrelationData correlationData = new CorrelationData(obj.getSendId());
                rabbitTemplate.convertAndSend(obj.getRoutingKey(), obj.getRouteExchange(), build, correlationData);
                logger.info("[正常消息]消息发送完毕,开始进行删除");
                iterator.remove();
                logger.info("[正常消息]消息删除完毕");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final RabbitTemplate.ConfirmCallback confirmCallback = (correlationData, ack, s) -> {
        logger.info("[正常消息]回调函数开始执行,返回id为:{}", correlationData.getId());
        logger.info("[正常消息]当前消息是否已被ACK?{}", ack);
        if (ack) {
            logger.info("[正常消息]消息已被确认");
        } else {
            logger.info("[正常消息]消息未被确认");
        }
    };

    @Scheduled(cron = "*/5 * * * * ?")
    public void deadTask() {
        logger.info("[死信消息]队列长度为:{}", deadList.size());
        try {
            Iterator<Mail> iterator = deadList.iterator();
            while (iterator.hasNext()) {
                Mail obj = iterator.next();
                logger.info("[死信消息]开始通过消息队列进行消息发送,发送信息为:{}", obj.toString());
                Message build = MessageBuilder.withBody(objectMapper.writeValueAsBytes(obj)).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
                rabbitTemplate.setConfirmCallback(confirmCallback);
                CorrelationData correlationData = new CorrelationData(obj.getMailId());
                rabbitTemplate.convertAndSend("deadExchange", "deadKey", build, correlationData);
                logger.info("[死信消息]执行完毕,开始移除");
                iterator.remove();
                logger.info("[死信消息]消息删除完毕");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}