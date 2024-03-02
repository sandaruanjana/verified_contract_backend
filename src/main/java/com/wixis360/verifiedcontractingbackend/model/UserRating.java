package com.wixis360.verifiedcontractingbackend.model;

import lombok.Data;

import java.util.Date;

@Data
public class UserRating {
    private String id;
    private String projectId;
    private String userId;
    private String revieweeUserId;
    private double rate;
    private String comment;
    private Date createdTime;
}
