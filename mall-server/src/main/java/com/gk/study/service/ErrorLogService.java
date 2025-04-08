package com.gk.study.service;

import com.gk.study.entity.ErrorLog;
import java.util.List;

/**
 * 错误日志服务接口
 *
 * 该接口定义了系统错误日志相关的所有业务操作。
 * 主要功能包括：
 * 1. 错误日志的创建、删除和更新
 * 2. 错误日志列表的获取
 *
 * 错误日志属性说明：
 * 1. 错误类型：系统错误、业务错误等
 * 2. 错误信息：详细的错误描述
 * 3. 错误堆栈：异常堆栈信息
 * 4. 发生时间：错误发生的时间
 * 5. 处理状态：已处理、未处理等
 *
 * 注意：
 * 1. 错误日志需要及时记录
 * 2. 错误日志需要定期清理
 * 3. 错误日志需要分类管理
 * 4. 错误日志需要支持搜索
 *
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 * @see ErrorLog
 */
public interface ErrorLogService {
    
    /**
     * 获取错误日志列表
     *
     * 查询系统中的所有错误日志记录。
     * 返回结果按发生时间倒序排列。
     *
     * @return List<ErrorLog> 错误日志列表，按时间倒序排列
     */
    List<ErrorLog> getErrorLogList();
    
    /**
     * 创建错误日志
     *
     * 创建新的错误日志记录。
     * 创建过程包括：
     * 1. 记录错误类型
     * 2. 记录错误信息
     * 3. 记录错误堆栈
     * 4. 设置处理状态
     *
     * @param errorLog 错误日志对象，包含错误详细信息
     * @throws IllegalArgumentException 当错误日志信息不完整时抛出
     */
    void createErrorLog(ErrorLog errorLog);
    
    /**
     * 删除错误日志
     *
     * 删除指定的错误日志记录。
     * 删除前需要检查：
     * 1. 错误日志是否存在
     * 2. 错误日志是否已处理
     *
     * @param id 错误日志ID
     * @throws IllegalStateException 当错误日志不允许删除时抛出
     */
    void deleteErrorLog(String id);

    /**
     * 更新错误日志
     *
     * 更新错误日志信息。
     * 更新内容包括：
     * 1. 处理状态
     * 2. 处理备注
     * 3. 处理时间
     *
     * @param errorLog 错误日志对象，包含更新信息
     * @throws IllegalArgumentException 当更新信息无效时抛出
     */
    void updateErrorLog(ErrorLog errorLog);
}
