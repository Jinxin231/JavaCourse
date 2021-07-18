package com.example.redispubsub.sub;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

/**
 * @author 13041285
 * @Description TODO
 * @date 2021/7/16-14:32
 */
public class SubscribeOrder {

    public SubscribeOrder(final JedisPool jedisPool, final String channelName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("start subscriber order");
                try(Jedis jedis = jedisPool.getResource()) {
                    jedis.subscribe(setupSubscriber(), channelName);
                }
            }
        }, "subscriberThread").start();
    }

    private JedisPubSub setupSubscriber() {
        return new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                if (message.isEmpty()) {
                    System.out.println("SubPub End");
                    System.exit(0);
                }
                System.out.printf("Receive message from %s :: %s\n", channel, message);
            }
        };
    }
}
