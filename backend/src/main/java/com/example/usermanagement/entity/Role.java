package com.example.usermanagement.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称最多50个字符")
    private String roleName;

    @NotBlank(message = "角色编码不能为空")
    @Size(max = 50, message = "角色编码最多50个字符")
    private String roleCode;

    private String description;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private List<Permission> permissions;

    private List<User> users;
}
