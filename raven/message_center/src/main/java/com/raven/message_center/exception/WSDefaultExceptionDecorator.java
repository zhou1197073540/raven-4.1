package com.raven.message_center.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ExceptionWebSocketHandlerDecorator;


/**
 * Created by zhouzhenyang on 2017/6/26.
 */
public class WSDefaultExceptionDecorator extends ExceptionWebSocketHandlerDecorator {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionWebSocketHandlerDecorator.class);

    public WSDefaultExceptionDecorator(WebSocketHandler delegate) {
        super(delegate);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        super.afterConnectionEstablished(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        try {
            super.getDelegate().handleMessage(session, message);
        } catch (Throwable ex) {
            this.tryCloseWithError(session, ex, logger);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        try {
            super.getDelegate().handleTransportError(session, exception);
        } catch (Throwable ex) {
            this.tryCloseWithError(session, ex, logger);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        super.afterConnectionClosed(session, closeStatus);
    }

    public void tryCloseWithError(WebSocketSession session, Throwable exception, Logger logger) {

        String type;
        if (session.getAttributes().get("type") != null) {
            type = session.getAttributes().get("type").toString();
        } else {
            //todo 是否分析地址？
            type = "unknown";
        }
        logger.error("try close with error {} session error:", type, exception);
        logger.debug("Closing due to com.messageBoard.exception for " + session, exception);
        if (session.isOpen()) {
            try {
                session.close(CloseStatus.SERVER_ERROR);
            } catch (Throwable ex) {
                // ignore
            }
        }
    }

}
