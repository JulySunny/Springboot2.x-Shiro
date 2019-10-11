package net.xdclass.rbac_shiro.config;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;


@Configuration
public class ShiroConfig {

    /**
     * 配置ShiroFilterFactoryBean
     *
     * @param securityManager
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {

        System.out.println("执行 ShiroFilterFactoryBean shiroFilter");
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        //必须设置securityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        //需要登录的接口:如果访问某个接口,需要登录却没有登录,则调用此接口,如果前端后端不分离,则跳转到html页面
        shiroFilterFactoryBean.setLoginUrl("/pub/need_login");

        //登录成功 跳转url,如果前后端分离,则没这个调用 --这里设置为首页就行了
        shiroFilterFactoryBean.setSuccessUrl("/");

        //登录成功,但是没有权限,未授权就会调用这个接口,如果不是前后端分离,则跳转到403页面
        shiroFilterFactoryBean.setUnauthorizedUrl("/pub/not_permit");


        //设置自定义filter
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("roleOrFilter", new CustomRoleFilter());

        //shiroFilterFactoryBean绑定自定义的filter
        shiroFilterFactoryBean.setFilters(filterMap);


        //过滤器链的map
        //拦截器(过滤器路径,坑一必须要用LinkedhashMap),部分路径无法进行拦截
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

        /*
        ********************************************
        通常是配置这些过滤器,也可以用个数据库的动态加载,这些数据
         ********************************************/
        //退出过滤器
        filterChainDefinitionMap.put("/logout", "logout");

        //匿名可以访问,也就是游客模式
        filterChainDefinitionMap.put("/pub/**", "anon");

        //登录用户才可以访问
        filterChainDefinitionMap.put("/authc/**", "authc");

        //管理员角色才可以访问
        filterChainDefinitionMap.put("/admin/**", "roles[admin]");

        //有编辑权限才可以访问
        filterChainDefinitionMap.put("/video/update", "perms[video_update]");

        //坑二:过滤器是顺序执行,从上而下,一般来说/** 放到最下面

        //authc: url定义必须通过认证才可以访问
        //anno:  url可以匿名访问
        filterChainDefinitionMap.put("/**", "authc");


        //配置过滤器
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilterFactoryBean;
    }

    /**
     * 安全管理器 SecurityManager
     *
     * @return
     */
    @Bean
    public SecurityManager securityManager() {

        //注意这里是DefaultWebSecurityManager
        DefaultWebSecurityManager defaultSecurityManager = new DefaultWebSecurityManager();

        //必须要这这种方式 设置,直接new SessionManager 不行
        //如果不是前后端分离, 就不用设置设置这个,也不用配置Bean
        defaultSecurityManager.setSessionManager(sessionManager());

        //使用自定义的cachaManager
        defaultSecurityManager.setCacheManager(redisCacheManager());

        //必须要这这种方式 设置,直接new CustomRealm 不行
        //设置realm(推荐放到最后,不然某些情况会不生效)
        defaultSecurityManager.setRealm(customRealm());
        return defaultSecurityManager;
    }

    /**
     * 数据域
     *
     * @return
     */
    @Bean
    public CustomRealm customRealm() {

        CustomRealm customRealm = new CustomRealm();

        //设置加密器--因为数据库中的密码不是明文存储
        customRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return customRealm;
    }

    /**
     * 自定义seesionManager
     *      -配置session持久化
     * @return
     */
    @Bean
    public SessionManager sessionManager() {
        //自定义CustomSessionManager 继承 DefaultWebSessionManager
        CustomSessionManager customSessionManager = new CustomSessionManager();

        //配置session持久化
        customSessionManager.setSessionDAO(redisSessionDAO());

        //超时时间，默认 30分钟，会话超时；方法里面的单位是毫秒
        customSessionManager.setGlobalSessionTimeout(20000);

        return customSessionManager;
    }

    /**
     * 密码加解密规则 CredentialMatcher
     *
     * @return
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();

        //设置散列算法:这里使用MD5算法
        hashedCredentialsMatcher.setHashAlgorithmName("md5");

        //散列次数,好比散列两次 相当于md5(md5(x))
        hashedCredentialsMatcher.setHashIterations(2);

        return hashedCredentialsMatcher;
    }

    /**
     * 配置redisManager
     */
    @Bean
    public RedisManager getRedisManager() {

        RedisManager redisManager = new RedisManager();

        //默认就是localhost:6379 不写也行
        redisManager.setHost("localhost");
        redisManager.setPort(6379);
        return redisManager;
    }

    /**
     * 配置具体cache实现类
     *
     * @return
     */
    public RedisCacheManager redisCacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(getRedisManager());
        //设置过期时间，单位是秒，20s,
        redisCacheManager.setExpire(20);
        return redisCacheManager;
    }

    /**
     * 自定义session持久化
     *
     * @return
     */
    public RedisSessionDAO redisSessionDAO() {

        /*
          为啥session也要持久化？
                重启应用，用户无感知，可以继续以原先的状态继续访问
          注意点：
                DO对象需要实现序列化接口 Serializable
                logout接口和以前一样调用，请求logout后会删除redis里面的对应的key,即删除对应的token
         */
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(getRedisManager());

        //配置自定义sessionId,shiro自动生成色sessionId不满足条件时可以使用
        redisSessionDAO.setSessionIdGenerator(new CustomSessionIdGenerator());
        return redisSessionDAO;
    }

    /**
     * LifecycleBeanPostProcessor
     * 管理shiro一些bean的生命周期 即bean初始化 与销毁
     *
     * @return
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * AuthorizationAttributeSourceAdvisor
     * 作用：加入shiro注解的使用，不加入这个AOP注解不生效(shiro的注解 例如 @RequiresGuest)
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new
                AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * DefaultAdvisorAutoProxyCreator
     * 作用: 用来扫描上下文寻找所有的Advistor(通知器), 将符合条件的Advisor应用到切入点的Bean中，需
     * 要在LifecycleBeanPostProcessor创建后才可以创建
     *
     * @return
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new
                DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setUsePrefix(true);
        return defaultAdvisorAutoProxyCreator;
    }
}
