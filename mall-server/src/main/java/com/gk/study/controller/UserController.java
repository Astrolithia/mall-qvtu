package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.User;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 用户管理控制器
 * 提供用户相关的功能接口
 * 主要功能包括：
 * 1. 用户列表查询
 * 2. 用户详情查询
 * 3. 用户登录（后台和普通用户）
 * 4. 用户注册
 * 5. 用户信息更新
 * 6. 用户密码修改
 * 7. 用户头像上传
 * 
 * 注意：
 * 1. 部分操作需要管理员权限
 * 2. 部分操作需要用户登录权限
 * 3. 密码使用MD5加盐加密
 * 4. 头像上传支持文件存储
 */
@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * 日志记录器，用于记录系统运行日志
     */
    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * 密码加密盐值
     */
    String salt = "abcd1234";

    /**
     * 用户服务接口，用于处理用户相关的业务逻辑
     */
    @Autowired
    UserService userService;

    /**
     * 文件上传路径配置，从配置文件中读取
     */
    @Value("${File.uploadPath}")
    private String uploadPath;

    /**
     * 获取用户列表
     * 支持关键词搜索
     * 
     * @param keyword 搜索关键词
     * @return APIResponse 包含用户列表的响应对象
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String keyword){
        // 调用服务层获取用户列表
        List<User> list =  userService.getUserList(keyword);
        
        // 返回成功响应，包含用户列表数据
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /**
     * 获取用户详情
     * 
     * @param userId 用户ID
     * @return APIResponse 包含用户详情的响应对象
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(String userId){
        // 调用服务层获取用户详情
        User user =  userService.getUserDetail(userId);
        
        // 返回成功响应，包含用户详情数据
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", user);
    }

    /**
     * 后台管理员登录
     * 使用MD5加盐加密密码
     * 
     * @param user 用户对象，包含用户名和密码
     * @return APIResponse 操作结果响应
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public APIResponse login(User user){
        // 对密码进行MD5加盐加密
        user.setPassword(DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes()));
        
        // 获取管理员用户信息
        User responseUser =  userService.getAdminUser(user);
        
        // 验证登录结果
        if(responseUser != null) {
            return new APIResponse(ResponeCode.SUCCESS, "查询成功", responseUser);
        }else {
            return new APIResponse(ResponeCode.FAIL, "用户名或密码错误");
        }
    }

    /**
     * 普通用户登录
     * 使用MD5加盐加密密码
     * 
     * @param user 用户对象，包含用户名和密码
     * @return APIResponse 操作结果响应
     */
    @RequestMapping(value = "/userLogin", method = RequestMethod.POST)
    public APIResponse userLogin(User user){
        // 对密码进行MD5加盐加密
        user.setPassword(DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes()));
        
        // 获取普通用户信息
        User responseUser =  userService.getNormalUser(user);
        
        // 验证登录结果
        if(responseUser != null) {
            return new APIResponse(ResponeCode.SUCCESS, "查询成功", responseUser);
        }else {
            return new APIResponse(ResponeCode.FAIL, "用户名或密码错误");
        }
    }

    /**
     * 普通用户注册
     * 需要事务管理确保数据一致性
     * 包含完整的参数验证和错误处理
     * 
     * @param user 用户对象，包含注册信息
     * @return APIResponse 操作结果响应
     * @throws IOException 当头像上传过程中发生IO异常时抛出
     */
    @RequestMapping(value = "/userRegister", method = RequestMethod.POST)
    @Transactional
    public APIResponse userRegister(User user) throws IOException {
        // 记录注册开始日志
        logger.info("开始用户注册: username={}, password长度={}, rePassword长度={}", 
                    user.getUsername(), 
                    user.getPassword() != null ? user.getPassword().length() : 0,
                    user.getRePassword() != null ? user.getRePassword().length() : 0);
        
        try {
            // 打印完整的用户对象用于调试
            logger.info("用户对象: {}", user);
            
            // 验证用户名
            if (StringUtils.isEmpty(user.getUsername())) {
                logger.warn("用户名为空");
                return new APIResponse(ResponeCode.FAIL, "用户名不能为空");
            }
            
            // 验证密码
            if (StringUtils.isEmpty(user.getPassword())) {
                logger.warn("密码为空");
                return new APIResponse(ResponeCode.FAIL, "密码不能为空");
            }
            
            // 处理确认密码
            if (StringUtils.isEmpty(user.getRePassword())) {
                logger.warn("确认密码为空，使用密码值代替");
                user.setRePassword(user.getPassword());
            }
            
            // 检查用户名是否重复
            logger.info("检查用户名是否重复: {}", user.getUsername());
            if(userService.getUserByUserName(user.getUsername()) != null) {
                logger.warn("用户名重复: {}", user.getUsername());
                return new APIResponse(ResponeCode.FAIL, "用户名重复");
            }
            
            // 验证两次密码是否一致
            if(!user.getPassword().equals(user.getRePassword())) {
                logger.warn("密码不一致");
                return new APIResponse(ResponeCode.FAIL, "密码不一致");
            }
            
            // 对密码进行MD5加盐加密
            String md5Str = DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes());
            user.setPassword(md5Str);
            
            // 生成用户token
            md5Str = DigestUtils.md5DigestAsHex((user.getUsername() + salt).getBytes());
            user.setToken(md5Str);

            // 处理用户头像上传
            try {
                logger.info("保存用户头像");
                String avatar = saveAvatar(user);
                if(!StringUtils.isEmpty(avatar)) {
                    user.avatar = avatar;
                    logger.info("头像保存成功: {}", avatar);
                } else {
                    logger.info("没有头像或头像保存失败");
                }
            } catch (Exception e) {
                logger.error("保存头像时出错: {}", e.getMessage(), e);
                // 继续执行，不因头像错误阻断注册
            }
            
            // 设置用户角色和状态
            user.setRole(String.valueOf(User.NormalUser));
            user.setStatus("0");
            user.setCreateTime(String.valueOf(System.currentTimeMillis()));
            
            // 创建用户
            try {
                logger.info("创建用户: {}", user.getUsername());
                userService.createUser(user);
                logger.info("用户创建成功: {}", user.getUsername());
                return new APIResponse(ResponeCode.SUCCESS, "创建成功");
            } catch (Exception e) {
                logger.error("创建用户时出错: {}", e.getMessage(), e);
                throw e; // 重新抛出异常以触发事务回滚
            }
        } catch (Exception e) {
            logger.error("注册过程中出错: {}", e.getMessage(), e);
            return new APIResponse(ResponeCode.FAIL, "创建失败: " + e.getMessage());
        }
    }

    /**
     * 创建用户（管理员操作）
     * 需要管理员权限
     * 使用事务注解确保数据一致性
     * 
     * @param user 用户对象
     * @return APIResponse 操作结果响应
     * @throws IOException 当头像上传过程中发生IO异常时抛出
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(User user) throws IOException {
        // 验证用户名和密码
        if (!StringUtils.isEmpty(user.getUsername()) || !StringUtils.isEmpty(user.getPassword())) {
            // 检查用户名是否重复
            if(userService.getUserByUserName(user.getUsername()) != null) {
                return new APIResponse(ResponeCode.FAIL, "用户名重复");
            }
            
            // 对密码进行MD5加盐加密
            String md5Str = DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes());
            user.setPassword(md5Str);
            
            // 生成用户token
            md5Str = DigestUtils.md5DigestAsHex((user.getUsername() + salt).getBytes());
            user.setToken(md5Str);
            
            // 设置创建时间
            user.setCreateTime(String.valueOf(System.currentTimeMillis()));

            // 处理用户头像上传
            String avatar = saveAvatar(user);
            if(!StringUtils.isEmpty(avatar)) {
                user.avatar = avatar;
            }
            
            // 创建用户
            userService.createUser(user);
            return new APIResponse(ResponeCode.SUCCESS, "创建成功");
        }
        return new APIResponse(ResponeCode.FAIL, "创建失败");
    }

    /**
     * 批量删除用户
     * 需要管理员权限
     * 
     * @param ids 要删除的用户ID列表，多个ID用逗号分隔
     * @return APIResponse 操作结果响应
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        // 记录删除请求的用户ID
        System.out.println("ids===" + ids);
        
        // 将逗号分隔的ID字符串转换为数组
        String[] arr = ids.split(",");
        
        // 遍历ID数组，逐个删除用户
        for (String id : arr) {
            userService.deleteUser(id);
        }
        
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    /**
     * 更新用户信息
     * 需要管理员权限
     * 使用事务注解确保数据一致性
     * 不允许修改密码
     * 
     * @param user 用户对象
     * @return APIResponse 操作结果响应
     * @throws IOException 当头像上传过程中发生IO异常时抛出
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(User user) throws IOException {
        // 不允许修改密码
        user.setPassword(null);
        
        // 处理用户头像上传
        String avatar = saveAvatar(user);
        if(!StringUtils.isEmpty(avatar)) {
            user.avatar = avatar;
        }
        
        // 更新用户信息
        userService.updateUser(user);
        
        // 记录更新后的用户信息
        System.out.println(user);
        
        // 返回成功响应
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    /**
     * 更新用户信息（普通用户操作）
     * 需要用户登录权限
     * 使用事务注解确保数据一致性
     * 不允许修改用户名、密码和角色
     * 
     * @param user 用户对象
     * @return APIResponse 操作结果响应
     * @throws IOException 当头像上传过程中发生IO异常时抛出
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
    @Transactional
    public APIResponse updateUserInfo(User user) throws IOException {
        // 将Long类型的id转换为String
        String userId = String.valueOf(user.getId());
        
        // 获取用户详情
        User tmpUser = userService.getUserDetail(userId);
        
        // 验证用户角色
        if(tmpUser.getRole().equals(String.valueOf(User.NormalUser))){
            // 不允许修改用户名、密码和角色
            user.setUsername(null);
            user.setPassword(null);
            user.setRole(String.valueOf(User.NormalUser));
            
            // 处理用户头像上传
            String avatar = saveAvatar(user);
            if(!StringUtils.isEmpty(avatar)) {
                user.avatar = avatar;
            }
            
            // 更新用户信息
            userService.updateUser(user);
            return new APIResponse(ResponeCode.SUCCESS, "更新成功");
        }else {
            return new APIResponse(ResponeCode.FAIL, "非法操作");
        }
    }

    /**
     * 修改用户密码
     * 需要用户登录权限
     * 使用事务注解确保数据一致性
     * 需要验证原密码
     * 
     * @param userId 用户ID
     * @param password 原密码
     * @param newPassword 新密码
     * @return APIResponse 操作结果响应
     * @throws IOException 当操作过程中发生IO异常时抛出
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/updatePwd", method = RequestMethod.POST)
    @Transactional
    public APIResponse updatePwd(String userId, String password, String newPassword) throws IOException {
        // 获取用户详情
        User user =  userService.getUserDetail(userId);
        
        // 验证用户角色
        if(user.getRole().equals(String.valueOf(User.NormalUser))) {
            // 验证原密码
            String md5Pwd = DigestUtils.md5DigestAsHex((password + salt).getBytes());
            if(user.getPassword().equals(md5Pwd)){
                // 设置新密码
                user.setPassword(DigestUtils.md5DigestAsHex((newPassword + salt).getBytes()));
                userService.updateUser(user);
            }else {
                return new APIResponse(ResponeCode.FAIL, "原密码错误");
            }
            return new APIResponse(ResponeCode.SUCCESS, "更新成功");
        }else {
            return new APIResponse(ResponeCode.FAIL, "非法操作");
        }
    }

    /**
     * 保存用户头像
     * 处理头像文件的上传和存储
     * 
     * @param user 用户对象，包含要上传的头像文件
     * @return String 头像文件的URL
     * @throws IOException 当文件上传过程中发生IO异常时抛出
     */
    public String saveAvatar(User user) throws IOException {
        // 获取上传的头像文件
        MultipartFile file = user.getAvatarFile();
        String newFileName = null;
        
        try {
            // 如果文件存在且不为空，则进行上传处理
            if(file != null && !file.isEmpty()) {
                // 验证文件名
                String oldFileName = file.getOriginalFilename();
                if (oldFileName == null || !oldFileName.contains(".")) {
                    logger.warn("无效的文件名: {}", oldFileName);
                    return null;
                }
                
                // 生成新的文件名
                String randomStr = UUID.randomUUID().toString();
                newFileName = randomStr + oldFileName.substring(oldFileName.lastIndexOf("."));
                
                // 构建文件保存路径
                String filePath = uploadPath + File.separator + "avatar" + File.separator + newFileName;
                File destFile = new File(filePath);
                
                // 确保目标目录存在
                if(!destFile.getParentFile().exists()){
                    boolean mkdirResult = destFile.getParentFile().mkdirs();
                    if (!mkdirResult) {
                        logger.warn("创建目录失败: {}", destFile.getParentFile().getAbsolutePath());
                    }
                }
                
                // 保存文件
                file.transferTo(destFile);
                logger.info("头像保存成功: {}", filePath);
            } else {
                logger.info("没有上传头像文件");
            }
        } catch (Exception e) {
            logger.error("保存头像时出错: {}", e.getMessage(), e);
            return null; // 出错时返回null
        }
        
        return newFileName;
    }
}
