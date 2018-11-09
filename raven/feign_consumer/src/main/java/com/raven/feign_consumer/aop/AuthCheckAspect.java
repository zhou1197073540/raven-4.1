package com.raven.feign_consumer.aop;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raven.feign_consumer.annotation.AuthCheck;
import com.raven.feign_consumer.consts.Auth;
import com.raven.feign_consumer.consts.Const;
import com.raven.feign_consumer.dto.RespDto;
import com.raven.feign_consumer.utils.TokenUtil;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Aspect
@Component
public class AuthCheckAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuthCheck.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    TokenUtil tokenUtil;

    @Around("@annotation(check)")
    public Object doAround(ProceedingJoinPoint joinPoint, AuthCheck check) {
        int[] auths = check.value();
        try {
            for (int auth : auths) {
                if (auth == Auth.NEED_LOGIN) {
                    RequestAttributes ra = RequestContextHolder.getRequestAttributes();
                    ServletRequestAttributes sra = (ServletRequestAttributes) ra;
                    HttpServletRequest request = sra.getRequest();
                    HttpServletResponse response = sra.getResponse();
                    if (request == null || response == null) {
                        return null;
                    }
                    String token = request.getHeader("Authorization");
                    if (StringUtils.isEmpty(token)) {
                        return new RespDto(Const.NOT_AUTHORIZED, "没有权限");
                    }
                    String[] ret = tokenUtil.renewToken(token);
                    if (ret == null) {
                        return new RespDto(Const.RE_LOGIN, "请重新登录");
                    } else if (tokenUtil.NEW_TOKEN.equals(ret[0])) {
                        response.setHeader("Authorization", ret[1]);
                    }
                }
            }
            return joinPoint.proceed();
        } catch (JsonProcessingException e) {
            return null;
        } catch (Throwable throwable) {
            return null;
        }
    }
}
