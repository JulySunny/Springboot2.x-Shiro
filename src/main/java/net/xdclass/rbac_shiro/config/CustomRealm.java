package net.xdclass.rbac_shiro.config;

import net.xdclass.rbac_shiro.domain.Permission;
import net.xdclass.rbac_shiro.domain.Role;
import net.xdclass.rbac_shiro.domain.User;
import net.xdclass.rbac_shiro.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义的realm 数据域
 */

public class CustomRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    /**
     * 权限校验的时候会调用
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("授权 doGetAuthorizationInfo");


        //从token中获取用户信息,token代表用户输入
//        String username = (String) principals.getPrimaryPrincipal();
        User newUser = (User) principals.getPrimaryPrincipal();

//        使用原因？
//        授权的时候每次都去查询数据库，对于频繁访问的接口，性能和响应速度比较慢，所以使用缓存

        //提高性能的方法1-使用redis缓存:
        //      将信息放到缓存,例如redis,但是要设置缓存失效时间,因为可能更新数据库了,但是缓存没有更新
        //提高性能的方法2-使用shiro-redis集成的缓存:
        //      shiro-redis的缓存配置在SecurityManager中
        User user = userService.findAllUserInfoByUsername(newUser.getUsername());

        List<String> stringRoleList = new ArrayList<>();
        List<String> stringPermissionList = new ArrayList<>();

        List<Role> roleList = user.getRoleList();

        for (Role role : roleList) {
            stringRoleList.add(role.getName());

            List<Permission> permissionList = role.getPermissionList();

            for (Permission p : permissionList) {
                if (p != null) {
                    stringPermissionList.add(p.getName());
                }
            }
        }
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        //将用户对应的角色和权限信息 放到权限器中
        simpleAuthorizationInfo.addStringPermissions(stringPermissionList);
        simpleAuthorizationInfo.addRoles(stringRoleList);

        return simpleAuthorizationInfo;
    }

    /**
     * 用户登录的时候会调用
     *
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("认证 doGetAuthenticationInfo");

        //从token中获取用户信息,token代表用户输入
        String username = (String) token.getPrincipal();

        User user = userService.findAllUserInfoByUsername(username);

        //取密码
        String password = user.getPassword();

        if (password == null || "".equals(password)) {
            return null;
        }

        return new SimpleAuthenticationInfo(user, password, this.getClass().getName());
    }
}

/*
*       原有的问题
        class java.lang.String must has getter for field: authCacheKey or id\nWe need a
        field to identify this Cache Object in Redis. So you need to defined an id field
        which you can get unique id to identify this principal. For example, if you use
        UserInfo as Principal class, the id field maybe userId, userName, email, etc. For
        example, getUserId(), getUserName(), getEmail(), etc.\nDefault value is
        authCacheKey or id, that means your principal object has a method called
        \"getAuthCacheKey()\" or \"getId()\""
        改造原有的逻辑，修改缓存的唯一key

        doGetAuthorizationInfo 方法
        原有：
        String username = (String)principals.getPrimaryPrincipal();
        User user = userService.findAllUserInfoByUsername(username);
        改为
        User newUser = (User)principals.getPrimaryPrincipal();
        User user = userService.findAllUserInfoByUsername(newUser.getUsername());


        doGetAuthenticationInfo方法
        原有：
        return new SimpleAuthenticationInfo(username, user.getPassword(),
        this.getClass().getName());
        改为
        return new SimpleAuthenticationInfo(user, user.getPassword(),
        this.getClass().getName());
*
*
* */