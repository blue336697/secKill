package com.lhjitem.seckilldemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.DefaultedRedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author lhj
 * @create 2021/11/1 23:35
 * redis配置类，主要功能就是实现需要存入redis的对象的序列化
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        //设置key的序列化
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //设置value序列化，产生的是字符串
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        //单独对hash类型进行配置
        //key的序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //value的序列化
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        //设置redis的连接工厂
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }


    /**
     * redis集成lua脚本
     * @return
     */
    @Bean
    public DefaultRedisScript<Long> script(){
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        //脚本位置，与applicatio.yaml同级目录
        redisScript.setLocation(new ClassPathResource("stock.lua"));
        redisScript.setResultType(Long.class);
        return redisScript;
    }
}
