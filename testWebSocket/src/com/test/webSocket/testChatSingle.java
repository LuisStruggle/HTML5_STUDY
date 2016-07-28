package com.test.webSocket;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

//该注解用来指定一个URI，客户端可以通过这个URI来连接到WebSocket。类似Servlet的注解mapping。无需在web.xml中配置。
@ServerEndpoint("/chatsingle")
public class testChatSingle {
	
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
     
    //concurrent包的线程安全map，用来存放每个客户端对应的MyWebSocket对象。
    private static ConcurrentHashMap<String,testChatSingle> webSocketMap = new ConcurrentHashMap<String,testChatSingle>();
     
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
     
    /**
     * 连接建立成功调用的方法
     * @param session  可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(Session session){
    	String socketId = session.getQueryString();
    	// 中文转码
    	try {
			socketId = URLDecoder.decode(socketId, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			System.out.println("中文转码失败！！！");
		}
		
		String nickname = socketId.substring(socketId.indexOf("=") + 1);
		
        this.session = session;
        
        webSocketMap.put(nickname, this);     //加入map中
        
        addOnlineCount();           //在线数加1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());        
    	// 返回在线人数(推送消息)
		sendMessage("onlineCount:"+String.valueOf(testChatSingle.onlineCount));
		// 返回当前人的昵称
		sendMessage("nickname:"+nickname);
		// 推送其它人的昵称给当前人
		sendNickname(nickname);
    }
     
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session){
    	String socketId = session.getQueryString();
    	// 中文转码
    	try {
			socketId = URLDecoder.decode(socketId, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			System.out.println("中文转码失败！！！");
		}
		
		String nickname = socketId.substring(socketId.indexOf("=") + 1);
    	
        webSocketMap.remove(nickname);  //从map中删除
        subOnlineCount();           //在线数减1
        // 返回在线人数(推送消息)
		sendMessage("onlineCount:"+String.valueOf(testChatSingle.onlineCount));
		
		// 返回当前人的昵称
		sendMessage("offlineNickname:"+nickname);
		
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }
     
    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("来自客户端的消息:" + message);
        // 连接关闭
        
        //截取字符串
        String nickname1 = message.split(":")[0];
        message = message.split(":")[1];
        
        // 获取自己的昵称
        String nickname2 = null;
    	// 中文转码
    	try {
    		nickname2 = URLDecoder.decode(session.getQueryString(), "utf-8");
    		nickname2 = nickname2.substring(nickname2.indexOf("=") + 1);
		} catch (UnsupportedEncodingException e1) {
			System.out.println("中文转码失败！！！");
		}
        
        //群发消息
        for(String socketId: webSocketMap.keySet()){             
            try {
            	if(nickname2.equals(socketId) || nickname1.equals(socketId)) {
            		webSocketMap.get(socketId).session.getBasicRemote().sendText(nickname2+":"+message);
            	}
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }
     
    /**
     * 发生错误时调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error){
        System.out.println("发生错误");
        error.printStackTrace();
    }
     
    /**
     * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message){
        for(String socketId: webSocketMap.keySet()){             
            try {
            	webSocketMap.get(socketId).session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("推送消息失败！！！");
                continue;
            }
        }
    }
    
    /**
     * 推送其它人的昵称给新增的用户
     * @param message
     * @throws IOException
     */
    public void sendNickname(String nickname){
        for(String socketId: webSocketMap.keySet()){             
            try {
            	if(!socketId.equals(nickname)){
            		webSocketMap.get(nickname).session.getBasicRemote().sendText("nickname:"+socketId);
            	}
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }
 
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }
 
    public static synchronized void addOnlineCount() {
    	testChatSingle.onlineCount++;
    }
     
    public static synchronized void subOnlineCount() {
    	testChatSingle.onlineCount--;
    }
}
