package com.example.usermanagement.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DepartmentQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String deptName;

    private Integer status;

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private Integer offset;

    public void calcOffset() {
        if (this.pageNum != null && this.pageSize != null && this.pageNum > 0) {
            this.offset = (this.pageNum - 1) * this.pageSize;
        }
    }
}
