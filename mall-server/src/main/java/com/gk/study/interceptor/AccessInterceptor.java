package com.gk.study.interceptor;

import com.google.gson.Gson;
import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.OpLog;
import com.gk.study.entity.User;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.OpLogService;
import com.gk.study.service.UserService;
import com.gk.study.utils.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 访问拦截器
 *
 * 该拦截器用于处理请求访问控制和操作日志记录。
 * 主要功能包括：
 * 1. 接口权限验证
 * 2. 操作日志记录
 * 
 * 权限验证流程：
 * 1. 检查方法是否包含@Access注解
 * 2. 根据注解级别验证用户权限
 * 3. 验证通过则放行，否则返回错误响应
 * 
 * 日志记录内容：
 * 1. 请求IP地址
 * 2. 请求方法
 * 3. 请求URL
 * 4. 用户代理信息
 * 5. 访问时间
 * 6. 请求处理时间
 * 
 * 注意：
 * 1. 使用@Component注解实现自动注入
 * 2. 继承HandlerInterceptorAdapter实现拦截器功能
 * 3. 支持管理员和普通用户两种权限级别
 * 4. 使用静态服务实例解决自动注入时序问题
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
@Component
public class AccessInterceptor extends HandlerInterceptorAdapter {

    /**
     * 日志记录器
     * 用于记录拦截器操作和异常信息
     */
    private Logger logger = LoggerFactory.getLogger(AccessInterceptor.class);

    /**
     * 操作日志服务接口
     * 用于记录用户操作日志
     */
    private static OpLogService service;

    /**
     * 用户服务接口
     * 用于验证用户权限
     */
    private static UserService userService;

    /**
     * 设置操作日志服务
     * 
     * 使用静态方法注入服务，解决@Component和@Autowired时序问题
     *
     * @param service 操作日志服务实例
     */
    @Autowired
    public void setOpLogService(OpLogService service) {
        AccessInterceptor.service = service;
    }

    /**
     * 设置用户服务
     * 
     * 使用静态方法注入服务，解决@Component和@Autowired时序问题
     *
     * @param userService 用户服务实例
     */
    @Autowired
    public void setUserService(UserService userService) {
        AccessInterceptor.userService = userService;
    }

    /**
     * 请求预处理方法
     * 
     * 在请求处理之前执行，主要用于权限验证。
     * 验证流程：
     * 1. 记录请求开始时间
     * 2. 检查处理器类型
     * 3. 获取方法注解
     * 4. 根据注解级别验证权限
     *
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param handler 处理器对象
     * @return boolean 是否继续处理请求
     * @throws Exception 可能抛出的异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 记录请求开始时间
        request.setAttribute("_startTime", System.currentTimeMillis());

        // 检查处理器类型
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 获取方法注解
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        Access access = method.getAnnotation(Access.class);

        // 如果方法没有@Access注解，直接放行
        if (access == null) {
            return true;
        }

        // 验证管理员权限
        if(access.level().getCode() == AccessLevel.ADMIN.getCode()) {
            String token = request.getHeader("ADMINTOKEN");
            logger.info("token==>" + token);
            User user = userService.getUserByToken(token);
            if(user != null && user.getRole().equals(String.valueOf(User.AdminUser))){
                return true;
            }else {
                APIResponse apiResponse = new APIResponse(ResponeCode.FAIL, "无操作权限");
                writeResponse(response, apiResponse);
                return false;
            }
        }

        // 验证用户权限
        if(access.level().getCode() == AccessLevel.LOGIN.getCode()) {
            String token = request.getHeader("TOKEN");
            logger.info("token==>" + token);
            User user = userService.getUserByToken(token);
            if(user != null && user.getRole().equals(String.valueOf(User.NormalUser))){
                return true;
            }else {
                APIResponse apiResponse = new APIResponse(ResponeCode.FAIL, "未登录");
                writeResponse(response, apiResponse);
                return false;
            }
        }

        return true;
    }

    /**
     * 请求后处理方法
     * 
     * 在请求处理之后，视图渲染之前执行。
     * 当前未实现具体功能，保留用于扩展。
     *
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param handler 处理器对象
     * @param modelAndView 模型和视图对象
     * @throws Exception 可能抛出的异常
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 预留扩展点
    }

    /**
     * 请求完成处理方法
     * 
     * 在请求处理完成之后执行，主要用于记录操作日志。
     * 记录内容包括：
     * 1. 请求处理时间
     * 2. 请求IP地址
     * 3. 请求方法
     * 4. 请求URL
     * 5. 用户代理信息
     * 6. 访问时间
     *
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param handler 处理器对象
     * @param ex 可能抛出的异常
     * @throws Exception 可能抛出的异常
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 计算请求处理时间
        Long endTime = System.currentTimeMillis();
        Long startTime = (Long) request.getAttribute("_startTime");
        Long diff = (endTime - startTime);

        // 创建操作日志对象
        OpLog opLog = new OpLog();
        
        // 设置请求相关信息
        opLog.setReIp(IpUtils.getIpAddr(request));
        opLog.setReMethod(request.getMethod());
        opLog.setReUrl(request.getRequestURI());
        opLog.setReUa(request.getHeader(HttpHeaders.USER_AGENT));
        
        // 设置时间信息
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        opLog.setReTime(formatter.format(new Date()));
        opLog.setAccessTime(String.valueOf(diff));
        
        // 保存操作日志
        service.createOpLog(opLog);
    }

    /**
     * 写入响应信息
     * 
     * 将API响应对象转换为JSON格式并写入HTTP响应。
     * 设置必要的响应头信息，包括：
     * 1. 跨域访问控制
     * 2. 缓存控制
     * 3. 字符编码
     * 4. 内容类型
     *
     * @param response HTTP响应对象
     * @param apiResponse API响应对象
     * @throws IOException 当写入响应时发生IO异常
     */
    public void writeResponse(HttpServletResponse response, APIResponse apiResponse) throws IOException {
        // 设置响应头
        response.setStatus(200);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        
        // 将响应对象转换为JSON并写入响应
        Gson gson = new Gson();
        String jsonStr = gson.toJson(apiResponse);
        response.getWriter().println(jsonStr);
        response.getWriter().flush();
    }
}
