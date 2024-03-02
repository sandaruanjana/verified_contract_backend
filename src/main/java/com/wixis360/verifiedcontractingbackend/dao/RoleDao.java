package com.wixis360.verifiedcontractingbackend.dao;

import com.wixis360.verifiedcontractingbackend.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleDao {
    List<Role> findAll();
    Optional<Role> findById(String id);
    Optional<Role> findByName(String name);
}
