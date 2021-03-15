package com.sunyw.xyz.vo;

import java.io.Serializable;

/**
 * 名称: XX定义
 * 功能: <功能详细描述>
 * 方法: <方法简述-方法描述>
 * 版本: 1.0
 * 作者: sunyw
 * 说明: 说明描述
 * 时间: 2021/3/15 14:26
 */
public class SendData implements Serializable {

    private String routingKey;

    private String routeExchange;

    private String queue;

    private Object reqData;

    private String sendId;

    public SendData(String routingKey, String routeExchange, String queue, Object reqData, String sendId) {
        this.routingKey = routingKey;
        this.routeExchange = routeExchange;
        this.queue = queue;
        this.reqData = reqData;
        this.sendId = sendId;
    }

    public String getSendId() {
        return sendId;
    }

    public void setSendId(String sendId) {
        this.sendId = sendId;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getRouteExchange() {
        return routeExchange;
    }

    public void setRouteExchange(String routeExchange) {
        this.routeExchange = routeExchange;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public Object getReqData() {
        return reqData;
    }

    public void setReqData(Object reqData) {
        this.reqData = reqData;
    }

    @Override
    public String toString() {
        return "SendData{" +
                "routingKey='" + routingKey + '\'' +
                ", routeExchange='" + routeExchange + '\'' +
                ", queue='" + queue + '\'' +
                ", reqData=" + reqData +
                ", sendId='" + sendId + '\'' +
                '}';
    }
}
