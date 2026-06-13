package com.example.usermanagement.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private String username;

    private String operation;

    private String method;

    private String params;

    private String ip;

    private Long duration;

    private Integer status;

    private String errorMsg;

    private LocalDateTime createTime;
}
