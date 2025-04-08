package com.gk.study.permission;

import java.lang.annotation.*;

/**
 * 访问权限注解
 *
 * 该注解用于标记需要权限控制的方法。
 * 主要功能：
 * 1. 定义方法的访问权限级别
 * 2. 支持运行时权限检查
 * 3. 支持注解继承
 * 4. 支持文档生成
 *
 * 使用方式：
 * 1. 在需要权限控制的方法上添加@Access注解
 * 2. 通过level属性指定所需的权限级别
 * 3. 默认权限级别为LOGIN（需要登录）
 *
 * 示例：
 * {@code
 * @Access(level = AccessLevel.ADMIN)
 * public void adminMethod() {
 *     // 需要管理员权限的方法
 * }
 * }
 *
 * 注意：
 * 1. 该注解仅能用于方法级别
 * 2. 权限检查由AccessInterceptor实现
 * 3. 支持运行时权限验证
 * 4. 注解信息会被包含在JavaDoc中
 *
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 * @see AccessLevel
 * @see AccessInterceptor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented     // 在生成javac时显示该注解的信息
@Inherited
public @interface Access {
    /**
     * 访问权限级别
     *
     * 定义方法所需的访问权限级别。
     * 可选值：
     * 1. ADMIN - 需要管理员权限
     * 2. LOGIN - 需要登录权限
     * 3. ALL - 无需权限
     *
     * @return AccessLevel 访问权限级别，默认为LOGIN
     */
    AccessLevel level() default AccessLevel.LOGIN; //默认为ALL
}
