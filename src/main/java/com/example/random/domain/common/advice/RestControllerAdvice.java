package com.example.random.domain.common.advice;

import com.example.random.domain.common.exception.NewException;
import com.example.random.domain.output.JsonResponse;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class RestControllerAdvice {
    private static final Logger logger = LoggerFactory.getLogger(RestControllerAdvice.class);

    @ExceptionHandler(NewException.class)
    @ResponseBody
    public JsonResponse<?> handleBusinessException(NewException ex, HttpServletResponse response) {
        logger.error(ex.getMessage(), ex);
        return new JsonResponse<>(ex.getCode(), ex.getMessage(), ex.getData());
    }
}
