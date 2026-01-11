package com.demo.config.client.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class SingerRedisAutoConfiguration {

    /**
     * 定义一个 SDK 专用的 RedisTemplate，起个特殊名字避免冲突
     */
    @Bean(name = "singerConfigRedisTemplate")
    public RedisTemplate<String, Object> singerConfigRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 统一序列化逻辑（必须与 B 端一致）
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(om);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        return template;
    }

    // 1. 定义监听器：收到消息后干什么
    @Bean
    public MessageListenerAdapter listenerAdapter(Cache<String, Object> localCache) {
        // 这里的匿名内部类会在收到消息时触发
        return new MessageListenerAdapter((MessageListener) (message, pattern) -> {
            String expiredKey = new String(message.getBody()).replace("\"", ""); // 去掉引号
            log.info("📥 收到缓存失效通知，正在清理本地缓存: {}", expiredKey);
            localCache.invalidate(expiredKey); // 精准清理对应的本地缓存
        });
    }

    // 2. 定义容器：管理 Redis 的长连接订阅
    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                   MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // 订阅频道：config:change:topic，必须与 Admin 端一致
        container.addMessageListener(listenerAdapter, new ChannelTopic("config:change:topic"));
        return container;
    }
}