package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Classification;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.ClassificationService;
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
 * 分类控制器
 *  * @author Administrator
 *  * @date 2024-03-26
 */
@RestController
@RequestMapping("/classification")
public class ClassificationController {

    private final static Logger logger = LoggerFactory.getLogger(ClassificationController.class);

    @Autowired
    ClassificationService service;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(){
        List<Classification> list =  service.getClassificationList();
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }
    
    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    public APIResponse tree(){
        List<Classification> tree =  service.getClassificationTree();
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", tree);
    }
    
    @RequestMapping(value = "/children", method = RequestMethod.GET)
    public APIResponse children(String parentId){
        Long pid = parentId != null ? Long.parseLong(parentId) : 0L;
        List<Classification> children = service.getChildClassifications(pid);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", children);
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Classification classification) throws IOException {
        service.createClassification(classification);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        System.out.println("ids===" + ids);
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteClassification(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Classification classification) throws IOException {
        service.updateClassification(classification);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    @RequestMapping(value = "/path", method = RequestMethod.GET)
    public APIResponse getClassificationPath(String id) {
        if (id == null || id.isEmpty()) {
            return new APIResponse(ResponeCode.FAIL, "分类ID不能为空");
        }
        
        try {
            Long categoryId = Long.parseLong(id);
            List<Classification> path = service.getClassificationPath(categoryId);
            return new APIResponse(ResponeCode.SUCCESS, "查询成功", path);
        } catch (NumberFormatException e) {
            return new APIResponse(ResponeCode.FAIL, "分类ID格式错误");
        }
    }

}
