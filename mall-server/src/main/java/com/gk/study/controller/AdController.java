package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Ad;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.AdService;
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
 * 广告管理控制器
 *
 * 该控制器负责处理与广告相关的所有HTTP请求，包括广告的创建、查询、更新和删除等操作。
 * 广告系统支持图片上传和展示，用于在平台上展示各类广告内容。
 * 
 * 主要功能包括：
 * 1. 广告列表查询
 * 2. 广告创建（需要管理员权限）
 * 3. 广告删除（需要管理员权限）
 * 4. 广告更新（需要管理员权限）
 * 5. 广告图片上传和管理
 * 
 * 注意：
 * 1. 部分操作需要管理员权限
 * 2. 广告图片支持文件上传
 * 3. 使用事务管理确保数据一致性
 */
@RestController
@RequestMapping("/ad")
public class AdController {

    /**
     * 日志记录器
     * 用于记录控制器操作和异常信息，便于系统监控和问题排查
     */
    private final static Logger logger = LoggerFactory.getLogger(AdController.class);

    /**
     * 广告服务接口
     * 处理与广告相关的业务逻辑，包括广告的CRUD操作
     */
    @Autowired
    AdService service;

    /**
     * 文件上传路径配置
     * 从配置文件中读取，用于存储上传的广告图片
     */
    @Value("${File.uploadPath}")
    private String uploadPath;

    /**
     * 获取所有广告列表
     * 
     * 该方法返回系统中所有可用的广告列表，用于前端展示。
     * 广告按创建时间倒序排列，确保最新的广告优先显示。
     *
     * @return APIResponse 包含状态码、提示信息和广告列表数据的响应对象
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(){
        // 调用服务层获取广告列表
        List<Ad> list = service.getAdList();
        
        // 返回成功响应，包含广告列表数据
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /**
     * 创建新广告
     *
     * 该方法处理创建新广告的请求，包括保存广告图片和广告信息。
     * 整个过程在事务中执行，确保数据一致性。
     * 只有管理员级别用户才能访问此接口。
     *
     * @param ad 包含广告信息的对象，从请求中自动绑定
     * @return APIResponse 包含操作结果的响应对象
     * @throws IOException 当处理广告图片过程中出现IO异常时抛出
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Ad ad) throws IOException {
        // 记录创建广告的日志
        logger.info("开始创建广告: {}", ad);
        
        // 保存广告图片并获取图片路径
        String image = saveAd(ad);
        if(!StringUtils.isEmpty(image)) {
            ad.image = image;
        }

        // 将广告信息保存到数据库
        service.createAd(ad);
        
        // 记录创建成功的日志
        logger.info("广告创建成功: {}", ad);
        
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    /**
     * 删除广告
     *
     * 该方法处理删除广告的请求，支持批量删除多个广告。
     * 只有管理员级别用户才能访问此接口。
     *
     * @param ids 要删除的广告ID字符串，多个ID用逗号分隔
     * @return APIResponse 包含操作结果的响应对象
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        // 记录删除请求的广告ID
        logger.info("开始删除广告, ids={}", ids);
        
        // 将逗号分隔的ID字符串转换为数组
        String[] arr = ids.split(",");
        
        // 遍历ID数组，逐个删除广告
        for (String id : arr) {
            service.deleteAd(id);
        }
        
        // 记录删除成功的日志
        logger.info("广告删除成功, ids={}", ids);
        
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    /**
     * 更新广告信息
     *
     * 该方法处理更新现有广告的请求，包括更新广告图片和广告信息。
     * 整个过程在事务中执行，确保数据一致性。
     * 只有管理员级别用户才能访问此接口。
     *
     * @param ad 包含更新后广告信息的对象，从请求中自动绑定
     * @return APIResponse 包含操作结果的响应对象
     * @throws IOException 当处理广告图片过程中出现IO异常时抛出
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Ad ad) throws IOException {
        // 记录更新广告的日志
        logger.info("开始更新广告: {}", ad);
        
        // 保存广告图片并获取图片路径
        String image = saveAd(ad);
        if(!StringUtils.isEmpty(image)) {
            ad.image = image;
        }

        // 将更新后的广告信息保存到数据库
        service.updateAd(ad);
        
        // 记录更新成功的日志
        logger.info("广告更新成功: {}", ad);
        
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    /**
     * 保存广告图片
     *
     * 该方法处理广告图片的上传和存储，生成唯一的文件名以避免冲突。
     * 主要步骤包括：
     * 1. 检查是否有上传的图片文件
     * 2. 生成唯一的文件名
     * 3. 确保目标目录存在
     * 4. 保存文件到服务器
     * 5. 更新广告对象的图片路径
     *
     * @param ad 包含图片文件的广告对象
     * @return 新的文件名，如果没有上传图片则返回null
     * @throws IOException 当文件处理过程中出现IO异常时抛出
     */
    public String saveAd(Ad ad) throws IOException {
        // 获取上传的图片文件
        MultipartFile file = ad.getImageFile();
        String newFileName = null;
        
        // 检查是否有上传的图片文件
        if(file !=null && !file.isEmpty()) {
            // 记录开始处理图片的日志
            logger.info("开始处理广告图片上传");
            
            // 获取原始文件名
            String oldFileName = file.getOriginalFilename();
            
            // 生成随机的UUID作为文件名前缀，避免文件名冲突
            String randomStr = UUID.randomUUID().toString();
            
            // 保留原文件的扩展名
            newFileName = randomStr + oldFileName.substring(oldFileName.lastIndexOf("."));
            
            // 构建完整的文件路径
            String filePath = uploadPath + File.separator + "image" + File.separator + newFileName;
            File destFile = new File(filePath);
            
            // 确保父目录存在
            if(!destFile.getParentFile().exists()){
                boolean mkdirResult = destFile.getParentFile().mkdirs();
                if (!mkdirResult) {
                    logger.warn("创建目录失败: {}", destFile.getParentFile().getAbsolutePath());
                }
            }
            
            // 将上传的文件保存到目标位置
            file.transferTo(destFile);
            
            // 记录图片保存成功的日志
            logger.info("广告图片保存成功: {}", filePath);
        }
        
        // 更新广告对象的图片属性
        if(!StringUtils.isEmpty(newFileName)) {
            ad.image = newFileName;
        }
        
        return newFileName;
    }
}
