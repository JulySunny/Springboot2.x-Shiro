package net.xdclass.rbac_shiro.controller;

import net.xdclass.rbac_shiro.domain.JsonData;
import net.xdclass.rbac_shiro.domain.UserQuery;
import net.xdclass.rbac_shiro.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: 杨强
 * @Date: 2019/10/10 22:10
 * @Version 1.0
 * @Discription
 */
@RestController
@RequestMapping("pub")
public class PublicController {

    @Autowired
    private UserService userService;


    /**
     * 没认证
     *
     * @return
     */
    @RequestMapping("need_login")
    public JsonData needLogin() {

        return JsonData.buildSuccess("温馨提示：请使用对应的账号登录", -2);

    }

    /**
     * 没权限
     *
     * @return
     */
    @RequestMapping("not_permit")
    public JsonData notPermit() {

        return JsonData.buildSuccess("温馨提示：拒绝访问，没权限", -3);
    }


    @RequestMapping("index")
    public JsonData index() {

        List<String> videoList = new ArrayList<>();
        videoList.add("Mysql零基础入门到实战 数据库教程");
        videoList.add("Redis高并发高可用集群百万级秒杀实战");
        videoList.add("Zookeeper+Dubbo视频教程 微服务教程分布式教程");
        videoList.add("2019年新版本RocketMQ4.X教程消息队列教程");
        videoList.add("微服务SpringCloud+Docker入门到高级实战");

        return JsonData.buildSuccess(videoList);
    }

    @PostMapping("login")
    public JsonData login(@RequestBody UserQuery userQuery, HttpServletRequest request,
                          HttpServletResponse response) {
        Subject subject = SecurityUtils.getSubject();
        HashMap<String, Object> info = new HashMap<>();

        try {
            UsernamePasswordToken usernamePasswordToken =
                    new UsernamePasswordToken(userQuery.getName(),userQuery.getPwd());
            subject.login(usernamePasswordToken);
            info.put("msg", "登录成功");
            info.put("session_id", subject.getSession().getId());

            return JsonData.buildSuccess(info);
        } catch (AuthenticationException e) {
            e.printStackTrace();

            return JsonData.buildError("账号或者密码错误");
        }
    }
}
