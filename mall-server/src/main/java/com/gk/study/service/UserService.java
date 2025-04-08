package com.gk.study.service;
/**
 *  * @author Administrator
 *  * @date 2024-03-26
 */
import com.gk.study.entity.User;

import java.util.List;

/**
 * 用户服务接口
 * @author Administrator
 * @date 2024-03-26
 */
public interface UserService {
    List<User> getUserList(String keyword);
    User getAdminUser(User user);
    User getNormalUser(User user);
    void createUser(User user);
    void deleteUser(String id);

    void updateUser(User user);

    User getUserByToken(String token);
    User getUserByUserName(String username);

    User getUserDetail(String userId);
}
