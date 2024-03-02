package com.wixis360.verifiedcontractingbackend.dto;

import lombok.Data;

@Data
public class UserFavouriteUserDto {
    private String id;
    private String userId;
    private String favouriteUserId;
}
