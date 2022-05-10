package com.wyq.websocket.controller;


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName WebSocketServer
 * @Description: //TODO
 * @Author wyq
 * @Date 2022/5/4 17:49
 * @ServerEndpoint 通过这个 spring boot 就可以知道你暴露出去的 websockst 应用的路径，有点类似我们经常用的@RequestMapping。比如你的启动端口是8080，而这个注解的值是ws，那我们就可以通过 ws://127.0.0.1:8080/websocket 来连接你的应用
 * @OnOpen 当 websocket 建立连接成功后会触发这个注解修饰的方法，注意它有一个 Session 参数
 * @OnClose 当 websocket 建立的连接断开后会触发这个注解修饰的方法，注意它有一个 Session 参数
 * @OnMessage 当客户端发送消息到服务端时，会触发这个注解修改的方法，它有一个 String 入参表明客户端传入的值
 * @OnError 当 websocket 建立连接时出现异常会触发这个注解修饰的方法，注意它有一个 Session 参数
 */
@ServerEndpoint("/socket/{userId}")
@Component
public class WebSocketServer {
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketController对象。
     */
    private static ConcurrentHashMap<String, Session> webSocketMap = new ConcurrentHashMap<>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /**
     * 接收id
     */
    private String userId;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        if (userId == null) {
            return;
        }
        System.out.println("用户：" + userId + "已连接websocket服务器");
        webSocketMap.put(userId, session);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        webSocketMap.remove(userId);
        System.out.println("用户：" + userId + "已离开websocket服务器");
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message) throws IOException {
        System.out.println("收到的信息为：" + message);
        JSONObject jsonObject = JSONUtil.parseObj(message);
        String fromUser = jsonObject.getStr("from");
        String toUser = jsonObject.getStr("to");
        String mes = jsonObject.getStr("text");
        Session toSession = webSocketMap.get(toUser);
        //如果这个好友在线就直接发给他
        if (toSession != null) {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("from", fromUser);
            jsonObject1.put("text", mes);
            jsonObject1.put("type", 2);
            String mesTo = jsonObject1.toString();
            sendMessageTo(mesTo, toSession);
        } else {
            System.out.println("对方不在线，对方id为："+toUser);
        }
    }

    /**
     * 出现异常触发的方法
     *
     * @param error
     */
    @OnError
    public void onError(Throwable error) {
        error.printStackTrace();
    }


    public void sendMessage(String message) throws IOException {
        System.out.println("测试" + message);
        // session.getBasicRemote().sendText(message);
        session.getAsyncRemote().sendText(message);

    }


    /**
     * 单发给某人
     */
    public void sendMessageTo(String message, Session toSession) throws IOException {
        toSession.getBasicRemote().sendText(message);
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessageAll(String message) {
        try {
            for (Session session : webSocketMap.values()) {
                session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized Map<String, Session> getClients() {

        return webSocketMap;

    }

}
