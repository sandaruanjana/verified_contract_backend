package com.wixis360.verifiedcontractingbackend.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectBidDto {
    private String id;
    private String projectId;
    private String userId;
    private String userName;
    private String userTelephone;
    private double amount;
    private Date createdTime;
}
