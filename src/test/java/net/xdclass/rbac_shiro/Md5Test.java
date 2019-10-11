package net.xdclass.rbac_shiro;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.junit.Test;

/**
 * @Author: 杨强
 * @Date: 2019/10/11 14:28
 * @Version 1.0
 * @Discription
 */
public class Md5Test {

    @Test
    public void testMd5() {
        //加密算法的名称
        String hashName = "md5";

//      二当家  String pwd = "123456";
//       大当家 String pwd = "123456789";
//       jack
        //密码明文
        String pwd = "123";
        //双重MD5加密
        //这里可以加salt盐--本次测试未加

        //使用shiro自带的api进行加密
        SimpleHash result = new SimpleHash(hashName, pwd, null, 2);

        System.out.println(result);
    }
}
