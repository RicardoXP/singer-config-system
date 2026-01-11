package com.demo.config.common.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("song_info")
public class Song {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long singerId;

    private String songName;

    private LocalDate releaseDate;

    private String genre;

    private String lyrics;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}