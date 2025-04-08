package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Address;
import com.gk.study.service.AddressService;
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
 * 地址管理控制器
 *
 * 该控制器负责处理与用户地址相关的所有HTTP请求，包括地址的创建、查询、更新和删除等操作。
 * 地址系统支持用户管理多个收货地址，并可以设置默认地址。
 * 
 * 主要功能包括：
 * 1. 查询用户地址列表
 * 2. 创建新地址
 * 3. 删除地址
 * 4. 更新地址信息
 * 5. 设置默认地址
 * 
 * 注意：
 * 1. 每个用户可以有多个地址
 * 2. 每个用户只能有一个默认地址
 * 3. 设置新默认地址时会自动取消其他地址的默认状态
 * 4. 使用事务管理确保数据一致性
 * 
 * @author 系统生成
 * @version 1.0
 */
@RestController
@RequestMapping("/address")
public class AddressController {

    /**
     * 日志记录器
     * 用于记录控制器操作和异常信息，便于系统监控和问题排查
     */
    private final static Logger logger = LoggerFactory.getLogger(AddressController.class);

    /**
     * 地址服务接口
     * 处理与地址相关的业务逻辑，包括地址的CRUD操作
     */
    @Autowired
    AddressService service;

    /**
     * 获取用户地址列表
     * 
     * 该方法返回指定用户的所有地址记录，用于前端展示和管理。
     * 地址列表按创建时间倒序排列，确保最新的地址优先显示。
     *
     * @param userId 用户ID，用于识别查询哪个用户的地址
     * @return APIResponse 包含状态码、提示信息和地址列表数据的响应对象
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String userId){
        // 记录开始查询日志
        logger.info("开始查询用户地址列表, userId={}", userId);
        
        // 调用服务层获取地址列表
        List<Address> list = service.getAddressList(userId);
        
        // 记录查询结果日志
        logger.info("查询用户地址列表完成, userId={}, 记录数量={}", userId, list.size());
        
        // 返回成功响应，包含地址列表数据
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /**
     * 创建新地址
     *
     * 该方法处理创建新地址的请求，支持设置默认地址。
     * 如果新地址设置为默认地址，系统会自动将用户的其他地址设置为非默认状态。
     * 整个过程在事务中执行，确保数据一致性。
     *
     * @param address 包含地址信息的对象，从请求中自动绑定
     * @return APIResponse 包含操作结果的响应对象
     * @throws IOException 当处理过程中出现IO异常时抛出
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Address address) throws IOException {
        // 记录开始创建地址的日志
        logger.info("开始创建新地址: {}", address);
        
        // 检查是否设置为默认地址
        if(address.getDef().equals("1")){
            // 记录设置默认地址的日志
            logger.info("新地址设置为默认地址, 开始更新其他地址状态");
            
            // 获取用户的所有地址
            List<Address> list = service.getAddressList(address.getUserId());
            
            // 遍历所有地址，将其他地址设置为非默认
            for(Address address1: list) {
                address1.setDef("0");
                service.updateAddress(address1);
            }
            
            // 记录更新完成的日志
            logger.info("其他地址已更新为非默认状态");
        }
        
        // 创建新地址
        service.createAddress(address);
        
        // 记录创建成功的日志
        logger.info("地址创建成功: {}", address);
        
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    /**
     * 批量删除地址
     *
     * 该方法处理删除地址的请求，支持批量删除多个地址。
     * 删除操作会同时删除地址记录和相关的默认地址设置。
     *
     * @param ids 要删除的地址ID列表，多个ID用逗号分隔
     * @return APIResponse 包含操作结果的响应对象
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        // 记录删除请求的地址ID
        logger.info("开始删除地址, ids={}", ids);
        
        // 将逗号分隔的ID字符串转换为数组
        String[] arr = ids.split(",");
        
        // 遍历ID数组，逐个删除地址
        for (String id : arr) {
            service.deleteAddress(id);
        }
        
        // 记录删除成功的日志
        logger.info("地址删除成功, ids={}", ids);
        
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    /**
     * 更新地址信息
     *
     * 该方法处理更新现有地址的请求，支持更新地址信息和设置默认地址。
     * 如果更新为默认地址，系统会自动将用户的其他地址设置为非默认状态。
     * 整个过程在事务中执行，确保数据一致性。
     *
     * @param address 包含更新后地址信息的对象，从请求中自动绑定
     * @return APIResponse 包含操作结果的响应对象
     * @throws IOException 当处理过程中出现IO异常时抛出
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Address address) throws IOException {
        // 记录开始更新地址的日志
        logger.info("开始更新地址: {}", address);
        
        // 检查是否更新为默认地址
        if(address.getDef().equals("1")){
            // 记录设置默认地址的日志
            logger.info("地址更新为默认地址, 开始更新其他地址状态");
            
            // 获取用户的所有地址
            List<Address> list = service.getAddressList(address.getUserId());
            
            // 遍历所有地址，将其他地址设置为非默认
            for(Address address1: list) {
                address1.setDef("0");
                service.updateAddress(address1);
            }
            
            // 记录更新完成的日志
            logger.info("其他地址已更新为非默认状态");
        }
        
        // 更新地址信息
        service.updateAddress(address);
        
        // 记录更新成功的日志
        logger.info("地址更新成功: {}", address);
        
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }
}
