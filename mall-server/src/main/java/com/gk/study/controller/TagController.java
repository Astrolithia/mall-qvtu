package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Tag;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.TagService;
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
 * 标签管理控制器
 * 提供标签的增删改查功能
 * 主要功能包括：
 * 1. 查询标签列表
 * 2. 创建新标签
 * 3. 删除标签
 * 4. 更新标签信息
 * 
 * 注意：创建、删除和更新操作需要管理员权限
 * 
 * @author Administrator
 * @email 123456@qq.com
 */
@RestController
@RequestMapping("/tag")
public class TagController {

    /**
     * 日志记录器，用于记录系统运行日志
     */
    private final static Logger logger = LoggerFactory.getLogger(TagController.class);

    /**
     * 标签服务接口，用于处理标签相关的业务逻辑
     */
    @Autowired
    TagService service;

    /**
     * 获取所有标签列表
     * 该接口不需要权限验证，所有用户都可以访问
     * 
     * @return APIResponse 包含标签列表的响应对象
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(){
        // 调用服务层获取标签列表
        List<Tag> list =  service.getTagList();

        // 返回成功响应，包含标签列表数据
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /**
     * 创建新标签
     * 需要管理员权限
     * 使用事务注解确保数据一致性
     * 
     * @param tag 要创建的标签对象
     * @return APIResponse 操作结果响应
     * @throws IOException 当创建过程中发生IO异常时抛出
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Tag tag) throws IOException {
        // 调用服务层创建标签
        service.createTag(tag);
        
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    /**
     * 批量删除标签
     * 需要管理员权限
     * 支持一次删除多个标签
     * 
     * @param ids 要删除的标签ID列表，多个ID用逗号分隔
     * @return APIResponse 操作结果响应
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        // 记录删除请求的标签ID
        System.out.println("ids===" + ids);
        
        // 将逗号分隔的ID字符串转换为数组
        String[] arr = ids.split(",");
        
        // 遍历ID数组，逐个删除标签
        for (String id : arr) {
            service.deleteTag(id);
        }
        
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    /**
     * 更新标签信息
     * 需要管理员权限
     * 使用事务注解确保数据一致性
     * 
     * @param tag 要更新的标签对象
     * @return APIResponse 操作结果响应
     * @throws IOException 当更新过程中发生IO异常时抛出
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Tag tag) throws IOException {
        // 调用服务层更新标签信息
        service.updateTag(tag);
        
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

}
