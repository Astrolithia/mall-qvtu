package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.OpLog;
import com.gk.study.service.OpLogService;
import com.gk.study.mapper.OpLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 操作日志服务实现类
 * 
 * 该类实现了操作日志相关的业务逻辑，包括：
 * 1. 操作日志列表的获取（全部日志和登录日志）
 * 2. 操作日志的创建、删除和更新
 * 3. 操作日志的查询和筛选
 * 
 * 使用MyBatis-Plus的ServiceImpl作为基类，简化了基础CRUD操作。
 * 日志查询默认限制为1000条记录，并按时间倒序排列。
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
@Service
public class OpLogServiceImpl extends ServiceImpl<OpLogMapper, OpLog> implements OpLogService {
    
    /**
     * 操作日志数据访问对象
     * 用于执行操作日志相关的数据库操作
     */
    @Autowired
    OpLogMapper mapper;

    /**
     * 获取操作日志列表
     * 
     * 查询所有操作日志记录，按时间倒序排列。
     * 默认限制返回前1000条记录，避免数据量过大。
     * 
     * @return List<OpLog> 操作日志列表
     */
    @Override
    public List<OpLog> getOpLogList() {
        // 创建查询条件
        QueryWrapper<OpLog> queryWrapper = new QueryWrapper();
        // 按时间倒序排序
        queryWrapper.orderBy(true, false, "re_time");
        // 限制返回前1000条记录
        queryWrapper.last("limit 0, 1000");
        // 执行查询
        return mapper.selectList(queryWrapper);
    }

    /**
     * 创建操作日志
     * 
     * 创建新的操作日志记录，记录系统操作信息。
     * 操作日志包含操作类型、操作时间、操作人等信息。
     * 
     * @param opLog 要创建的操作日志对象
     */
    @Override
    public void createOpLog(OpLog opLog) {
        mapper.insert(opLog);
    }

    /**
     * 删除操作日志
     * 
     * 根据操作日志ID删除指定的操作日志记录。
     * 注意：删除操作不可恢复，请谨慎操作。
     * 
     * @param id 要删除的操作日志ID
     */
    @Override
    public void deleteOpLog(String id) {
        mapper.deleteById(id);
    }

    /**
     * 更新操作日志
     * 
     * 更新指定操作日志的信息。
     * 使用操作日志ID作为更新条件。
     * 
     * @param opLog 包含更新信息的操作日志对象
     */
    @Override
    public void updateOpLog(OpLog opLog) {
        mapper.updateById(opLog);
    }

    /**
     * 获取登录日志列表
     * 
     * 查询所有用户登录相关的操作日志记录。
     * 通过URL路径"/api/user/userLogin"筛选登录操作。
     * 按时间倒序排列，限制返回前1000条记录。
     * 
     * @return List<OpLog> 登录日志列表
     */
    @Override
    public List<OpLog> getLoginLogList() {
        // 创建查询条件
        QueryWrapper<OpLog> queryWrapper = new QueryWrapper();
        // 筛选登录操作
        queryWrapper.eq("re_url", "/api/user/userLogin");
        // 按时间倒序排序
        queryWrapper.orderBy(true, false, "re_time");
        // 限制返回前1000条记录
        queryWrapper.last("limit 0, 1000");
        // 执行查询
        return mapper.selectList(queryWrapper);
    }
}
