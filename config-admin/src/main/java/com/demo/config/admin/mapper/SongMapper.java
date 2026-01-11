package com.demo.config.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.config.common.domain.Song;
import org.apache.ibatis.annotations.Mapper;

/**
 * 歌曲表 Mapper 接口
 */
@Mapper
public interface SongMapper extends BaseMapper<Song> {
}