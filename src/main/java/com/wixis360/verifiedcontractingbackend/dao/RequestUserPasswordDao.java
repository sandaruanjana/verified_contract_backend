package com.wixis360.verifiedcontractingbackend.dao;

import com.wixis360.verifiedcontractingbackend.model.RequestUserPassword;

import java.util.Optional;

public interface RequestUserPasswordDao {
    int save(RequestUserPassword requestUserPassword);
    Optional<RequestUserPassword> findByUserId(String userId);
    int deleteByUserId(String userId);
}
