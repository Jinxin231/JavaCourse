package com.example.redislock.lock;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;

/**
 * @author jinx
 * @Description TODO
 * @date 2021/7/14-14:54
 */
public class RedisLock {

    private enum EnumSingleton {
        /**
         * 懒汉枚举单例
         */
        INSTANCE;
        private RedisLock instance;

        EnumSingleton() {
            instance = new RedisLock();
        }

        public RedisLock getSingleton() {
            return instance;
        }
    }

    public static RedisLock getInstance() {
        return EnumSingleton.INSTANCE.getSingleton();
    }

    private JedisPool jedisPool = new JedisPool("127.0.0.1",6379);

    /*
     * 加锁
     * */
    public boolean lock(String lockValue, int expire) {
        try (Jedis jedis = jedisPool.getResource()) {
            SetParams setParams = new SetParams();
            setParams.nx();
            setParams.px(expire);
            return "OK".equals(jedis.set(lockValue,lockValue,setParams));
        }
    }

    /*
     * 释放锁
     * lua脚本保证原子性
     * */
    public boolean release(String lock) {
        String luaScript = "if redis.call('get',KEYS[1]) == ARGV[1] then " + "return redis.call('del',KEYS[1]) else return 0 end";
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.eval(luaScript, Collections.singletonList(lock), Collections.singletonList(lock)).equals(1L);
        }
    }

}
