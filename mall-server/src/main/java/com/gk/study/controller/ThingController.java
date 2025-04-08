package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Thing;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.ThingService;
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
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 商品管理控制器
 * 提供商品相关的功能接口
 * 主要功能包括：
 * 1. 商品列表查询（支持关键词、排序、分类、标签筛选）
 * 2. 商品详情查询
 * 3. 商品创建（需要管理员权限）
 * 4. 商品删除（需要管理员权限）
 * 5. 商品更新（需要管理员权限）
 * 6. 商品评分（需要登录权限）
 * 7. 商品折扣信息查询
 * 8. 商品浏览量更新
 * 
 * 注意：部分操作需要管理员或登录权限
 */
@RestController
@RequestMapping("/thing")
public class ThingController {

    /**
     * 日志记录器，用于记录系统运行日志
     */
    private final static Logger logger = LoggerFactory.getLogger(ThingController.class);

    /**
     * 商品服务接口，用于处理商品相关的业务逻辑
     */
    @Autowired
    ThingService service;

    /**
     * 文件上传路径配置，从配置文件中读取
     */
    @Value("${File.uploadPath}")
    private String uploadPath;

    /**
     * 获取商品列表
     * 支持多种查询条件：
     * 1. 关键词搜索
     * 2. 排序方式
     * 3. 分类筛选
     * 4. 标签筛选
     * 
     * @param keyword 搜索关键词
     * @param sort 排序方式
     * @param c 分类ID
     * @param tag 标签ID
     * @return APIResponse 包含商品列表的响应对象
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String keyword, String sort, String c, String tag){
        // 调用服务层获取商品列表
        List<Thing> list =  service.getThingList(keyword, sort, c, tag);

        // 返回成功响应，包含商品列表数据
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /**
     * 获取商品详情
     * 
     * @param id 商品ID
     * @return APIResponse 包含商品详情的响应对象
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(String id){
        // 调用服务层获取商品详情
        Thing thing =  service.getThingById(id);

        // 返回成功响应，包含商品详情数据
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", thing);
    }

    /**
     * 创建商品
     * 需要管理员权限
     * 使用事务注解确保数据一致性
     * 支持商品图片上传
     * 
     * @param thing 商品对象
     * @return APIResponse 操作结果响应
     * @throws IOException 当文件上传过程中发生IO异常时抛出
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Thing thing) throws IOException {
        // 保存商品图片并获取URL
        String url = saveThing(thing);
        
        // 如果图片上传成功，更新商品封面
        if(!StringUtils.isEmpty(url)) {
            thing.cover = url;
        }

        // 创建商品
        service.createThing(thing);
        
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    /**
     * 批量删除商品
     * 需要管理员权限
     * 
     * @param ids 要删除的商品ID列表，多个ID用逗号分隔
     * @return APIResponse 操作结果响应
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        // 记录删除请求的商品ID
        System.out.println("ids===" + ids);
        
        // 将逗号分隔的ID字符串转换为数组
        String[] arr = ids.split(",");
        
        // 遍历ID数组，逐个删除商品
        for (String id : arr) {
            service.deleteThing(id);
        }
        
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    /**
     * 更新商品信息
     * 需要管理员权限
     * 使用事务注解确保数据一致性
     * 支持商品图片更新
     * 
     * @param thing 商品对象
     * @return APIResponse 操作结果响应
     * @throws IOException 当文件上传过程中发生IO异常时抛出
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Thing thing) throws IOException {
        // 记录更新请求的商品信息
        System.out.println(thing);
        
        // 保存商品图片并获取URL
        String url = saveThing(thing);
        
        // 如果图片上传成功，更新商品封面
        if(!StringUtils.isEmpty(url)) {
            thing.cover = url;
        }

        // 更新商品信息
        service.updateThing(thing);
        
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    /**
     * 保存商品图片
     * 处理商品图片的上传和存储
     * 
     * @param thing 商品对象，包含要上传的图片文件
     * @return String 图片文件的URL
     * @throws IOException 当文件上传过程中发生IO异常时抛出
     */
    public String saveThing(Thing thing) throws IOException {
        // 获取上传的图片文件
        MultipartFile file = thing.getImageFile();
        String newFileName = null;
        
        // 如果文件存在且不为空，则进行上传处理
        if(file !=null && !file.isEmpty()) {
            // 生成新的文件名
            String oldFileName = file.getOriginalFilename();
            String randomStr = UUID.randomUUID().toString();
            newFileName = randomStr + oldFileName.substring(oldFileName.lastIndexOf("."));
            
            // 构建文件保存路径
            String filePath = uploadPath + File.separator + "image" + File.separator + newFileName;
            File destFile = new File(filePath);
            
            // 确保目标目录存在
            if(!destFile.getParentFile().exists()){
                destFile.getParentFile().mkdirs();
            }
            
            // 保存文件
            file.transferTo(destFile);
        }
        
        // 如果生成了新文件名，更新商品封面
        if(!StringUtils.isEmpty(newFileName)) {
            thing.cover = newFileName;
        }
        
        return newFileName;
    }

