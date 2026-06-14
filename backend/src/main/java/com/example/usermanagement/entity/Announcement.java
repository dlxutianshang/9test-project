package com.example.usermanagement.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Announcement implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "公告标题不能为空")
    @Size(max = 100, message = "公告标题最多100个字符")
    private String title;

    private String content;

    @NotBlank(message = "公告类型不能为空")
    private String type;

    private Integer status;

    private String creator;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer deleted;
}
