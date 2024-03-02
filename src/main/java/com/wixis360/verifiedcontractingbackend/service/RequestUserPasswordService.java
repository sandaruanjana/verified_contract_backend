package com.wixis360.verifiedcontractingbackend.service;

import com.wixis360.verifiedcontractingbackend.dto.RequestUserPasswordDto;

public interface RequestUserPasswordService {
    RequestUserPasswordDto findByUserId(String userId);
    boolean deleteByUserId(String userId);
}
