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
 * 商品分类管理控制器
 *
 * 该控制器负责处理与商品分类相关的所有HTTP请求，包括分类的创建、查询、更新和删除等操作。
 * 支持分类的树形结构管理，提供父子分类关系维护，并实现了管理员权限控制。
 * 
 * 主要功能包括：
 * 1. 查询分类列表
 * 2. 获取分类树形结构
 * 3. 获取子分类
 * 4. 创建新分类
 * 5. 删除分类
 * 6. 更新分类信息
 * 7. 获取分类路径
 * 
 * 注意：
 * 1. 创建、更新和删除操作需要管理员权限
 * 2. 分类支持多级结构，通过parentId维护父子关系
 * 3. 分类树形结构用于前端展示和导航
 * 4. 使用事务管理确保数据一致性
 * 
 * @author Administrator
 * @date 2024-03-26
 */
@RestController
@RequestMapping("/classification")
public class ClassificationController {

    /**
     * 日志记录器
     * 用于记录控制器操作和异常信息，便于系统监控和问题排查
     */
    private final static Logger logger = LoggerFactory.getLogger(ClassificationController.class);

    /**
     * 分类服务接口
     * 处理与商品分类相关的业务逻辑，包括分类的CRUD操作和树形结构管理
     */
    @Autowired
    ClassificationService service;

    /**
     * 获取所有分类列表
     * 
     * 该方法返回系统中所有的商品分类记录，用于前端展示。
     * 分类列表按创建时间倒序排列，确保最新的分类优先显示。
     *
     * @return APIResponse 包含状态码、提示信息和分类列表数据的响应对象
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(){
        // 记录开始查询日志
        logger.info("开始查询分类列表");
        
        // 调用服务层获取分类列表
        List<Classification> list = service.getClassificationList();
        
        // 记录查询结果日志
        logger.info("查询分类列表完成, 记录数量={}", list.size());
        
        // 返回成功响应，包含分类列表数据
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }
    
    /**
     * 获取分类树形结构
     * 
     * 该方法返回完整的分类树形结构，包含所有分类的父子关系。
     * 树形结构用于前端展示分类的层级关系，支持多级分类导航。
     *
     * @return APIResponse 包含状态码、提示信息和分类树形结构的响应对象
     */
    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    public APIResponse tree(){
        // 记录开始查询日志
        logger.info("开始查询分类树形结构");
        
        // 调用服务层获取分类树
        List<Classification> tree = service.getClassificationTree();
        
        // 记录查询结果日志
        logger.info("查询分类树形结构完成");
        
        // 返回成功响应，包含分类树数据
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", tree);
    }
    
    /**
     * 获取指定分类的子分类列表
     * 
     * 该方法返回指定父分类下的所有子分类。
     * 如果parentId为null，则返回所有顶级分类。
     * 用于构建分类的层级导航结构。
     *
     * @param parentId 父分类ID，如果为null则返回顶级分类
     * @return APIResponse 包含状态码、提示信息和子分类列表的响应对象
     */
    @RequestMapping(value = "/children", method = RequestMethod.GET)
    public APIResponse children(String parentId){
        // 记录开始查询日志
        logger.info("开始查询子分类列表, parentId={}", parentId);
        
        // 转换父分类ID，如果为null则使用0L（顶级分类）
        Long pid = parentId != null ? Long.parseLong(parentId) : 0L;
        
        // 调用服务层获取子分类列表
        List<Classification> children = service.getChildClassifications(pid);
        
        // 记录查询结果日志
        logger.info("查询子分类列表完成, 记录数量={}", children.size());
        
        // 返回成功响应，包含子分类列表数据
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", children);
    }

    /**
     * 创建新分类
     * 
     * 该方法处理创建新分类的请求。
     * 需要管理员权限才能执行此操作。
     * 整个过程在事务中执行，确保数据一致性。
     *
     * @param classification 包含分类信息的对象，从请求中自动绑定
     * @return APIResponse 包含操作结果的响应对象
     * @throws IOException 当处理过程中出现IO异常时抛出
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Classification classification) throws IOException {
        // 记录开始创建分类的日志
        logger.info("开始创建新分类: {}", classification);
        
        // 创建分类记录
        service.createClassification(classification);
        
        // 记录创建成功的日志
        logger.info("分类创建成功: {}", classification);
        
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    /**
     * 批量删除分类
     * 
     * 该方法处理删除分类的请求，支持批量删除多个分类。
     * 需要管理员权限才能执行此操作。
     * 删除操作会同时删除分类记录和相关的子分类。
     *
     * @param ids 要删除的分类ID列表，多个ID用逗号分隔
     * @return APIResponse 包含操作结果的响应对象
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        // 记录删除请求的分类ID
        logger.info("开始删除分类, ids={}", ids);
        
        // 将逗号分隔的ID字符串转换为数组
        String[] arr = ids.split(",");
        
        // 遍历ID数组，逐个删除分类
        for (String id : arr) {
            service.deleteClassification(id);
        }
        
        // 记录删除成功的日志
        logger.info("分类删除成功, ids={}", ids);
        
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    /**
     * 更新分类信息
     * 
     * 该方法处理更新现有分类的请求。
     * 需要管理员权限才能执行此操作。
     * 整个过程在事务中执行，确保数据一致性。
     *
     * @param classification 包含更新后分类信息的对象，从请求中自动绑定
     * @return APIResponse 包含操作结果的响应对象
     * @throws IOException 当处理过程中出现IO异常时抛出
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Classification classification) throws IOException {
        // 记录开始更新分类的日志
        logger.info("开始更新分类: {}", classification);
        
        // 更新分类记录
        service.updateClassification(classification);
        
        // 记录更新成功的日志
        logger.info("分类更新成功: {}", classification);
        
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    /**
     * 获取分类路径
     * 
     * 该方法返回从根分类到指定分类的完整路径。
     * 路径信息用于前端展示分类的层级位置，支持面包屑导航。
     * 如果分类ID无效或格式错误，将返回相应的错误信息。
     *
     * @param id 要查询的分类ID
     * @return APIResponse 包含状态码、提示信息和分类路径的响应对象
     */
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    public APIResponse getClassificationPath(String id) {
        // 记录开始查询分类路径的日志
        logger.info("开始查询分类路径, id={}", id);
        
        // 检查分类ID是否为空
        if (id == null || id.isEmpty()) {
            logger.warn("分类ID为空");
            return new APIResponse(ResponeCode.FAIL, "分类ID不能为空");
        }
        
        try {
            // 转换分类ID为Long类型
            Long categoryId = Long.parseLong(id);
            
            // 调用服务层获取分类路径
            List<Classification> path = service.getClassificationPath(categoryId);
            
            // 记录查询结果日志
            logger.info("查询分类路径完成, 路径长度={}", path.size());
            
            // 返回成功响应，包含分类路径数据
            return new APIResponse(ResponeCode.SUCCESS, "查询成功", path);
        } catch (NumberFormatException e) {
            // 记录ID格式错误日志
            logger.error("分类ID格式错误: {}", id, e);
            return new APIResponse(ResponeCode.FAIL, "分类ID格式错误");
        }
    }
}
