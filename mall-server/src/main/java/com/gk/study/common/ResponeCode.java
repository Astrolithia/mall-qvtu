package com.gk.study.common;

/**
 * API响应状态码枚举
 * 
 * 定义系统中所有API接口响应的标准状态码和对应描述信息。
 * 作为统一响应格式的一部分，用于前端快速判断请求处理结果。
 * 遵循HTTP标准状态码设计，包含常见的成功、失败、未授权等状态。
 * 
 * @author Administrator
 * @date 2024-03-26
 */
public enum ResponeCode {
    /**
     * 请求成功
     * 
     * 状态码: 200
     * 标准HTTP成功状态，表示请求已被成功处理并返回预期结果
     */
    SUCCESS(200, "成功"),
    
    /**
     * 请求失败
     * 
     * 状态码: 400
     * 表示由于客户端原因导致请求无法被处理，如参数错误、验证失败等
     */
    FAIL(400, "失败"),
    
    /**
     * 未授权
     * 
     * 状态码: 401
     * 表示用户未登录或token无效，需要进行身份认证
     */
    UNAUTHORIZED(401, "未认证"),
    
    /**
     * 资源不存在
     * 
     * 状态码: 404
     * 表示请求的API接口或资源不存在
     */
    NOT_FOUND(404, "接口不存在"),
    
    /**
     * 服务器错误
     * 
     * 状态码: 500
     * 表示服务器内部发生错误，无法完成请求处理
     */
    INTERNAL_SERVER_ERROR(500, "服务器内部错误");

    /**
     * 状态码
     * 
     * 数值型响应状态码，与HTTP标准状态码保持一致
     */
    private int code;
    
    /**
     * 状态描述
     * 
     * 对应状态码的文字说明，用于前端展示或日志记录
     */
    private String msg;

    /**
     * 构造函数
     * 
     * 创建响应码枚举实例，设置状态码和对应描述信息
     * 
     * @param code 状态码数值
     * @param msg 状态描述文字
     */
    private ResponeCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 获取状态码
     * 
     * @return 返回当前枚举实例的状态码数值
     */
    public int getCode() {
        return this.code;
    }

    /**
     * 获取状态描述
     * 
     * @return 返回当前枚举实例的状态描述文字
     */
    public String getMsg() {
        return this.msg;
    }
}
