package com.example.usermanagement.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class PasswordReset implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private String resetToken;

    private LocalDateTime expireTime;

    private Integer used;

    private LocalDateTime createTime;
}
