package com.gk.study.service;

import com.gk.study.entity.OpLog;
import java.util.List;

/**
 * 操作日志服务接口
 *
 * 该接口定义了系统操作日志相关的所有业务操作。
 * 主要功能包括：
 * 1. 操作日志的创建、删除和更新
 * 2. 操作日志列表的获取
 * 3. 登录日志的查询
 *
 * 操作日志属性说明：
 * 1. 操作类型：系统操作、用户操作等
 * 2. 操作内容：具体的操作描述
 * 3. 操作时间：操作发生的时间
 * 4. 操作人：执行操作的用户
 * 5. 操作IP：操作来源的IP地址
 *
 * 注意：
 * 1. 操作日志需要及时记录
 * 2. 操作日志需要定期清理
 * 3. 操作日志需要分类管理
 * 4. 操作日志需要支持搜索
 *
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 * @see OpLog
 */
public interface OpLogService {
    
    /**
     * 获取操作日志列表
     *
     * 查询系统中的所有操作日志记录。
     * 返回结果按操作时间倒序排列。
     *
     * @return List<OpLog> 操作日志列表，按时间倒序排列
     */
    List<OpLog> getOpLogList();
    
    /**
     * 创建操作日志
     *
     * 创建新的操作日志记录。
     * 创建过程包括：
     * 1. 记录操作类型
     * 2. 记录操作内容
     * 3. 记录操作人信息
     * 4. 记录操作时间
     *
     * @param opLog 操作日志对象，包含操作详细信息
     * @throws IllegalArgumentException 当操作日志信息不完整时抛出
     */
    void createOpLog(OpLog opLog);
    
    /**
     * 删除操作日志
     *
     * 删除指定的操作日志记录。
     * 删除前需要检查：
     * 1. 操作日志是否存在
     * 2. 操作日志是否允许删除
     *
     * @param id 操作日志ID
     * @throws IllegalArgumentException 当操作日志ID为空时抛出
     * @throws IllegalStateException 当操作日志不允许删除时抛出
     */
    void deleteOpLog(String id);

    /**
     * 更新操作日志
     *
     * 更新操作日志信息。
     * 更新内容包括：
     * 1. 操作状态
     * 2. 操作备注
     * 3. 处理时间
     *
     * @param opLog 操作日志对象，包含更新信息
     * @throws IllegalArgumentException 当更新信息无效时抛出
     */
    void updateOpLog(OpLog opLog);

    /**
     * 获取登录日志列表
     *
     * 查询系统中的所有登录日志记录。
     * 登录日志包括：
     * 1. 用户登录
     * 2. 用户登出
     * 3. 登录失败
     *
     * @return List<OpLog> 登录日志列表，按时间倒序排列
     */
    List<OpLog> getLoginLogList();
}
