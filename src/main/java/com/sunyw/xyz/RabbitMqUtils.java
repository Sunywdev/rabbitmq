package com.sunyw.xyz;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunyw.xyz.vo.SendData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 名称: XX定义
 * 功能: <功能详细描述>
 * 方法: <方法简述-方法描述>
 * 版本: 1.0
 * 作者: sunyw
 * 说明: 说明描述
 * 时间: 2021/3/15 14:25
 */
@Component
public class RabbitMqUtils {

    private Logger logger = LoggerFactory.getLogger(RabbitMqUtils.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(SendData obj) {
        logger.info("开始通过消息队列进行消息发送,发送信息为:{}", obj.toString());
        try {
            Message build = MessageBuilder.withBody(objectMapper.writeValueAsBytes(obj.getReqData())).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
            rabbitTemplate.setConfirmCallback(confirmCallback);
            CorrelationData correlationData = new CorrelationData(obj.getSendId());
            rabbitTemplate.convertAndSend(obj.getRoutingKey(), obj.getRouteExchange(), build, correlationData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final RabbitTemplate.ConfirmCallback confirmCallback = (correlationData, ack, s) -> {
        logger.info("回调函数开始执行,返回id为:{}", correlationData.getId());
        logger.info("当前消息是否已被ACK?{}", ack);
        if (ack) {
           logger.info("消息已被确认");
        } else {
           logger.info("");
        }
    };

}
