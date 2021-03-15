package com.sunyw.xyz.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 名称: XX定义
 * 功能: <功能详细描述>
 * 方法: <方法简述-方法描述>
 * 版本: 1.0
 * 作者: sunyw
 * 说明: 说明描述
 * 时间: 2021/3/15 10:26
 */
@Configuration
public class ProducerConfig {

    @Bean
    public Queue queue() {
        return new Queue("testue");
    }


}
