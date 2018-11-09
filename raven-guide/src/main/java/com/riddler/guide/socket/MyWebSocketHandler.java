package com.riddler.guide.socket;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.riddler.guide.bean.SocketMessage;
import com.riddler.guide.common.Constant;
import com.riddler.guide.service.SocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class MyWebSocketHandler implements WebSocketHandler {
	private static Logger logger = LoggerFactory
			.getLogger(MyWebSocketHandler.class);
	public static final Map<String, WebSocketSession> userSocketSessionMap;

	static {
		userSocketSessionMap = new ConcurrentHashMap<String, WebSocketSession>();
	}

	@Autowired
	private SocketService socketService;

	@Override
	public void afterConnectionEstablished(WebSocketSession session)
			throws Exception {
		saveUserInfo(session);// 存储链接用户
		// 链接成功，发送历史数据
		SocketMessage msg = new SocketMessage();
		msg.setType(Constant.HISTORY_MSG_TYPE);
		msg.setData(socketService.getHistoryNews());
		
		session.sendMessage(new TextMessage(JSON.toJSONString(msg)));
		logger.info(getUserId(session)
				+ " connect to the websocket success..... 在线人数："+userSocketSessionMap.size());
	}

	@Override
	public void handleMessage(WebSocketSession session,
			WebSocketMessage<?> message) throws Exception {
		socketService.handleUsrMessage(session,message);
	}

	@Override
	public void handleTransportError(WebSocketSession session,
			Throwable exception) throws Exception {
		if (session.isOpen()) {
			session.close();
		}
		logger.debug("websocket connection closed......");
		removeSession(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session,
			CloseStatus closeStatus) throws Exception {
		logger.debug("websocket connection closed......");
		removeSession(session);
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

	/**
	 * 给所有在线用户发送消息
	 *
	 * @param message
	 */
	public static void sendMessageToUsers(TextMessage message) {
		Iterator<Entry<String, WebSocketSession>> it = userSocketSessionMap
				.entrySet().iterator();
		ExecutorService server = Executors.newFixedThreadPool(10);
		while (it.hasNext()) {
			final Entry<String, WebSocketSession> entry = it.next();
			if (entry.getValue().isOpen()) {
				Runnable run = new Runnable() {
					@Override
					public void run() {
						if (entry.getValue().isOpen()) {
							try {
								entry.getValue().sendMessage(message);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				};
				server.execute(run);
			}
		}
        server.shutdown();
	}

	/**
	 * 给某个用户发送消息
	 *
	 * @param userName
	 * @param message
	 * @throws IOException
	 */
	public static void sendMessageToUser(String userName, TextMessage message)
			throws IOException {
		WebSocketSession session = userSocketSessionMap.get(userName);
		if (session != null && session.isOpen()) {
			session.sendMessage(message);
		}
	}

	private void saveUserInfo(WebSocketSession session) {
		// 记录登录用户
		String user = (String) session.getAttributes()
				.get(Constant.SOCKET_USER);
		if (!StringUtils.isEmpty(user)) {
			user = user + Constant.SOCKET_USER;
			if (userSocketSessionMap.get(user) == null) {
				userSocketSessionMap.put(user, session);
			}
		} else {
			// 记录游客用户
			String visitor = (String) session.getAttributes().get(
					Constant.SOCKET_VISITOR);
			if (!StringUtils.isEmpty(visitor)) {
				String visitorID = session.getId() + Constant.SOCKET_VISITOR;
				if (userSocketSessionMap.get(visitorID) == null) {
					userSocketSessionMap.put(visitorID, session);
				}
			}
		}
	}

	private void removeSession(WebSocketSession session) {
		String user = (String) session.getAttributes()
				.get(Constant.SOCKET_USER);
		if (!StringUtils.isEmpty(user)) {
			userSocketSessionMap.remove(user + Constant.SOCKET_USER);
		} else {
			String visitor = (String) session.getAttributes().get(
					Constant.SOCKET_VISITOR);
			if (!StringUtils.isEmpty(visitor)) {
				userSocketSessionMap.remove(session.getId()
						+ Constant.SOCKET_VISITOR);
			}
		}
	}

	public String getUserId(WebSocketSession session) {
		String user = (String) session.getAttributes()
				.get(Constant.SOCKET_USER);
		if (!StringUtils.isEmpty(user)) {
			return user + Constant.SOCKET_USER;
		} else {
			String visitor = (String) session.getAttributes().get(
					Constant.SOCKET_VISITOR);
			if (!StringUtils.isEmpty(visitor)) {
				return session.getId() + Constant.SOCKET_VISITOR;
			}
		}
		return null;
	}
}
