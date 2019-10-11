package net.xdclass.rbac_shiro.config;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;

import java.io.Serializable;
import java.util.UUID;

/**
 * @Author: 杨强
 * @Date: 2019/10/11 17:11
 * @Version 1.0
 * @Discription 自定义SessionId生成器
 */
public class CustomSessionIdGenerator implements SessionIdGenerator {
    @Override
    public Serializable generateId(Session session) {
        //可以使用更加复杂的,例如加解密算法等等算法
        return "xdclass" + UUID.randomUUID().toString().replace("-", "");
    }
}
