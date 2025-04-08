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

@RestController
@RequestMapping("/thing")
public class ThingController {

    private final static Logger logger = LoggerFactory.getLogger(ThingController.class);

    @Autowired
    ThingService service;

    @Value("${File.uploadPath}")
    private String uploadPath;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String keyword, String sort, String c, String tag){
        List<Thing> list =  service.getThingList(keyword, sort, c, tag);

        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(String id){
        Thing thing =  service.getThingById(id);

        return new APIResponse(ResponeCode.SUCCESS, "查询成功", thing);
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Thing thing) throws IOException {
        String url = saveThing(thing);
        if(!StringUtils.isEmpty(url)) {
            thing.cover = url;
        }

        service.createThing(thing);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        System.out.println("ids===" + ids);
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteThing(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Thing thing) throws IOException {
        System.out.println(thing);
        String url = saveThing(thing);
        if(!StringUtils.isEmpty(url)) {
            thing.cover = url;
        }

        service.updateThing(thing);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    public String saveThing(Thing thing) throws IOException {
        MultipartFile file = thing.getImageFile();
        String newFileName = null;
        if(file !=null && !file.isEmpty()) {

            // 存文件
            String oldFileName = file.getOriginalFilename();
            String randomStr = UUID.randomUUID().toString();
            newFileName = randomStr + oldFileName.substring(oldFileName.lastIndexOf("."));
            String filePath = uploadPath + File.separator + "image" + File.separator + newFileName;
            File destFile = new File(filePath);
            if(!destFile.getParentFile().exists()){
                destFile.getParentFile().mkdirs();
            }
            file.transferTo(destFile);
        }
        if(!StringUtils.isEmpty(newFileName)) {
            thing.cover = newFileName;
        }
        return newFileName;
    }

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/score", method = RequestMethod.POST)
    public APIResponse score(String thingId, String score) {
        if (StringUtils.isEmpty(thingId) || StringUtils.isEmpty(score)) {
            return new APIResponse(ResponeCode.FAIL, "参数错误");
        }
        
        try {
            BigDecimal scoreValue = new BigDecimal(score);
            // 评分范围限制为0-5
            if (scoreValue.compareTo(BigDecimal.ZERO) < 0 || scoreValue.compareTo(new BigDecimal("5")) > 0) {
                return new APIResponse(ResponeCode.FAIL, "评分必须在0-5之间");
            }
            
            service.addScore(thingId, scoreValue);
            return new APIResponse(ResponeCode.SUCCESS, "评分成功");
        } catch (NumberFormatException e) {
            return new APIResponse(ResponeCode.FAIL, "评分格式错误");
        }
    }

    @RequestMapping(value = "/discount", method = RequestMethod.GET)
    public APIResponse getDiscount(String id) {
        if (StringUtils.isEmpty(id)) {
            return new APIResponse(ResponeCode.FAIL, "参数错误");
        }
        
        Thing thing = service.getThingById(id);
        if (thing == null) {
            return new APIResponse(ResponeCode.FAIL, "商品不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("price", thing.getPrice());
        result.put("originalPrice", thing.getOriginalPrice());
        
        // 计算折扣率和节省金额
        BigDecimal discountRate = thing.getDiscountRate();
        BigDecimal savedAmount = thing.getSavedAmount();
        
        result.put("discountRate", discountRate);
        result.put("savedAmount", savedAmount);
        
        // 格式化折扣信息，如"8.5折"，"省¥30"
        String discountText = discountRate.multiply(new BigDecimal("10")).setScale(1, BigDecimal.ROUND_HALF_UP) + "折";
        String savedText = "省¥" + savedAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
        
        result.put("discountText", discountText);
        result.put("savedText", savedText);
        
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", result);
    }

    @RequestMapping(value = "/updateViews", method = RequestMethod.POST)
    public APIResponse updateViews(String id){
        service.updateViewCount(id);
        return new APIResponse(ResponeCode.SUCCESS, "更新浏览量成功");
    }

}
