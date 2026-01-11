package com.demo.config.common.model;

import com.demo.config.common.domain.Singer;
import com.demo.config.common.domain.Song;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SingerVO extends Singer {
    /** 歌手关联的歌曲列表 */
    private List<Song> songList;
}