package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 * 提供文件上传相关的操作接口
 * 主要功能包括：
 * 1. 上传图片（支持富文本编辑器）
 * 2. 上传视频（支持富文本编辑器）
 * 
 * 文件存储说明：
 * - 图片文件存储在 editor/images 目录下
 * - 视频文件存储在 editor/videos 目录下
 * - 使用UUID生成唯一文件名，避免文件名冲突
 * 
 * @author Administrator
 * @date 2024-03-26
 */
@RestController
@RequestMapping("/file")
public class FileController {

    // 日志记录器
    private final static Logger logger = LoggerFactory.getLogger(FileController.class);

    // 文件上传根路径配置
    @Value("${File.uploadPath}")
    private String uploadPath;

    /**
     * 上传图片（富文本编辑器使用）
     * 支持图片文件上传，返回符合wangEditor格式的响应
     * 
     * 处理流程：
     * 1. 验证文件是否为空
     * 2. 生成唯一文件名
     * 3. 创建存储目录
     * 4. 保存文件
     * 5. 返回文件访问URL
     * 
     * @param file 上传的图片文件
     * @return APIResponse 包含文件URL等信息的响应对象
     */
    @RequestMapping(value = "/upload/image", method = RequestMethod.POST)
    public APIResponse uploadImage(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return new APIResponse(ResponeCode.FAIL, "文件为空");
        }
        
        try {
            // 生成新文件名
            String oldFileName = file.getOriginalFilename();
            String randomStr = UUID.randomUUID().toString();
            String newFileName = randomStr + oldFileName.substring(oldFileName.lastIndexOf("."));
            
            // 存储路径
            String dirPath = uploadPath + File.separator + "editor" + File.separator + "images";
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 保存文件
            String filePath = dirPath + File.separator + newFileName;
            File destFile = new File(filePath);
            file.transferTo(destFile);
            
            // 返回文件访问URL（修复URL格式）
            String fileUrl = "/api/staticfiles/editor/images/" + newFileName;
            
            Map<String, Object> data = new HashMap<>();
            // 以下格式符合wangEditor的要求
            data.put("url", fileUrl);
            data.put("alt", oldFileName);
            data.put("href", fileUrl);
            
            return new APIResponse(ResponeCode.SUCCESS, "上传成功", data);
        } catch (IOException e) {
            logger.error("文件上传失败", e);
            return new APIResponse(ResponeCode.FAIL, "文件上传失败");
        }
    }
    
    /**
     * 上传视频（富文本编辑器使用）
     * 支持视频文件上传，返回文件访问URL
     * 
     * 处理流程：
     * 1. 验证文件是否为空
     * 2. 生成唯一文件名
     * 3. 创建存储目录
     * 4. 保存文件
     * 5. 返回文件访问URL
     * 
     * @param file 上传的视频文件
     * @return APIResponse 包含文件URL的响应对象
     */
    @RequestMapping(value = "/upload/video", method = RequestMethod.POST)
    public APIResponse uploadVideo(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return new APIResponse(ResponeCode.FAIL, "文件为空");
        }
        
        try {
            // 生成新文件名
            String oldFileName = file.getOriginalFilename();
            String randomStr = UUID.randomUUID().toString();
            String newFileName = randomStr + oldFileName.substring(oldFileName.lastIndexOf("."));
            
            // 存储路径
            String dirPath = uploadPath + File.separator + "editor" + File.separator + "videos";
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 保存文件
            String filePath = dirPath + File.separator + newFileName;
            File destFile = new File(filePath);
            file.transferTo(destFile);
            
            // 返回文件访问URL
            String fileUrl = "/api/staticfiles/editor/videos/" + newFileName;
            
            Map<String, Object> data = new HashMap<>();
            data.put("url", fileUrl);
            
            return new APIResponse(ResponeCode.SUCCESS, "上传成功", data);
        } catch (IOException e) {
            logger.error("视频上传失败", e);
            return new APIResponse(ResponeCode.FAIL, "视频上传失败");
        }
    }
} 