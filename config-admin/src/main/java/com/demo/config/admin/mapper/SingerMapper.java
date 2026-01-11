package com.demo.config.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.config.common.domain.Singer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 歌手表 Mapper 接口
 */
@Mapper
public interface SingerMapper extends BaseMapper<Singer> {
    // 基础的增删改查已由 BaseMapper 提供
    // 如果后续有复杂的自定义 SQL，可以在这里定义方法并在 XML 中实现
}