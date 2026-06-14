package com.example.usermanagement.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "岗位编码不能为空")
    @Size(max = 50, message = "岗位编码最多50个字符")
    private String postCode;

    @NotBlank(message = "岗位名称不能为空")
    @Size(max = 50, message = "岗位名称最多50个字符")
    private String postName;

    private Integer sortOrder;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer deleted;
}
