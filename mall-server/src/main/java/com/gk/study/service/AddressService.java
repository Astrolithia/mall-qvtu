package com.gk.study.service;

import com.gk.study.entity.Address;
import java.util.List;

/**
 * 地址服务接口
 *
 * 该接口定义了用户地址相关的所有业务操作。
 * 主要功能包括：
 * 1. 地址的创建、删除和更新
 * 2. 用户地址列表的获取
 *
 * 地址属性说明：
 * 1. 收货人姓名
 * 2. 联系电话
 * 3. 省市区地址
 * 4. 详细地址
 * 5. 是否默认地址
 *
 * 注意：
 * 1. 每个用户最多设置一个默认地址
 * 2. 地址删除需要检查是否被订单使用
 * 3. 地址更新需要处理默认地址的变更
 * 4. 地址信息需要支持三级联动
 *
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 * @see Address
 */
public interface AddressService {
    
    /**
     * 获取用户地址列表
     *
     * 查询指定用户的所有地址记录。
     * 返回结果按默认地址优先，创建时间倒序排列。
     *
     * @param userId 用户ID
     * @return List<Address> 地址列表，按默认地址优先排序
     * @throws IllegalArgumentException 当用户ID为空时抛出
     */
    List<Address> getAddressList(String userId);
    
    /**
     * 创建地址
     *
     * 创建新的地址记录。
     * 创建过程包括：
     * 1. 验证地址信息完整性
     * 2. 处理默认地址的设置
     * 3. 保存地址信息
     *
     * @param address 地址对象，包含地址详细信息
     * @throws IllegalArgumentException 当地址信息不完整时抛出
     */
    void createAddress(Address address);
    
    /**
     * 删除地址
     *
     * 删除指定的地址记录。
     * 删除前需要检查：
     * 1. 地址是否存在
     * 2. 地址是否被订单使用
     * 3. 是否为默认地址
     *
     * @param id 地址ID
     * @throws IllegalStateException 当地址被使用时抛出
     */
    void deleteAddress(String id);

    /**
     * 更新地址
     *
     * 更新地址信息。
     * 更新内容包括：
     * 1. 地址基本信息
     * 2. 默认地址状态
     * 3. 地址标签
     *
     * @param address 地址对象，包含更新信息
     * @throws IllegalArgumentException 当更新信息无效时抛出
     */
    void updateAddress(Address address);
}
