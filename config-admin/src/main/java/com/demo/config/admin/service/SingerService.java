package com.demo.config.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.config.common.domain.Singer;
import com.demo.config.common.model.SingerDTO;

public interface SingerService extends IService<Singer> {
    /**
     * 保存歌手及其歌曲，并同步缓存
     */
    void saveSingerWithSongs(SingerDTO singerDTO);

    SingerDTO getSingerDetails(Long id);
}