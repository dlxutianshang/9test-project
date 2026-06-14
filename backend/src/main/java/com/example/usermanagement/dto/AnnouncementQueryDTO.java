package com.example.usermanagement.dto;

import lombok.Data;

@Data
public class AnnouncementQueryDTO {

    private String title;

    private String creator;

    private String type;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
