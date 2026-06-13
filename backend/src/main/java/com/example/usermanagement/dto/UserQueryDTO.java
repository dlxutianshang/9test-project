package com.example.usermanagement.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;

    private String nickname;

    private Integer status;

    private String email;

    private String phone;

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private Integer offset;

    public void calcOffset() {
        if (this.pageNum != null && this.pageSize != null && this.pageNum > 0) {
            this.offset = (this.pageNum - 1) * this.pageSize;
        }
    }
}
