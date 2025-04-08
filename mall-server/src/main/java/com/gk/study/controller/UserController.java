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
 * 用户控制器
 * @author Administrator
 * @date 2024-03-26
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    String salt = "abcd1234";

    @Autowired
    UserService userService;

    @Value("${File.uploadPath}")
    private String uploadPath;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String keyword){
        List<User> list =  userService.getUserList(keyword);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(String userId){
        User user =  userService.getUserDetail(userId);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", user);
    }

    // 后台用户登录
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public APIResponse login(User user){
        user.setPassword(DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes()));
        User responseUser =  userService.getAdminUser(user);
        if(responseUser != null) {
            return new APIResponse(ResponeCode.SUCCESS, "查询成功", responseUser);
        }else {
            return new APIResponse(ResponeCode.FAIL, "用户名或密码错误");
        }
    }

    // 普通用户登录
    @RequestMapping(value = "/userLogin", method = RequestMethod.POST)
    public APIResponse userLogin(User user){
        user.setPassword(DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes()));
        User responseUser =  userService.getNormalUser(user);
        if(responseUser != null) {
            return new APIResponse(ResponeCode.SUCCESS, "查询成功", responseUser);
        }else {
            return new APIResponse(ResponeCode.FAIL, "用户名或密码错误");
        }
    }

    // 普通用户注册
    @RequestMapping(value = "/userRegister", method = RequestMethod.POST)
    @Transactional
    public APIResponse userRegister(User user) throws IOException {
        logger.info("开始用户注册: username={}, password长度={}, rePassword长度={}", 
                    user.getUsername(), 
                    user.getPassword() != null ? user.getPassword().length() : 0,
                    user.getRePassword() != null ? user.getRePassword().length() : 0);
        
        try {
            // 打印完整的用户对象用于调试
            logger.info("用户对象: {}", user);
            
            if (StringUtils.isEmpty(user.getUsername())) {
                logger.warn("用户名为空");
                return new APIResponse(ResponeCode.FAIL, "用户名不能为空");
            }
            
            if (StringUtils.isEmpty(user.getPassword())) {
                logger.warn("密码为空");
                return new APIResponse(ResponeCode.FAIL, "密码不能为空");
            }
            
            // 如果重复密码为空，则使用密码值
            if (StringUtils.isEmpty(user.getRePassword())) {
                logger.warn("确认密码为空，使用密码值代替");
                user.setRePassword(user.getPassword());
            }
            
            // 查重
            logger.info("检查用户名是否重复: {}", user.getUsername());
            if(userService.getUserByUserName(user.getUsername()) != null) {
                logger.warn("用户名重复: {}", user.getUsername());
                return new APIResponse(ResponeCode.FAIL, "用户名重复");
            }
            
            // 验证密码
            if(!user.getPassword().equals(user.getRePassword())) {
                logger.warn("密码不一致");
                return new APIResponse(ResponeCode.FAIL, "密码不一致");
            }
            
            String md5Str = DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes());
            // 设置密码
            user.setPassword(md5Str);
            md5Str = DigestUtils.md5DigestAsHex((user.getUsername() + salt).getBytes());
            // 设置token
            user.setToken(md5Str);

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
            
            // 设置角色
            user.setRole(String.valueOf(User.NormalUser));
            // 设置状态
            user.setStatus("0");
            user.setCreateTime(String.valueOf(System.currentTimeMillis()));
            
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

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(User user) throws IOException {

        if (!StringUtils.isEmpty(user.getUsername()) || !StringUtils.isEmpty(user.getPassword())) {
            // 查重
            if(userService.getUserByUserName(user.getUsername()) != null) {
                return new APIResponse(ResponeCode.FAIL, "用户名重复");
            }
            String md5Str = DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes());
            // 设置密码
            user.setPassword(md5Str);
            md5Str = DigestUtils.md5DigestAsHex((user.getUsername() + salt).getBytes());
            // 设置token
            user.setToken(md5Str);
            user.setCreateTime(String.valueOf(System.currentTimeMillis()));

            String avatar = saveAvatar(user);
            if(!StringUtils.isEmpty(avatar)) {
                user.avatar = avatar;
            }
            userService.createUser(user);
            return new APIResponse(ResponeCode.SUCCESS, "创建成功");
        }
        return new APIResponse(ResponeCode.FAIL, "创建失败");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        System.out.println("ids===" + ids);
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            userService.deleteUser(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(User user) throws IOException {
        // update不能修改密码，故置空
        user.setPassword(null);
        String avatar = saveAvatar(user);
        if(!StringUtils.isEmpty(avatar)) {
            user.avatar = avatar;
        }
        userService.updateUser(user);
        System.out.println(user);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }


    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
    @Transactional
    public APIResponse updateUserInfo(User user) throws IOException {
        // 将Long类型的id转换为String
        String userId = String.valueOf(user.getId());
        User tmpUser = userService.getUserDetail(userId);
        
        if(tmpUser.getRole().equals(String.valueOf(User.NormalUser))){
            // username和password不能改，故置空
            user.setUsername(null);
            user.setPassword(null);
            user.setRole(String.valueOf(User.NormalUser));
            String avatar = saveAvatar(user);
            if(!StringUtils.isEmpty(avatar)) {
                user.avatar = avatar;
            }
            userService.updateUser(user);
            return new APIResponse(ResponeCode.SUCCESS, "更新成功");
        }else {
            return new APIResponse(ResponeCode.FAIL, "非法操作");
        }
    }

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/updatePwd", method = RequestMethod.POST)
    @Transactional
    public APIResponse updatePwd(String userId, String password, String newPassword) throws IOException {
        User user =  userService.getUserDetail(userId);
        if(user.getRole().equals(String.valueOf(User.NormalUser))) {
            String md5Pwd = DigestUtils.md5DigestAsHex((password + salt).getBytes());
            if(user.getPassword().equals(md5Pwd)){
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

    public String saveAvatar(User user) throws IOException {
        MultipartFile file = user.getAvatarFile();
        String newFileName = null;
        try {
            if(file != null && !file.isEmpty()) {
                // 存文件
                String oldFileName = file.getOriginalFilename();
                if (oldFileName == null || !oldFileName.contains(".")) {
                    logger.warn("无效的文件名: {}", oldFileName);
                    return null;
                }
                
                String randomStr = UUID.randomUUID().toString();
                newFileName = randomStr + oldFileName.substring(oldFileName.lastIndexOf("."));
                
                // 使用跨平台的路径分隔符
                String filePath = uploadPath + File.separator + "avatar" + File.separator + newFileName;
                File destFile = new File(filePath);
                
                if(!destFile.getParentFile().exists()){
                    boolean mkdirResult = destFile.getParentFile().mkdirs();
                    if (!mkdirResult) {
                        logger.warn("创建目录失败: {}", destFile.getParentFile().getAbsolutePath());
                    }
                }
                
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
