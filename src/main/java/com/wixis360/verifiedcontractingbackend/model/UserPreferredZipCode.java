package com.wixis360.verifiedcontractingbackend.model;

import lombok.Data;

@Data
public class UserPreferredZipCode {
    private String id;
    private String userId;
    private String zipCode;
    private String longitude;
    private String latitude;
}
