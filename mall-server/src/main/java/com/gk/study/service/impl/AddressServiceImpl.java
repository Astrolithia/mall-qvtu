package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Address;
import com.gk.study.mapper.AddressMapper;
import com.gk.study.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 地址服务实现类
 * 
 * 该类实现了用户地址相关的业务逻辑，包括：
 * 1. 用户地址列表的获取
 * 2. 地址的创建、删除和更新
 * 3. 地址的排序和默认地址管理
 * 
 * 使用MyBatis-Plus的ServiceImpl作为基类，简化了基础CRUD操作。
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {
    
    /**
     * 地址数据访问对象
     * 用于执行地址相关的数据库操作
     */
    @Autowired
    AddressMapper mapper;

    /**
     * 获取用户地址列表
     * 
     * 根据用户ID查询该用户的所有地址记录。
     * 返回的列表按创建时间倒序排列。
     * 
     * @param userId 用户ID
     * @return List<Address> 地址列表
     */
    @Override
    public List<Address> getAddressList(String userId) {
        // 创建查询条件
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        // 设置用户ID条件
        queryWrapper.eq("user_id", userId);
        // 按创建时间倒序排序
        queryWrapper.orderBy(true, false, "create_time");
        // 执行查询
        return mapper.selectList(queryWrapper);
    }

    /**
     * 创建地址
     * 
     * 创建新的地址记录，自动设置创建时间。
     * 打印地址信息到控制台用于调试。
     * 
     * @param address 要创建的地址对象
     */
    @Override
    public void createAddress(Address address) {
        // 打印地址信息，用于调试
        System.out.println(address);
        // 设置创建时间
        address.setCreateTime(String.valueOf(System.currentTimeMillis()));
        // 插入地址记录
        mapper.insert(address);
    }

    /**
     * 删除地址
     * 
     * 根据地址ID删除指定的地址记录。
     * 
     * @param id 要删除的地址ID
     */
    @Override
    public void deleteAddress(String id) {
        mapper.deleteById(id);
    }

    /**
     * 更新地址
     * 
     * 更新指定地址的信息。
     * 使用地址ID作为更新条件。
     * 
     * @param address 包含更新信息的地址对象
     */
    @Override
    public void updateAddress(Address address) {
        mapper.updateById(address);
    }
}
