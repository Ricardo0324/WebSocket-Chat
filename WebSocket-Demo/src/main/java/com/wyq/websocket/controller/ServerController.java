package com.wyq.websocket.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName ServerController
 * @Description: //TODO 模拟服务端发送消息
 * @Author wyq
 * @Date 2022/5/5 5:10
 */
@RestController
public class ServerController {
    @RequestMapping(value = "/server")
    public String server(HttpServletRequest request) {
        try {
            String msg = request.getParameter("msg");
            String user = request.getParameter("user");
            //注意，为了便于测试，这里写死了
            user = "abc";
            //获取用户的webSocket对象
            WebSocketServer ws = (WebSocketServer) WebSocketServer.getClients().get(user);
            //发送消息
            ws.sendMessage(msg);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return "<script language=\"javascript\" type=\"text/javascript\">\n" +
                "window.location.href=\"server.html\";\n" +
                "</script>";
    }
}
