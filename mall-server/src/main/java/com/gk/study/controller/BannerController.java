package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Banner;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.BannerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 轮播图管理控制器
 *
 * 该控制器负责处理与轮播图相关的所有HTTP请求，包括轮播图的创建、查询、更新和删除等操作。
 * 轮播图系统支持图片上传、展示顺序管理等功能，并实现了管理员权限控制。
 * 
 * 主要功能包括：
 * 1. 查询轮播图列表
 * 2. 创建新轮播图（包含图片上传）
 * 3. 删除轮播图
 * 4. 更新轮播图信息
 * 5. 图片文件管理
 * 
 * 注意：
 * 1. 创建、更新和删除操作需要管理员权限
 * 2. 图片上传使用UUID生成唯一文件名
 * 3. 文件存储路径通过配置文件指定
 * 4. 使用事务管理确保数据一致性
 * 
 * @author Administrator
 * @date 2024-03-26
 */
@RestController
@RequestMapping("/banner")
public class BannerController {

    /**
     * 日志记录器
     * 用于记录控制器操作和异常信息，便于系统监控和问题排查
     */
    private final static Logger logger = LoggerFactory.getLogger(BannerController.class);

    /**
     * 轮播图服务接口
     * 处理与轮播图相关的业务逻辑，包括轮播图的CRUD操作
     */
    @Autowired
    BannerService service;

    /**
     * 文件上传路径配置
     * 从配置文件中读取，用于指定轮播图图片的存储位置
     */
    @Value("${File.uploadPath}")
    private String uploadPath;

    /**
     * 获取轮播图列表
     * 
     * 该方法返回系统中所有的轮播图记录，用于前端展示。
     * 轮播图列表按创建时间倒序排列，确保最新的轮播图优先显示。
     *
     * @return APIResponse 包含状态码、提示信息和轮播图列表数据的响应对象
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(){
        // 记录开始查询日志
        logger.info("开始查询轮播图列表");
        
        // 调用服务层获取轮播图列表
        List<Banner> list = service.getBannerList();
        
        // 记录查询结果日志
        logger.info("查询轮播图列表完成, 记录数量={}", list.size());
        
        // 返回成功响应，包含轮播图列表数据
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /**
     * 创建新轮播图
     *
     * 该方法处理创建新轮播图的请求，包含图片上传功能。
     * 需要管理员权限才能执行此操作。
     * 整个过程在事务中执行，确保数据一致性。
     *
     * @param banner 包含轮播图信息的对象，从请求中自动绑定
     * @return APIResponse 包含操作结果的响应对象
     * @throws IOException 当图片上传过程中出现IO异常时抛出
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Banner banner) throws IOException {
        // 记录开始创建轮播图的日志
        logger.info("开始创建新轮播图: {}", banner);
        
        // 保存轮播图图片并获取文件名
        String image = saveBanner(banner);
        
        // 如果成功保存图片，更新轮播图对象的图片路径
        if(!StringUtils.isEmpty(image)) {
            banner.image = image;
        }
        
        // 创建轮播图记录
        service.createBanner(banner);
        
        // 记录创建成功的日志
        logger.info("轮播图创建成功: {}", banner);
        
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    /**
     * 批量删除轮播图
     *
     * 该方法处理删除轮播图的请求，支持批量删除多个轮播图。
     * 需要管理员权限才能执行此操作。
     * 删除操作会同时删除轮播图记录和相关的图片文件。
     *
     * @param ids 要删除的轮播图ID列表，多个ID用逗号分隔
     * @return APIResponse 包含操作结果的响应对象
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        // 记录删除请求的轮播图ID
        logger.info("开始删除轮播图, ids={}", ids);
        
        // 将逗号分隔的ID字符串转换为数组
        String[] arr = ids.split(",");
        
        // 遍历ID数组，逐个删除轮播图
        for (String id : arr) {
            service.deleteBanner(id);
        }
        
        // 记录删除成功的日志
        logger.info("轮播图删除成功, ids={}", ids);
        
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    /**
     * 更新轮播图信息
     *
     * 该方法处理更新现有轮播图的请求，包含图片更新功能。
     * 需要管理员权限才能执行此操作。
     * 整个过程在事务中执行，确保数据一致性。
     *
     * @param banner 包含更新后轮播图信息的对象，从请求中自动绑定
     * @return APIResponse 包含操作结果的响应对象
     * @throws IOException 当图片上传过程中出现IO异常时抛出
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Banner banner) throws IOException {
        // 记录开始更新轮播图的日志
        logger.info("开始更新轮播图: {}", banner);
        
        // 保存轮播图图片并获取文件名
        String image = saveBanner(banner);
        
        // 如果成功保存图片，更新轮播图对象的图片路径
        if(!StringUtils.isEmpty(image)) {
            banner.image = image;
        }
        
        // 更新轮播图记录
        service.updateBanner(banner);
        
        // 记录更新成功的日志
        logger.info("轮播图更新成功: {}", banner);
        
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    /**
     * 保存轮播图图片
     *
     * 该方法处理轮播图图片的上传和保存逻辑，包括：
     * 1. 生成唯一文件名
     * 2. 创建存储目录
     * 3. 保存文件到指定位置
     * 
     * 文件命名规则：
     * 1. 使用UUID生成随机字符串
     * 2. 保留原文件扩展名
     * 3. 确保文件名唯一性
     *
     * @param banner 包含图片文件的轮播图对象
     * @return String 保存后的文件名，如果未上传文件则返回null
     * @throws IOException 当文件保存过程中出现IO异常时抛出
     */
    public String saveBanner(Banner banner) throws IOException {
        // 获取上传的图片文件
        MultipartFile file = banner.getImageFile();
        String newFileName = null;
        
        // 检查文件是否存在且不为空
        if(file !=null && !file.isEmpty()) {
            // 记录开始保存图片的日志
            logger.info("开始保存轮播图图片, 原文件名={}", file.getOriginalFilename());
            
            // 生成唯一文件名
            String oldFileName = file.getOriginalFilename();
            String randomStr = UUID.randomUUID().toString();
            newFileName = randomStr + oldFileName.substring(oldFileName.lastIndexOf("."));
            
            // 构建文件保存路径
            String filePath = uploadPath + File.separator + "banner" + File.separator + newFileName;
            File destFile = new File(filePath);
            
            // 确保存储目录存在
            if(!destFile.getParentFile().exists()){
                destFile.getParentFile().mkdirs();
            }
            
            // 保存文件到指定位置
            file.transferTo(destFile);
            
            // 记录保存成功的日志
            logger.info("轮播图图片保存成功, 新文件名={}", newFileName);
        }
        
        // 如果成功保存图片，更新轮播图对象的图片路径
        if(!StringUtils.isEmpty(newFileName)) {
            banner.image = newFileName;
        }
        
        return newFileName;
    }
}
