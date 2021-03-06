package com.wyq.websocket.component;

import cn.hutool.json.JSONObject;
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
 * @Date 2022/5/5 17:47
 * @ServerEndpoint 通过这个 spring boot 就可以知道你暴露出去的 websockst 应用的路径，有点类似我们经常用的@RequestMapping。比如你的启动端口是8080，而这个注解的值是ws，那我们就可以通过 ws://127.0.0.1:8080/websocket 来连接你的应用
 * @OnOpen 当 websocket 建立连接成功后会触发这个注解修饰的方法，注意它有一个 Session 参数
 * @OnClose 当 websocket 建立的连接断开后会触发这个注解修饰的方法，注意它有一个 Session 参数
 * @OnMessage 当客户端发送消息到服务端时，会触发这个注解修改的方法，它有一个 String 入参表明客户端传入的值
 * @OnError 当 websocket 建立连接时出现异常会触发这个注解修饰的方法，注意它有一个 Session 参数
 */
@Component
@ServerEndpoint("/webSocket/{userId}")
public class WebSocketServer {
    /**
     * concurrent包的线程安全Set，用来存放每个用户对应的Session对象。
     */
    private static Map<String, Session> clients = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session,@PathParam("userId") String userId) throws IOException {
        if (userId == null) {
            return;
        }
        clients.put(userId, session);
        System.out.println("用户：" + userId + "已连接到websocke服务器");
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("userId") String userId) throws IOException {
        clients.remove(userId);
        System.out.println("用户：" + userId + "已离开websocket服务器");
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String json) throws IOException {
        System.out.println("前端发送的信息为：" + json);
        JSONObject jsonObject = new JSONObject(json);
        String fromUser = jsonObject.getStr("from");
        String toUser = jsonObject.getStr("to");
        String mes = jsonObject.getStr("text");
        Session toSession = clients.get(toUser);
        //如果这个好友在线就直接发给他
        if (toSession != null) {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("from", fromUser);
            jsonObject1.put("text", mes);
            jsonObject1.put("type", 2);
            String mesTo = jsonObject1.toString();
            sendMessageTo(mesTo, toSession);
        } else {
            System.out.println("对方不在线，对方id为：" + toUser);
        }
    }

    /**
     * 出现异常触发的方法
     */
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    /**
     * 单发给某人
     */
    public void sendMessageTo(String message, Session session) throws IOException {
        session.getBasicRemote().sendText(message);
    }
}
