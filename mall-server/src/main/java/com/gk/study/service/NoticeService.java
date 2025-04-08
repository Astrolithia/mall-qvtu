package com.gk.study.service;
/**
 *  * @author Administrator
 *  * @date 2024-03-26
 */
import com.gk.study.entity.Notice;

import java.util.List;

/**
 * 通知服务接口
 *
 * 该接口定义了系统通知相关的所有业务操作。
 * 主要功能包括：
 * 1. 通知的创建、删除和更新
 * 2. 通知列表的获取
 * 3. 通知状态的维护
 *
 * 通知属性说明：
 * 1. 通知标题：通知的简要描述
 * 2. 通知内容：通知的详细内容
 * 3. 通知类型：系统通知、活动通知等
 * 4. 发布时间：通知的发布时间
 * 5. 发布状态：已发布、未发布等
 *
 * 注意：
 * 1. 通知内容需要支持富文本
 * 2. 通知发布需要审核
 * 3. 通知需要支持定时发布
 * 4. 通知列表需要支持分页查询
 *
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 * @see Notice
 */
public interface NoticeService {
    
    /**
     * 获取通知列表
     *
     * 查询系统中的所有通知记录。
     * 返回结果按发布时间倒序排列。
     * 只返回已发布的通知。
     *
     * @return List<Notice> 通知列表，按时间倒序排列
     */
    List<Notice> getNoticeList();
    
    /**
     * 创建通知
     *
     * 创建新的通知记录。
     * 创建过程包括：
     * 1. 验证通知信息完整性
     * 2. 设置初始发布状态
     * 3. 保存通知信息
     *
     * @param notice 通知对象，包含通知详细信息
     * @throws IllegalArgumentException 当通知信息不完整时抛出
     */
    void createNotice(Notice notice);
    
    /**
     * 删除通知
     *
     * 删除指定的通知记录。
     * 删除前需要检查：
     * 1. 通知是否存在
     * 2. 通知是否已发布
     *
     * @param id 通知ID
     * @throws IllegalArgumentException 当通知ID为空时抛出
     * @throws IllegalStateException 当通知不允许删除时抛出
     */
    void deleteNotice(String id);

    /**
     * 更新通知
     *
     * 更新通知信息。
     * 更新内容包括：
     * 1. 通知标题
     * 2. 通知内容
     * 3. 通知类型
     * 4. 发布状态
     *
     * @param notice 通知对象，包含更新信息
     * @throws IllegalArgumentException 当更新信息无效时抛出
     */
    void updateNotice(Notice notice);
}
