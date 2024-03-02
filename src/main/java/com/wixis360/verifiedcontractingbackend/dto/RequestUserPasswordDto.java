package com.wixis360.verifiedcontractingbackend.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RequestUserPasswordDto {
    private String id;
    private String userId;
    private Date createdTime;
}
