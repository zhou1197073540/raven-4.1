package com.raven.iirinstitute.exception;

import com.raven.iirinstitute.consts.Const;
import com.raven.iirinstitute.dto.RespDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zhouzhenyang on 2017/6/5.
 */
@ControllerAdvice
public class GlobalDefaultExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);

    @ExceptionHandler(value = Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public RespDto defaultErrorHandler(HttpServletRequest request, Exception e) throws Exception {

        logger.error("ERROR!!!", e);
        return new RespDto(Const.INTERNAL_ERROR, Const.INTERNAL_ERROR_MSG);
    }
}
