package com.gk.study.utils;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP上下文工具类
 *
 * 该工具类提供了获取HTTP请求上下文信息的功能。
 * 主要功能包括：
 * 1. 获取当前HTTP请求对象
 * 2. 获取请求参数映射
 * 3. 获取请求域名
 * 4. 获取请求来源
 *
 * 使用场景：
 * 1. 在非Controller层获取请求信息
 * 2. 记录请求日志
 * 3. 处理跨域请求
 *
 * 注意：
 * 1. 需要在Web环境中使用
 * 2. 获取请求对象可能返回null
 * 3. 参数值可能为null或空字符串
 *
 * @author Administrator
 * @version 1.0
 */
public class HttpContextUtils {

    /**
     * 获取当前HTTP请求对象
     *
     * 从Spring的RequestContextHolder中获取当前请求对象。
     * 主要用于在非Controller层获取请求信息。
     *
     * @return HttpServletRequest 当前HTTP请求对象，如果没有请求上下文则返回null
     */
    public static HttpServletRequest getHttpServletRequest() {
        // 从RequestContextHolder中获取请求属性
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if(requestAttributes == null){
            return null;
        }

        // 将RequestAttributes转换为ServletRequestAttributes并获取请求对象
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    /**
     * 获取请求参数映射
     *
     * 将HTTP请求中的所有参数转换为Map格式。
     * 只包含非空的参数值。
     *
     * @param request HTTP请求对象
     * @return Map<String, String> 参数名和参数值的映射
     */
    public static Map<String, String> getParameterMap(HttpServletRequest request) {
        // 获取所有参数名
        Enumeration<String> parameters = request.getParameterNames();

        // 创建参数映射
        Map<String, String> params = new HashMap<>();
        while (parameters.hasMoreElements()) {
            String parameter = parameters.nextElement();
            String value = request.getParameter(parameter);
            // 只添加非空的参数值
            if (StringUtils.isNotBlank(value)) {
                params.put(parameter, value);
            }
        }

        return params;
    }

    /**
     * 获取请求域名
     *
     * 从HTTP请求中提取完整的域名信息。
     * 包括协议、主机名和端口号。
     *
     * @return String 请求的完整域名
     */
    public static String getDomain(){
        // 获取当前请求对象
        HttpServletRequest request = getHttpServletRequest();
        // 获取完整的请求URL
        StringBuffer url = request.getRequestURL();
        // 删除URI部分，只保留域名
        return url.delete(url.length() - request.getRequestURI().length(), url.length()).toString();
    }

    /**
     * 获取请求来源
     *
     * 从HTTP请求头中获取Origin信息。
     * 主要用于处理跨域请求。
     *
     * @return String 请求来源的域名
     */
    public static String getOrigin(){
        // 获取当前请求对象
        HttpServletRequest request = getHttpServletRequest();
        // 从请求头中获取Origin
        return request.getHeader(HttpHeaders.ORIGIN);
    }
}
