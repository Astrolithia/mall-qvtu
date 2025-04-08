package com.gk.study.handler;

import com.gk.study.common.APIResponse;
import com.gk.study.service.ErrorLogService;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.ErrorLog;
import com.gk.study.utils.HttpContextUtils;
import com.gk.study.utils.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;

/**
 * 全局异常处理器
 *
 * 该处理器用于捕获和处理系统中所有未处理的异常。
 * 主要功能包括：
 * 1. 统一异常处理
 * 2. 异常日志记录
 * 3. 错误信息返回
 * 
 * 处理流程：
 * 1. 捕获异常
 * 2. 记录错误日志
 * 3. 保存异常信息
 * 4. 返回统一错误响应
 * 
 * 注意：
 * 1. 使用@RestControllerAdvice注解实现全局异常处理
 * 2. 使用@ExceptionHandler注解处理特定类型的异常
 * 3. 异常信息包含请求参数、IP地址等上下文信息
 * 4. 所有异常都会被记录到数据库
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
@RestControllerAdvice
public class CustomExceptionHandler {

    /**
     * 日志记录器
     * 用于记录异常信息和处理过程
     */
    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    /**
     * 错误日志服务接口
     * 用于将异常信息保存到数据库
     */
    @Autowired
    private ErrorLogService service;

    /**
     * 全局异常处理方法
     * 
     * 该方法捕获所有未处理的异常，记录错误日志并返回统一的错误响应。
     * 使用@ExceptionHandler注解指定处理Exception类型的异常。
     *
     * @param ex 捕获到的异常对象
     * @return APIResponse 包含错误信息的响应对象
     */
    @ExceptionHandler(Exception.class)
    public APIResponse handleException(Exception ex){
        // 记录错误日志
        logger.error("error log======>" + ex.getMessage(), ex);

        // 保存异常信息到数据库
        saveLog(ex);
        
        // 返回错误响应
        return new APIResponse(ResponeCode.FAIL, ex.getMessage());
    }

    /**
     * 保存异常日志
     * 
     * 该方法收集异常相关的上下文信息，包括：
     * 1. 请求IP地址
     * 2. 请求URL
     * 3. 请求方法
     * 4. 请求参数
     * 5. 异常堆栈信息
     * 6. 发生时间
     * 
     * 然后将这些信息保存到数据库。
     *
     * @param ex 要保存的异常对象
     */
    private void saveLog(Exception ex){
        // 创建错误日志对象
        ErrorLog log = new ErrorLog();
        
        // 获取当前HTTP请求
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        
        // 设置请求相关信息
        log.setIp(IpUtils.getIpAddr(request));
        log.setUrl(request.getRequestURI());
        log.setMethod(request.getMethod());
        
        // 获取并设置请求参数
        Map<String, String> params = HttpContextUtils.getParameterMap(request);
        if(!params.isEmpty()){
            // 参数不为空时，可以记录请求参数
            // log.setRequestParams(JsonUtils.toJsonString(params));
        }

        // 设置异常信息
        log.setContent(Arrays.toString(ex.getStackTrace()));
        log.setLogTime(String.valueOf(System.currentTimeMillis()));

        // 保存错误日志到数据库
        service.createErrorLog(log);
    }
}
