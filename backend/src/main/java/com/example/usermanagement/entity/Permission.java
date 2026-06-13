package com.example.usermanagement.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "权限名称不能为空")
    @Size(max = 50, message = "权限名称最多50个字符")
    private String permissionName;

    @NotBlank(message = "权限编码不能为空")
    @Size(max = 100, message = "权限编码最多100个字符")
    private String permissionCode;

    private String description;

    private Integer type;

    private Long parentId;

    private Integer sortOrder;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private List<Permission> children;
}
