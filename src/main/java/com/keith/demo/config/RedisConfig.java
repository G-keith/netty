package com.keith.demo.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;
import java.time.Duration;

/**
 * @author keith
 * @date 2020-04-13
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * redis的key生成策略
     *
     * @return redis key值
     */
    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }

    @Bean
    CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        //初始化一个RedisCacheWriter
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer);
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(pair);
        defaultCacheConfig.entryTtl(Duration.ofSeconds(30));
        //初始化RedisCacheManager
        return new RedisCacheManager(redisCacheWriter, defaultCacheConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setEnableTransactionSupport(true);
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * redis string
     */
    @Bean
    public ValueOperations<String, Object> valueOperations() {
        return redisTemplate.opsForValue();
    }
    /**
     * redis hash
     */
    @Bean
    public HashOperations<String, String, Object> hashOperations() {
        return redisTemplate.opsForHash();
    }

    /**
     * redis list
     */
    @Bean
    public ListOperations<String, Object> listOperations() {
        return redisTemplate.opsForList();
    }

    /**
     * redis set
     */
    @Bean
    public SetOperations<String, Object> setOperations() {
        return redisTemplate.opsForSet();
    }

    /**
     * redis zset
     */
    @Bean
    public ZSetOperations<String, Object> zSetOperations() {
        return redisTemplate.opsForZSet();
    }
}
