package com.demo.config.admin.controller;

import com.demo.config.admin.service.SingerService;
import com.demo.config.common.domain.Singer;
import com.demo.config.common.model.Result;
import com.demo.config.common.model.SingerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/config/singer") // 注意这里的路径，前端请求时要对应
@RequiredArgsConstructor
public class SingerController {

    private final SingerService singerService;

    // 1. 保存/更新接口
    @PostMapping("/save")
    public Result<String> save(@RequestBody SingerDTO singerDTO) {
        singerService.saveSingerWithSongs(singerDTO);
        return Result.success("Success");
    }

    // 2. 列表查询接口
    @GetMapping("/list")
    public Result<List<Singer>> list() {
        return Result.success(singerService.list());
    }

    // 3. 详情查询接口 (保留这一个即可，返回 DTO 包含歌曲信息)
    @GetMapping("/get/{id}")
    public Result<SingerDTO> getById(@PathVariable("id") Long id) {
        return Result.success(singerService.getSingerDetails(id));
    }
}