package net.xdclass.rbac_shiro.config;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * @Author: 杨强
 * @Date: 2019/10/11 14:44
 * @Version 1.0
 * @Discription
 */

/**
 * 自定义Filter
 * 问题:为什么要自定义filter
 */
public class CustomRoleFilter extends AuthorizationFilter {


    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {
        //获取当前访问路径所有角色的集合
        Subject subject = getSubject(request, response);
        String[] rolesArray = (String[]) mappedValue;

        //没有角色显示,可以直接访问
        if (rolesArray == null || rolesArray.length == 0) {
            //no roles specified, so nothing to check - allow access.
            return true;
        }


        //当前subject是roles中的任意一个,则有权限访问
        Set<String> roles = CollectionUtils.asSet(rolesArray);

        for (String role : roles) {
            if (subject.hasRole(role)) {
                return true;
            }
        }
        return subject.hasAllRoles(roles);
    }
}
