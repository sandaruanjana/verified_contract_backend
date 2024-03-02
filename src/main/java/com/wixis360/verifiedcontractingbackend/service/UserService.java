package com.wixis360.verifiedcontractingbackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wixis360.verifiedcontractingbackend.dto.UpdateUserPasswordDto;
import com.wixis360.verifiedcontractingbackend.dto.UserAbilitySkillDto;
import com.wixis360.verifiedcontractingbackend.dto.UserDto;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface UserService {
    UserDto save(UserDto userDto) throws IOException, MessagingException;
    UserDto verify(String id) throws JsonProcessingException;
    UserDto findById(String id);
    UserDto findByEmail(String email);
    List<UserDto> findAll();
    Page<UserDto> findAllBySortAndPage(Pageable page, String role, String search, String abilities, String skills);
    Page<UserDto> findAllByDistanceRange(Pageable page, String longitude, String latitude, int distance, String role, String search, String abilities, String skills);
    UserDto update(UserDto userDto);
    UserDto updateAbilitySkill(UserAbilitySkillDto userAbilitySkillDto);
    boolean updatePassword(UpdateUserPasswordDto updateUserPasswordDto);
    boolean forgotPassword(UserDto userDto) throws MessagingException, IOException;
    boolean resetPassword(UpdateUserPasswordDto updateUserPasswordDto);
}
