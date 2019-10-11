package net.xdclass.rbac_shiro.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 角色
 */
public class Role implements Serializable{


    private static final long serialVersionUID = -3017715475035751446L;
    private int id;

    private String name;

    private String description;

    /** 不单独写dto了,仅作为项目演示 */
    private List<Permission> permissionList;

    public List<Permission> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<Permission> permissionList) {
        this.permissionList = permissionList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
