package com.wixis360.verifiedcontractingbackend.model;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private String id;
    private String roleId;
    private String name;
    private String profilePicture;
    private String email;
    private String password;
    private String telephone;
    private String addressLine1;
    private String addressLine2;
    private String zipCode;
    private String longitude;
    private String latitude;
    private String smallInfo;
    private String bio;
    private String facebookUrl;
    private String twitterUrl;
    private String linkedInUrl;
    private Date createdTime;
    private int isEnabled;
}
