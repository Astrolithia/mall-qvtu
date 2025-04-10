package com.gk.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 应用程序入口类
 * 
 * 该类是整个应用程序的启动入口点，通过 @SpringBootApplication 注解启用 Spring Boot 的自动配置机制。
 * @SpringBootApplication 注解相当于同时使用了 @Configuration、@EnableAutoConfiguration 和 @ComponentScan。
 * 
 * 应用启动时会自动扫描同包及子包下的所有组件，并加载必要的 Bean 到 Spring 容器中。
 * 应用启动过程包括初始化 Spring 容器、加载配置、启动内嵌 Web 服务器等步骤。
 * 
 * 本应用是一个电商平台服务，提供商品管理、订单处理、用户认证等功能的 REST API。
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */
@SpringBootApplication
public class MySpringApplication {

    /**
     * 应用程序主入口方法
     * 
     * 负责启动 Spring Boot 应用程序，初始化 Spring 容器，加载所有配置。
     * 该方法通过调用 SpringApplication.run 创建应用上下文，完成 Bean 的初始化和依赖注入。
     * 启动过程中会根据 application.yml/properties 配置文件加载相应的配置项。
     * 
     * @param args 命令行参数，可在启动时传入配置参数覆盖配置文件中的设置
     */
    public static void main(String[] args) {
        SpringApplication.run(MySpringApplication.class, args);
    }

}
