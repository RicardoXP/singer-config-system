package com.demo.config.client;

import com.demo.config.client.config.SingerConfigAutoConfiguration;
import com.demo.config.client.config.SingerRedisAutoConfiguration;
import com.demo.config.common.domain.Singer;
import com.demo.config.common.domain.Song;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = SingerConfigClientTest.TestApplication.class)
@TestPropertySource(properties = {
        "spring.data.redis.host=localhost",
        "spring.data.redis.port=6379"
})
public class SingerConfigClientTest {

    @SpringBootApplication(
            scanBasePackages = "com.demo.config.client",
            exclude = {DataSourceAutoConfiguration.class}
    )
    // 关键点：强制导入官方 Redis 自动化配置，确保生成 RedisConnectionFactory
    @ImportAutoConfiguration(RedisAutoConfiguration.class)
    static class TestApplication {
    }

    @Autowired
    private SingerConfigClient singerConfigClient;

    @Test
    void testGetSinger() {
        assertNotNull(singerConfigClient);
        Singer singer = singerConfigClient.getSinger(2L);
        if (singer != null) {
            System.out.println("✅ 读取成功: " + singer.getName());
        } else {
            System.out.println("⚠️ 读取为空，请确认 Redis 里的数据 key 为 config:singer:2");
        }

        List<Song> songsBySinger = singerConfigClient.getSongsBySinger(2L);
        if (songsBySinger != null) {
            System.out.println("✅ 读取成功: " + songsBySinger.get(0).getSongName());
        } else {
            System.out.println("⚠️ 读取为空，请确认 Redis 里的数据 key 为 config:singer:2");
        }
    }
}