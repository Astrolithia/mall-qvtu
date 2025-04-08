package com.gk.study.permission;

/**
 * 访问权限级别枚举
 *
 * 该枚举定义了系统中不同用户的访问权限级别。
 * 权限级别从低到高依次为：
 * 1. LOGIN - 普通登录用户
 * 2. DEMO - 演示账号
 * 3. ADMIN - 管理员
 * 4. SUPER - 超级管理员
 *
 * 注意：
 * 1. 权限级别必须与user表的role字段对应
 * 2. 权限级别代码(code)必须唯一
 * 3. 权限级别描述(msg)用于显示和日志记录
 * 4. 权限检查时使用code进行比较
 *
 * 使用示例：
 * {@code
 * if (user.getRole().equals(AccessLevel.ADMIN.getMsg())) {
 *     // 管理员权限操作
 * }
 * }
 *
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 * @see Access
 */
public enum AccessLevel {

    /**
     * 登录用户权限
     * 最基本的权限级别，仅需要登录即可访问
     */
    LOGIN(1, "all"),

    /**
     * 演示账号权限
     * 用于演示系统的账号，权限高于普通用户
     */
    DEMO(2, "demo"),

    /**
     * 管理员权限
     * 系统管理员权限，可以进行系统管理操作
     */
    ADMIN(3, "admin"),

    /**
     * 超级管理员权限
     * 最高权限级别，可以进行所有操作
     */
    SUPER(4, "super");

    /**
     * 权限级别代码
     * 用于权限比较和存储
     */
    private final int code;

    /**
     * 权限级别描述
     * 用于显示和日志记录
     */
    private final String msg;

    /**
     * 构造函数
     *
     * @param code 权限级别代码
     * @param msg 权限级别描述
     */
    AccessLevel(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 获取权限级别代码
     *
     * @return int 权限级别代码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取权限级别描述
     *
     * @return String 权限级别描述
     */
    public String getMsg() {
        return msg;
    }
}
