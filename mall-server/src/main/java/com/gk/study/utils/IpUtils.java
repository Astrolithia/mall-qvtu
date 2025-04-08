package com.gk.study.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpServletRequest;

/**
 * IP地址工具类
 *
 * 该工具类提供了获取客户端真实IP地址的功能。
 * 主要功能包括：
 * 1. 从HTTP请求中获取客户端IP地址
 * 2. 支持多种代理服务器场景
 * 3. 处理多级代理的情况
 *
 * 获取IP地址的优先级：
 * 1. X-Forwarded-For
 * 2. Proxy-Client-IP
 * 3. WL-Proxy-Client-IP
 * 4. HTTP_CLIENT_IP
 * 5. HTTP_X_FORWARDED_FOR
 * 6. request.getRemoteAddr()
 *
 * 注意：
 * 1. 在使用Nginx等反向代理时，不能直接使用request.getRemoteAddr()
 * 2. X-Forwarded-For可能包含多个IP地址，需要取第一个非unknown的有效IP
 * 3. 需要处理各种异常情况
 *
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
public class IpUtils {
    
    /**
     * 日志记录器
     * 用于记录IP地址获取过程中的异常信息
     */
    private static Logger logger = LoggerFactory.getLogger(IpUtils.class);

    /**
     * 获取客户端真实IP地址
     *
     * 从HTTP请求中获取客户端的真实IP地址。
     * 支持多种代理服务器场景，包括：
     * 1. 直接访问
     * 2. 使用Nginx等反向代理
     * 3. 使用多级代理
     *
     * 获取过程：
     * 1. 依次尝试从不同的请求头中获取IP
     * 2. 如果获取到的IP为unknown或空，则尝试下一个来源
     * 3. 如果所有来源都获取失败，则使用request.getRemoteAddr()
     *
     * @param request HTTP请求对象
     * @return String 客户端真实IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        // 定义unknown常量，用于判断IP是否有效
        String unknown = "unknown";
        String ip = null;
        try {
            // 1. 尝试从X-Forwarded-For获取IP
            ip = request.getHeader("x-forwarded-for");
            if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
                // 2. 尝试从Proxy-Client-IP获取IP
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
                // 3. 尝试从WL-Proxy-Client-IP获取IP
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
                // 4. 尝试从HTTP_CLIENT_IP获取IP
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
                // 5. 尝试从HTTP_X_FORWARDED_FOR获取IP
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
                // 6. 如果以上都获取失败，使用request.getRemoteAddr()
                ip = request.getRemoteAddr();
            }
        } catch (Exception e) {
            // 记录获取IP地址过程中的异常
            logger.error("IPUtils ERROR ", e);
        }

        return ip;
    }
}
