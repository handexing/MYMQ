package com.my.mq.remoting.protocol;

/**
 * @author handx
 * @version 1.0.0
 * @ClassName Message.java
 * @Description 消息
 * @createTime 2020年09月30日 23:11:00
 */
public class Message {

    private String topic;
    private String body;


    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


}
