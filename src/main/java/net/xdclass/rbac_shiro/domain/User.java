package net.xdclass.rbac_shiro.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户
 */
public class User implements Serializable {
    private static final long serialVersionUID = -8430087586420925524L;
    private int id;

    private String username;

    private String password;

    private Date createTime;

    private String salt;


    /**
     * 角色集合 --不单独写dto了,仅作为项目演示
     */
    private List<Role> roleList;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
