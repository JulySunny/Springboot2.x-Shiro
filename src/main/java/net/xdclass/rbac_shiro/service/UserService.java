package net.xdclass.rbac_shiro.service;

import net.xdclass.rbac_shiro.domain.User;

/**
 * @Author: 杨强
 * @Date: 2019/10/10 22:11
 * @Version 1.0
 * @Discription
 */
public interface UserService {

    /**
     * 获取全部用户信息，包括角色，权限
     * @param username
     * @return
     */
    User findAllUserInfoByUsername(String username);


    /**
     * 获取用户基本信息
     * @param userId
     * @return
     */
    User findSimpleUserInfoById(int userId);


    /**
     * 根据用户名查找用户信息
     * @param username
     * @return
     */
    User findSimpleUserInfoByUsername(String username);

}
