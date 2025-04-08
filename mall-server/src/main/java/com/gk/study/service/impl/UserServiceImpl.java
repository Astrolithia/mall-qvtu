package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.service.UserService;
import com.gk.study.entity.User;
import com.gk.study.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务实现类
 * 
 * 该类实现了用户相关的业务逻辑，包括：
 * 1. 用户列表的获取
 * 2. 用户的创建、删除和更新
 * 3. 用户登录验证（管理员用户和普通用户）
 * 4. 基于token和用户名的用户查询
 * 5. 用户详细信息获取
 * 
 * 使用MyBatis-Plus的ServiceImpl作为基类，简化了基础CRUD操作。
 * 系统中用户角色说明：
 * 1: 普通用户
 * >1: 管理员用户（数值越大权限越高）
 * 
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 用户数据访问对象
     * 用于执行用户相关的数据库操作
     */
    @Autowired
    UserMapper userMapper;

    /**
     * 获取用户列表
     * 
     * 查询所有用户记录，支持按用户名关键字模糊搜索。
     * 返回的列表按创建时间倒序排列。
     * 
     * @param keyword 搜索关键字（可选）
     * @return List<User> 用户列表
     */
    @Override
    public List<User> getUserList(String keyword) {
        // 创建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        // 如果提供了关键字，进行模糊查询
        if(StringUtils.isNotBlank(keyword)){
            // like查询
            queryWrapper.like("username", keyword);
        }
        // 按创建时间倒序排序
        queryWrapper.orderBy(true, false, "create_time");
        // 执行查询
        return userMapper.selectList(queryWrapper);
    }

    /**
     * 获取管理员用户
     * 
     * 根据用户名和密码查询管理员用户。
     * 管理员用户的角色值必须大于1。
     * 
     * @param user 包含用户名和密码的用户对象
     * @return User 管理员用户信息，如果不存在或不是管理员则返回null
     */
    @Override
    public User getAdminUser(User user) {
        // 创建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        // 设置用户名和密码条件
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", user.getPassword());
        // 设置角色条件，大于1为管理员
        queryWrapper.gt("role", "1");
        // 执行查询
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 获取普通用户
     * 
     * 根据用户名和密码查询普通用户。
     * 普通用户的角色值必须等于1。
     * 
     * @param user 包含用户名和密码的用户对象
     * @return User 普通用户信息，如果不存在或不是普通用户则返回null
     */
    @Override
    public User getNormalUser(User user) {
        // 创建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        // 设置用户名和密码条件
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", user.getPassword());
        // 设置角色条件，等于1为普通用户
        queryWrapper.eq("role", "1");
        // 执行查询
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 创建用户
     * 
     * 创建新的用户记录。
     * 
     * @param user 要创建的用户对象
     */
    @Override
    public void createUser(User user) {
        userMapper.insert(user);
    }

    /**
     * 删除用户
     * 
     * 根据用户ID删除指定的用户记录。
     * 
     * @param id 要删除的用户ID
     */
    @Override
    public void deleteUser(String id) {
        userMapper.deleteById(Long.parseLong(id));
    }

    /**
     * 更新用户
     * 
     * 更新指定用户的信息。
     * 使用用户ID作为更新条件。
     * 
     * @param user 包含更新信息的用户对象
     */
    @Override
    public void updateUser(User user) {
        userMapper.updateById(user);
    }

    /**
     * 根据Token获取用户
     * 
     * 通过用户的登录令牌查询用户信息。
     * 用于验证用户登录状态和身份。
     * 
     * @param token 用户登录令牌
     * @return User 用户信息，如果令牌无效则返回null
     */
    @Override
    public User getUserByToken(String token) {
        // 创建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        // 设置token条件
        queryWrapper.eq("token", token);
        // 执行查询
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 根据用户名获取用户
     * 
     * 通过用户名查询用户信息。
     * 用于用户注册时检查用户名是否已存在。
     * 
     * @param username 用户名
     * @return User 用户信息，如果用户名不存在则返回null
     */
    @Override
    public User getUserByUserName(String username) {
        // 创建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        // 设置用户名条件
        queryWrapper.eq("username", username);
        // 执行查询
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 获取用户详细信息
     * 
     * 根据用户ID查询用户的详细信息。
     * 
     * @param userId 用户ID
     * @return User 用户详细信息
     */
    @Override
    public User getUserDetail(String userId) {
        // 创建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        // 设置用户ID条件
        queryWrapper.eq("id", Long.parseLong(userId));
        // 执行查询
        return userMapper.selectOne(queryWrapper);
    }
}
