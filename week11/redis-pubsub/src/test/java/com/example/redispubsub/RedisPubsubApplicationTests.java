package com.example.redispubsub;

import com.example.redispubsub.pub.PublishOrder;
import com.example.redispubsub.sub.SubscribeOrder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.JedisPool;

@SpringBootTest
class RedisPubsubApplicationTests {

    @Test
    void contextLoads() {
    }


    @Test
    void pubSub(){
        JedisPool jedisPool = new JedisPool("127.0.0.1",6379);
        String channelName = "ORDER";

        SubscribeOrder subscribeOrder = new SubscribeOrder(jedisPool, channelName);
        PublishOrder publishOrder = new PublishOrder(jedisPool, channelName);
    }
}
