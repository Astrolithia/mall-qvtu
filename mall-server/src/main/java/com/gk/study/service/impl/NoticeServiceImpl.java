package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Notice;
import com.gk.study.mapper.NoticeMapper;
import com.gk.study.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公告服务实现类
 * 
 * 该类实现了公告相关的业务逻辑，包括：
 * 1. 公告列表的获取
 * 2. 公告的创建、删除和更新
 * 3. 公告的状态管理
 * 
 * 使用MyBatis-Plus的ServiceImpl作为基类，简化了基础CRUD操作。
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {
    
    /**
     * 公告数据访问对象
     * 用于执行公告相关的数据库操作
     */
    @Autowired
    NoticeMapper mapper;

    /**
     * 获取公告列表
     * 
     * 查询所有公告记录，不添加任何筛选条件。
     * 返回的列表包含所有公告，包括标题、内容、创建时间等信息。
     * 
     * @return List<Notice> 公告列表
     */
    @Override
    public List<Notice> getNoticeList() {
        return mapper.selectList(new QueryWrapper<>());
    }

    /**
     * 创建公告
     * 
     * 创建新的公告记录，自动设置创建时间。
     * 公告创建后，会打印公告信息到控制台（用于调试）。
     * 
     * @param notice 要创建的公告对象
     */
    @Override
    public void createNotice(Notice notice) {
        // 打印公告信息到控制台（用于调试）
        System.out.println(notice);
        // 设置创建时间
        notice.setCreateTime(String.valueOf(System.currentTimeMillis()));
        // 插入公告记录
        mapper.insert(notice);
    }

    /**
     * 删除公告
     * 
     * 根据公告ID删除指定的公告记录。
     * 注意：删除操作不可恢复，请谨慎操作。
     * 
     * @param id 要删除的公告ID
     */
    @Override
    public void deleteNotice(String id) {
        mapper.deleteById(id);
    }

    /**
     * 更新公告
     * 
     * 更新指定公告的信息。
     * 使用公告ID作为更新条件。
     * 
     * @param notice 包含更新信息的公告对象
     */
    @Override
    public void updateNotice(Notice notice) {
        mapper.updateById(notice);
    }
}
