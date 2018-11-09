package com.riddler.guide.socket;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.riddler.guide.common.Constant;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;



public class HandShakeInterceptor implements HandshakeInterceptor {

	@Override
	public boolean beforeHandshake(ServerHttpRequest request,ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		if (request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest httpRequest =(ServletServerHttpRequest)request;
//			HttpServletRequest httpservlet=httpRequest.getServletRequest();
			String uid=httpRequest.getServletRequest().getParameter("uid");
			if(!StringUtils.isEmpty(uid)){
				attributes.put(Constant.SOCKET_USER, uid);
			}else{
				attributes.put(Constant.SOCKET_VISITOR, "is_visitor");
			}
        }
        return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request,
			ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		
	}

}
