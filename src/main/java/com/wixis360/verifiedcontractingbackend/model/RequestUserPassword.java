package com.wixis360.verifiedcontractingbackend.model;

import lombok.Data;

import java.util.Date;

@Data
public class RequestUserPassword {
    private String id;
    private String userId;
    private Date createdTime;
}
