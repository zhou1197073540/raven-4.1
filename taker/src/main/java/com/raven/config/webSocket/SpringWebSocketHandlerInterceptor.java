package com.raven.config.webSocket;

import com.raven.consts.Const;
import com.raven.utils.TokenUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

/**
 * Created by zhouzhenyang on 2017/6/6.
 */
@Component
public class SpringWebSocketHandlerInterceptor extends HttpSessionHandshakeInterceptor {

    @Autowired
    TokenUtil tokenUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
//            System.out.println("got ya!");
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            String token = servletRequest.getServletRequest().getHeader(Const.SUB_PROTOCOL_TOKEN);
            if (!StringUtils.isEmpty(token)) {
                String uid = tokenUtil.getProperty(token, Const.UID);
                if (uid == null) {
                    return false;
                }
                ServletServerHttpResponse serverResponse = (ServletServerHttpResponse) response;
                //chrome检查严格，（我们protocol使用不按常理出牌）
                serverResponse.getHeaders().set(Const.SUB_PROTOCOL_TOKEN, token);
                attributes.put(Const.UID, uid);
            } else {
                attributes.put(Const.VISITOR, "isVisitor");
            }
        }
        return super.beforeHandshake(request, response, wsHandler, attributes);

    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception ex) {
        super.afterHandshake(request, response, wsHandler, ex);
    }
}
