package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.ErrorLog;
import com.gk.study.mapper.ErrorLogMapper;
import com.gk.study.service.ErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 错误日志服务实现类
 * 
 * 该类实现了错误日志相关的业务逻辑，包括：
 * 1. 错误日志列表的获取
 * 2. 错误日志的创建、删除和更新
 * 3. 错误日志的状态管理
 * 
 * 使用MyBatis-Plus的ServiceImpl作为基类，简化了基础CRUD操作。
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
@Service
public class ErrorLogServiceImpl extends ServiceImpl<ErrorLogMapper, ErrorLog> implements ErrorLogService {
    
    /**
     * 错误日志数据访问对象
     * 用于执行错误日志相关的数据库操作
     */
    @Autowired
    ErrorLogMapper mapper;

    /**
     * 获取错误日志列表
     * 
     * 查询所有错误日志记录，不添加任何筛选条件。
     * 返回的列表包含所有错误日志，包括错误信息、时间、状态等。
     * 
     * @return List<ErrorLog> 错误日志列表
     */
    @Override
    public List<ErrorLog> getErrorLogList() {
        return mapper.selectList(new QueryWrapper<>());
    }

    /**
     * 创建错误日志
     * 
     * 创建新的错误日志记录，记录系统运行过程中的错误信息。
     * 错误日志包含错误详情、发生时间、错误类型等信息。
     * 
     * @param errorLog 要创建的错误日志对象
     */
    @Override
    public void createErrorLog(ErrorLog errorLog) {
        mapper.insert(errorLog);
    }

    /**
     * 删除错误日志
     * 
     * 根据错误日志ID删除指定的错误日志记录。
     * 注意：删除操作不可恢复，请谨慎操作。
     * 
     * @param id 要删除的错误日志ID
     */
    @Override
    public void deleteErrorLog(String id) {
        mapper.deleteById(id);
    }

    /**
     * 更新错误日志
     * 
     * 更新指定错误日志的信息。
     * 使用错误日志ID作为更新条件。
     * 
     * @param errorLog 包含更新信息的错误日志对象
     */
    @Override
    public void updateErrorLog(ErrorLog errorLog) {
        mapper.updateById(errorLog);
    }
}
