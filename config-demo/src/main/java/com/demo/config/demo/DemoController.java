package com.demo.config.demo;

import com.demo.config.client.SingerConfigClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    // 重点：如果 SDK 自动装配成功，这里可以直接注入
    @Autowired
    private SingerConfigClient singerConfigClient;

    @GetMapping("/singer/{id}")
// 明确告诉 Spring，要把路径里的 {id} 绑定给这个 Long id
    public Object getSinger(@PathVariable("id") Long id) {
        return singerConfigClient.getSinger(id);
    }
}
