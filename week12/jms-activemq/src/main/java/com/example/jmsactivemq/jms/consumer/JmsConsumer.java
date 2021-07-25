package com.example.jmsactivemq.jms.consumer;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author 13041285
 * @Description TODO
 * @date 2021/7/23-16:53
 */
@Component
public class JmsConsumer {

    @JmsListener(destination = "activeTest")
    public void receiveMessage(final Map message) {
        System.out.println(message.toString());
    }
}


