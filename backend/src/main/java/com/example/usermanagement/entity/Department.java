package com.example.usermanagement.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "部门名称不能为空")
    @Size(max = 50, message = "部门名称最多50个字符")
    private String deptName;

    private Long parentId;

    private String ancestors;

    private Integer sortOrder;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private List<Department> children;
}