    /**
     * 商品评分
     * 需要用户登录权限
     * 评分范围限制在0-5之间
     * 
     * @param thingId 商品ID
     * @param score 评分值
     * @return APIResponse 操作结果响应
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/score", method = RequestMethod.POST)
    public APIResponse score(String thingId, String score) {
        // 参数校验
        if (StringUtils.isEmpty(thingId) || StringUtils.isEmpty(score)) {
            return new APIResponse(ResponeCode.FAIL, "参数错误");
        }
        
        try {
            // 将评分转换为BigDecimal类型
            BigDecimal scoreValue = new BigDecimal(score);
            
            // 验证评分范围
            if (scoreValue.compareTo(BigDecimal.ZERO) < 0 || scoreValue.compareTo(new BigDecimal("5")) > 0) {
                return new APIResponse(ResponeCode.FAIL, "评分必须在0-5之间");
            }
            
            // 添加评分
            service.addScore(thingId, scoreValue);
            
            // 返回成功响应
            return new APIResponse(ResponeCode.SUCCESS, "评分成功");
        } catch (NumberFormatException e) {
            // 处理评分格式错误
            return new APIResponse(ResponeCode.FAIL, "评分格式错误");
        }
    }

    /**
     * 获取商品折扣信息
     * 包括：
     * 1. 当前价格
     * 2. 原价
     * 3. 折扣率
     * 4. 节省金额
     * 5. 格式化后的折扣信息
     * 
     * @param id 商品ID
     * @return APIResponse 包含折扣信息的响应对象
     */
    @RequestMapping(value = "/discount", method = RequestMethod.GET)
    public APIResponse getDiscount(String id) {
        // 参数校验
        if (StringUtils.isEmpty(id)) {
            return new APIResponse(ResponeCode.FAIL, "参数错误");
        }
        
        // 获取商品信息
        Thing thing = service.getThingById(id);
        if (thing == null) {
            return new APIResponse(ResponeCode.FAIL, "商品不存在");
        }
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("price", thing.getPrice());
        result.put("originalPrice", thing.getOriginalPrice());
        
        // 计算折扣信息
        BigDecimal discountRate = thing.getDiscountRate();
        BigDecimal savedAmount = thing.getSavedAmount();
        
        result.put("discountRate", discountRate);
        result.put("savedAmount", savedAmount);
        
        // 格式化折扣信息
        String discountText = discountRate.multiply(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_HALF_UP) + "折";
        String savedText = "省¥" + savedAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
        
        result.put("discountText", discountText);
        result.put("savedText", savedText);
        
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", result);
    }

    /**
     * 更新商品浏览量
     * 
     * @param id 商品ID
     * @return APIResponse 操作结果响应
     */
    @RequestMapping(value = "/updateViews", method = RequestMethod.POST)
    public APIResponse updateViews(String id){
        // 更新商品浏览量
        service.updateViewCount(id);
        
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "更新浏览量成功");
    }
}
