package com.demo.config.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.config.admin.mapper.SingerMapper;
import com.demo.config.admin.mapper.SongMapper;
import com.demo.config.admin.service.SingerService;
import com.demo.config.common.domain.Singer;
import com.demo.config.common.domain.Song;
import com.demo.config.common.model.SingerDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j // 建议加上日志，方便观察消息发送
public class SingerServiceImpl extends ServiceImpl<SingerMapper, Singer> implements SingerService {

    private final SongMapper songMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String SINGER_CACHE_KEY = "config:singer:";
    private static final String SONGS_CACHE_KEY = "config:singer:songs:";

    // 关键点：定义与 SDK 端一致的消息频道名
    private static final String CACHE_CHANGE_TOPIC = "config:change:topic";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSingerWithSongs(SingerDTO singerDTO) {
        Singer singer = singerDTO.getSinger();
        List<Song> songs = singerDTO.getSongs();

        // 1. 保存/更新 歌手信息
        this.saveOrUpdate(singer);
        Long singerId = singer.getId();

        // 2. 清理旧歌曲：先从数据库物理删除该歌手的所有歌曲
        songMapper.delete(new LambdaQueryWrapper<Song>().eq(Song::getSingerId, singerId));

        // 3. 插入新歌曲列表
        if (songs != null && !songs.isEmpty()) {
            songs.forEach(song -> {
                // 重要：将 ID 置为 null！
                // 这样 MyBatis Plus 才会使用数据库的自增主键，而不是尝试插入前端传回来的旧 ID
                song.setId(null);

                song.setSingerId(singerId);
                songMapper.insert(song);
            });
        }

        // 4. 同步缓存并广播
        syncToCache(singerId, singer, songs);
        publishCacheChange(singerId);
    }

    @Override
    public SingerDTO getSingerDetails(Long id) {
        // 1. 查询歌手基本信息
        Singer singer = this.getById(id);
        // 2. 查询歌曲列表
        List<Song> songs = songMapper.selectList(
                new LambdaQueryWrapper<Song>().eq(Song::getSingerId, id)
        );

        // 3. 组装 DTO
        SingerDTO dto = new SingerDTO();
        dto.setSinger(singer);
        dto.setSongs(songs);
        return dto;
    }

    private void syncToCache(Long singerId, Singer singer, List<Song> songs) {
        redisTemplate.opsForValue().set(SINGER_CACHE_KEY + singerId, singer, 24, TimeUnit.HOURS);
        redisTemplate.opsForValue().set(SONGS_CACHE_KEY + singerId, songs, 24, TimeUnit.HOURS);
    }

    /**
     * 发送广播消息
     */
    private void publishCacheChange(Long singerId) {
        try {
            // 我们发送对应的 Key，让 C 端精准删除
            String singerKey = SINGER_CACHE_KEY + singerId;
            String songsKey = SONGS_CACHE_KEY + singerId;

            // 发送两条消息，或者发送一个自定义对象包含这两个 Key
            redisTemplate.convertAndSend(CACHE_CHANGE_TOPIC, singerKey);
            redisTemplate.convertAndSend(CACHE_CHANGE_TOPIC, songsKey);

            log.info("📡 [Cache Invalidation] 已发送广播消息清理本地缓存，ID: {}", singerId);
        } catch (Exception e) {
            // 广播失败不应影响主业务事务，打印警告即可
            log.warn("📡 [Cache Invalidation] 广播消息发送失败", e);
        }
    }
}