package com.gk.study.config;

import com.gk.study.interceptor.AccessInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 *  * @author Administrator
 *  * @date 2024-03-26
 */
@Configuration
public class MyConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")//项目中的所有接口都支持跨域
                .allowedOriginPatterns("*") //所有地址都可以访问，也可以配置具体地址
                .allowCredentials(true)
                .allowedMethods("*");//"GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS"
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //添加资源映射规则，默认的前缀是：http://localhost:port
        registry.addResourceHandler("/staticfiles/image/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + File.separator + "upload" + File.separator + "image" + File.separator);
        registry.addResourceHandler("/staticfiles/avatar/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + File.separator + "upload" + File.separator + "avatar" + File.separator);
        registry.addResourceHandler("/staticfiles/banner/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + File.separator + "upload" + File.separator + "banner" + File.separator);
        // 富文本编辑器的资源映射
        registry.addResourceHandler("/staticfiles/editor/images/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + File.separator + "upload" + File.separator + "editor" + File.separator + "images" + File.separator);
        registry.addResourceHandler("/staticfiles/editor/videos/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + File.separator + "upload" + File.separator + "editor" + File.separator + "videos" + File.separator);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 自定义拦截器
        registry.addInterceptor(new AccessInterceptor());
    }
}
