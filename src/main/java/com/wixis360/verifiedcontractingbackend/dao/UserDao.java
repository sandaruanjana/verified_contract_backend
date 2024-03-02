package com.wixis360.verifiedcontractingbackend.dao;

import com.wixis360.verifiedcontractingbackend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    int save(User user);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    Page<User> findAllBySortAndPage(Pageable page, String role, String search, String abilities, String skills);
    Page<User> findAllByDistanceRange(Pageable page, String longitude, String latitude, int distance, String role, String search, String abilities, String skills);
    int update(User user);
    int updatePassword(String id,String password);
}
