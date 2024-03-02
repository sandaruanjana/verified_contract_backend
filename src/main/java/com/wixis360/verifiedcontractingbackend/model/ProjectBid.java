package com.wixis360.verifiedcontractingbackend.model;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectBid {
    private String id;
    private String projectId;
    private String userId;
    private String userName;
    private String userTelephone;
    private double amount;
    private Date createdTime;
}
