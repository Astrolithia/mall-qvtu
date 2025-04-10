package com.gk.study.config;

import com.gk.study.interceptor.AccessInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Spring MVC 全局配置类
 * 
 * 该类负责配置 Spring MVC 相关功能，包括跨域请求处理、静态资源映射和拦截器注册。
 * 通过实现 WebMvcConfigurer 接口，自定义 Web 应用的行为和特性。
 * 使用 @Configuration 注解标记为配置类，会被 Spring 容器自动扫描并应用。
 *
 * 主要功能：
 * 1. 配置全局跨域资源共享(CORS)策略，允许前端访问后端API
 * 2. 配置静态资源处理器，实现上传文件的访问路径映射
 * 3. 注册自定义拦截器，实现请求预处理和权限控制
 * 
 * @author Administrator
 * @date 2024-03-26
 */
@Configuration
public class MyConfig implements WebMvcConfigurer {
    
    /**
     * 配置跨域资源共享(CORS)
     * 
     * 解决前后端分离架构中的跨域问题，允许来自不同源的HTTP请求访问API。
     * 设置允许的请求方法、请求头和是否允许携带认证信息等。
     * 
     * @param registry CORS注册表，用于配置跨域规则
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 对所有接口路径应用CORS配置
        registry.addMapping("/**")
                // 允许所有来源访问，支持通配符匹配
                .allowedOriginPatterns("*") 
                // 允许请求携带认证信息(如cookies)
                .allowCredentials(true)
                // 允许所有HTTP方法(GET、POST、PUT、DELETE等)
                .allowedMethods("*");
    }

    /**
     * 配置静态资源映射
     * 
     * 将文件系统中的上传文件目录映射到可通过HTTP访问的URL路径。
     * 支持图片、头像、banner和富文本编辑器资源等不同类型的静态文件访问。
     * 
     * @param registry 资源处理器注册表，用于配置资源映射规则
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 上传图片的访问路径映射
        registry.addResourceHandler("/staticfiles/image/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + File.separator + "upload" + File.separator + "image" + File.separator);
        
        // 用户头像的访问路径映射
        registry.addResourceHandler("/staticfiles/avatar/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + File.separator + "upload" + File.separator + "avatar" + File.separator);
        
        // 轮播图banner的访问路径映射
        registry.addResourceHandler("/staticfiles/banner/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + File.separator + "upload" + File.separator + "banner" + File.separator);
        
        // 富文本编辑器上传图片的访问路径映射
        registry.addResourceHandler("/staticfiles/editor/images/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + File.separator + "upload" + File.separator + "editor" + File.separator + "images" + File.separator);
        
        // 富文本编辑器上传视频的访问路径映射
        registry.addResourceHandler("/staticfiles/editor/videos/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + File.separator + "upload" + File.separator + "editor" + File.separator + "videos" + File.separator);
    }

    /**
     * 配置请求拦截器
     * 
     * 注册自定义拦截器，用于实现请求预处理、用户认证、权限控制等功能。
     * 拦截器会按照注册顺序依次处理匹配到的请求。
     * 
     * @param registry 拦截器注册表，用于配置拦截器链
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册访问控制拦截器，用于权限验证和访问控制
        registry.addInterceptor(new AccessInterceptor());
    }
}
