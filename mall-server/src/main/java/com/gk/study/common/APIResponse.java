package com.gk.study.common;

import java.io.Serializable;

import lombok.Data;

/**
 * API响应封装类
 * 
 * 该类用于统一封装后端API的响应数据，包含状态码、消息、数据体和追踪信息等。
 * 作为系统中所有API接口的标准返回格式，确保前后端交互的一致性和规范性。
 * 支持泛型，可以适应不同类型的业务数据返回需求。
 * 
 * @param <T> 响应数据的类型参数，允许返回任意类型的业务数据
 * @author Administrator
 * @date 2024-03-26
 */
@Data
public class APIResponse<T> implements Serializable {
    /**
     * 响应状态码
     * 用于标识API请求处理的结果状态，如成功、失败、未授权等
     */
    private int code;
    
    /**
     * 响应消息
     * 对响应结果的文字说明，通常用于描述成功信息或错误原因
     */
    private String msg;
    
    /**
     * 响应数据
     * 实际业务数据，可以是任意类型（由泛型T决定）
     */
    private T data;
    
    /**
     * 追踪信息
     * 用于记录异常堆栈或调试信息，方便问题排查
     */
    private String trace;
    
    /**
     * 时间戳
     * 记录响应生成的时间，格式为毫秒级时间戳
     */
    private long timestamp;

    /**
     * 默认构造函数
     * 
     * 创建一个只包含当前时间戳的响应对象
     */
    public APIResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 响应码构造函数
     * 
     * 根据预定义的响应码创建响应对象，包含状态码和默认消息
     * 
     * @param responseCode 预定义的响应码枚举对象
     */
    public APIResponse(ResponeCode responseCode) {
        this.timestamp = System.currentTimeMillis();
        this.code = responseCode.getCode();
        this.msg = responseCode.getMsg();
    }

    /**
     * 响应码和自定义消息构造函数
     * 
     * 根据预定义的响应码创建响应对象，但使用自定义消息覆盖默认消息
     * 
     * @param responseCode 预定义的响应码枚举对象
     * @param msg 自定义响应消息
     */
    public APIResponse(ResponeCode responseCode, String msg) {
        this(responseCode);
        this.msg = msg;
    }

    /**
     * 响应码和数据构造函数
     * 
     * 根据预定义的响应码创建响应对象，并包含响应数据
     * 
     * @param responseCode 预定义的响应码枚举对象
     * @param data 响应的业务数据
     */
    public APIResponse(ResponeCode responseCode, T data) {
        this(responseCode);
        this.data = data;
    }

    /**
     * 完整构造函数
     * 
     * 根据预定义的响应码创建响应对象，包含自定义消息和响应数据
     * 注意：此构造函数将自定义消息设置为trace字段而非msg字段
     * 
     * @param responseCode 预定义的响应码枚举对象
     * @param msg 自定义消息，将被设置为trace字段
     * @param data 响应的业务数据
     */
    public APIResponse(ResponeCode responseCode, String msg, T data) {
        this(responseCode);
        this.trace = msg;
        this.data = data;
    }

    /**
     * 重写toString方法
     * 
     * 提供对象的字符串表示，用于日志记录和调试
     * 包含时间戳、状态码、消息、追踪信息和数据等主要字段
     * 
     * @return 对象的字符串表示
     */
    @Override
    public String toString() {
        return "APIResponse{" +
                "timestamp=" + timestamp +
                ", code=" + code +
                ", msg='" + msg + '\'' +
                ", trace='" + trace + '\'' +
                ", data=" + data +
                //", count=" + count +
                '}';
    }
}
