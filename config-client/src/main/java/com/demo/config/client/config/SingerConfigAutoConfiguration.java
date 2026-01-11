package com.demo.config.client.config;

import com.demo.config.client.SingerConfigClient;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
public class SingerConfigAutoConfiguration {

    /**
     * 定义本地缓存：有效期1分钟，最大容量500个
     */
    @Bean(name = "singerLocalCache")
    public Cache<String, Object> singerLocalCache() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(500)
                .expireAfterWrite(1, TimeUnit.MINUTES) // 写入1分钟后失效
                .build();
    }

    @Bean
    public SingerConfigClient singerConfigClient(
            @Qualifier("singerConfigRedisTemplate") RedisTemplate<String, Object> redisTemplate,
            @Qualifier("singerLocalCache") Cache<String, Object> localCache) {
        return new SingerConfigClient(redisTemplate, localCache);
    }
}