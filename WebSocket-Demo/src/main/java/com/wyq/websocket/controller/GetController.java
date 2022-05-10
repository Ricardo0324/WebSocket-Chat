package com.wyq.websocket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName GetController
 * @Description: //TODO 跳转页面
 * @Author wyq
 * @Date 2022/5/4 21:26
 */
@Controller
public class GetController {
    @GetMapping({"/", "/index", "/index.html"})
    public String toIndex() {
        return "index";
    }

    @GetMapping({"/server.html"})
    public String toServer(){
        return "server";
    }
}
