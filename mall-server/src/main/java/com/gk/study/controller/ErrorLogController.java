package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.ErrorLog;
import com.gk.study.service.ErrorLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * 错误日志管理控制器
 * 提供系统错误日志的增删改查等操作接口
 * 主要功能包括：
 * 1. 查询错误日志列表
 * 2. 创建错误日志记录
 * 3. 删除错误日志
 * 4. 更新错误日志信息
 * 
 * @author Administrator
 * @date 2024-03-26
 */
@RestController
@RequestMapping("/errorLog")
public class ErrorLogController {

    // 日志记录器
    private final static Logger logger = LoggerFactory.getLogger(ErrorLogController.class);

    // 注入错误日志服务
    @Autowired
    ErrorLogService service;

    /**
     * 获取所有错误日志列表
     * @return APIResponse 包含错误日志列表的响应对象
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(){
        List<ErrorLog> list =  service.getErrorLogList();
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /**
     * 创建新的错误日志记录
     * @param errorLog 错误日志对象
     * @return APIResponse 操作结果响应
     * @throws IOException 可能抛出的IO异常
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(ErrorLog errorLog) throws IOException {
        service.createErrorLog(errorLog);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    /**
     * 批量删除错误日志
     * @param ids 要删除的错误日志ID列表，以逗号分隔
     * @return APIResponse 操作结果响应
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        System.out.println("ids===" + ids);
        // 批量删除处理
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteErrorLog(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    /**
     * 更新错误日志信息
     * @param errorLog 错误日志对象
     * @return APIResponse 操作结果响应
     * @throws IOException 可能抛出的IO异常
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(ErrorLog errorLog) throws IOException {
        service.updateErrorLog(errorLog);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }
}
