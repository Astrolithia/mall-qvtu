package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.OpLog;
import com.gk.study.service.OpLogService;
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
 * 操作日志管理控制器
 * 提供系统操作日志和登录日志的增删改查等操作接口
 * 主要功能包括：
 * 1. 查询操作日志列表
 * 2. 查询登录日志列表
 * 3. 创建操作日志记录
 * 4. 删除操作日志
 * 5. 更新操作日志信息
 * 
 * 日志类型说明：
 * - 操作日志：记录用户的操作行为
 * - 登录日志：记录用户的登录行为
 * 
 * @author Administrator
 * @date 2024-03-26
 */
@RestController
@RequestMapping("/opLog")
public class OpLogController {

    // 日志记录器
    private final static Logger logger = LoggerFactory.getLogger(OpLogController.class);

    // 注入操作日志服务
    @Autowired
    OpLogService service;

    /**
     * 获取所有操作日志列表
     * @return APIResponse 包含操作日志列表的响应对象
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(){
        List<OpLog> list =  service.getOpLogList();
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /**
     * 获取所有登录日志列表
     * @return APIResponse 包含登录日志列表的响应对象
     */
    @RequestMapping(value = "/loginLogList", method = RequestMethod.GET)
    public APIResponse loginLogList(){
        List<OpLog> list =  service.getLoginLogList();
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /**
     * 创建新的操作日志记录
     * @param opLog 操作日志对象
     * @return APIResponse 操作结果响应
     * @throws IOException 可能抛出的IO异常
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(OpLog opLog) throws IOException {
        service.createOpLog(opLog);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    /**
     * 批量删除操作日志
     * @param ids 要删除的操作日志ID列表，以逗号分隔
     * @return APIResponse 操作结果响应
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        System.out.println("ids===" + ids);
        // 批量删除处理
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteOpLog(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    /**
     * 更新操作日志信息
     * @param opLog 操作日志对象
     * @return APIResponse 操作结果响应
     * @throws IOException 可能抛出的IO异常
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(OpLog opLog) throws IOException {
        service.updateOpLog(opLog);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }
}
