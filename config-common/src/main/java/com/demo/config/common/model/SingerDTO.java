package com.demo.config.common.model;

import com.demo.config.common.domain.Singer;
import com.demo.config.common.domain.Song;
import lombok.Data;
import java.util.List;

@Data
public class SingerDTO {
    private Singer singer;      // 歌手基础信息
    private List<Song> songs;   // 该歌手的歌曲列表
}