package com.demo.config.client;

import com.demo.config.common.domain.Singer;
import com.demo.config.common.domain.Song;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
public class SingerConfigClient {

    private final RedisTemplate<String, Object> redisTemplate;
    private final Cache<String, Object> localCache; // 必须初始化的 final 字段

    private static final String SINGER_CACHE_KEY = "config:singer:";
    private static final String SONGS_CACHE_KEY = "config:singer:songs:";

    // 修复点：添加 this.localCache = localCache;
    public SingerConfigClient(@Qualifier("singerConfigRedisTemplate") RedisTemplate<String, Object> redisTemplate,
                              @Qualifier("singerLocalCache") Cache<String, Object> localCache) {
        this.redisTemplate = redisTemplate;
        this.localCache = localCache; // 刚才漏掉了这一行
    }

    public Singer getSinger(Long singerId) {
        String key = SINGER_CACHE_KEY + singerId;

        // 1. 尝试从本地缓存获取 (L1)
        Singer singer = (Singer) localCache.getIfPresent(key);
        if (singer != null) {
            log.info("🔥 命中本地内存缓存, key: {}", key);
            return singer;
        }

        // 2. 本地没有，查 Redis (L2)
        log.info("📡 本地缓存未命中，开始查询 Redis, key: {}", key);
        try {
            singer = (Singer) redisTemplate.opsForValue().get(key);
            if (singer != null) {
                // 3. 查到后回填本地内存
                localCache.put(key, singer);
            }
        } catch (Exception e) {
            log.error("ConfigClient获取Redis数据异常, id:{}", singerId, e);
        }

        return singer;
    }

    /**
     * 获取歌手的所有歌曲（建议也加上本地缓存逻辑）
     */
    public List<Song> getSongsBySinger(Long singerId) {
        String key = SONGS_CACHE_KEY + singerId;

        // 尝试从本地获取
        List<Song> songs = (List<Song>) localCache.getIfPresent(key);
        if (songs != null) {
            log.info("🔥 歌曲列表命中本地缓存, key: {}", key);
            return songs;
        }

        try {
            songs = (List<Song>) redisTemplate.opsForValue().get(key);
            if (songs != null) {
                localCache.put(key, songs);
            }
            return songs;
        } catch (Exception e) {
            log.error("ConfigClient获取歌曲列表异常, id:{}", singerId, e);
            return null;
        }
    }
}