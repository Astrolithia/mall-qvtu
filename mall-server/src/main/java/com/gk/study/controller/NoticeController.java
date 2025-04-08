package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Notice;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.NoticeService;
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
 * 系统通知管理控制器
 * 提供系统通知的增删改查等操作接口
 * 主要功能包括：
 * 1. 查询通知列表
 * 2. 创建新通知（需要管理员权限）
 * 3. 删除通知（需要管理员权限）
 * 4. 更新通知信息（需要管理员权限）
 * 
 * 权限说明：
 * - 查询通知列表：所有用户可访问
 * - 创建、删除、更新通知：仅管理员可操作
 * 
 * @author Administrator
 * @date 2024-03-26
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {

    // 日志记录器
    private final static Logger logger = LoggerFactory.getLogger(NoticeController.class);

    // 注入通知服务
    @Autowired
    NoticeService service;

    /**
     * 获取所有通知列表
     * 所有用户均可访问
     * @return APIResponse 包含通知列表的响应对象
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(){
        List<Notice> list =  service.getNoticeList();
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /**
     * 创建新通知
     * 需要管理员权限
     * @param notice 通知对象
     * @return APIResponse 操作结果响应
     * @throws IOException 可能抛出的IO异常
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Notice notice) throws IOException {
        service.createNotice(notice);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    /**
     * 批量删除通知
     * 需要管理员权限
     * @param ids 要删除的通知ID列表，以逗号分隔
     * @return APIResponse 操作结果响应
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        System.out.println("ids===" + ids);
        // 批量删除处理
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteNotice(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    /**
     * 更新通知信息
     * 需要管理员权限
     * @param notice 通知对象
     * @return APIResponse 操作结果响应
     * @throws IOException 可能抛出的IO异常
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Notice notice) throws IOException {
        service.updateNotice(notice);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }
}
