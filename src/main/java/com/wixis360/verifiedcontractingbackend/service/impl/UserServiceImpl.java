package com.wixis360.verifiedcontractingbackend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wixis360.verifiedcontractingbackend.dao.*;
import com.wixis360.verifiedcontractingbackend.dto.*;
import com.wixis360.verifiedcontractingbackend.model.*;
import com.wixis360.verifiedcontractingbackend.security.service.UserDetailsImpl;
import com.wixis360.verifiedcontractingbackend.service.EmailService;
import com.wixis360.verifiedcontractingbackend.service.UserService;
import com.wixis360.verifiedcontractingbackend.util.Utility;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final RedisCached redisCached;
    private final UserDao userDao;
    private final UserAbilityDao userAbilityDao;
    private final UserSkillDao userSkillDao;
    private final UserPreferredZipCodeDao userPreferredZipCodeDao;
    private final UserFavouriteUserDao userFavouriteUserDao;
    private final RoleDao roleDao;
    private final RequestUserPasswordDao requestUserPasswordDao;
    private final EmailService emailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ModelMapper mapper;
    @Value("${profile.picture.upload.path}")
    private String profilePictureUploadPath;
    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public UserDto save(UserDto userDto) throws IOException, MessagingException {
        Optional<User> optionalUser = userDao.findByEmail(userDto.getEmail());

        if (optionalUser.isPresent()) {
            return null;
        }

        userDto.setId(UUID.randomUUID().toString());
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] bytes = objectMapper.writeValueAsBytes(userDto);
        redisCached.updateCached(("USER_" + userDto.getId()).getBytes(), bytes, (long) (60 * 60 * 48));

        String trHtml = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\" style=\"font-family: sans-serif; -ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Password Reset</title>\n" +
                "    <style type=\"text/css\" data-styled-components=\"\" data-styled-components-is-local=\"false\">\n" +
                "        @media only screen and (min-width:30em) {\n" +
                "            .container {\n" +
                "                width: 31rem;\n" +
                "            }\n" +
                "            .col-sm,\n" +
                "            .col-sm-1,\n" +
                "            .col-sm-10,\n" +
                "            .col-sm-11,\n" +
                "            .col-sm-12,\n" +
                "            .col-sm-2,\n" +
                "            .col-sm-3,\n" +
                "            .col-sm-4,\n" +
                "            .col-sm-5,\n" +
                "            .col-sm-6,\n" +
                "            .col-sm-7,\n" +
                "            .col-sm-8,\n" +
                "            .col-sm-9,\n" +
                "            .col-sm-offset-1,\n" +
                "            .col-sm-offset-10,\n" +
                "            .col-sm-offset-11,\n" +
                "            .col-sm-offset-12,\n" +
                "            .col-sm-offset-2,\n" +
                "            .col-sm-offset-3,\n" +
                "            .col-sm-offset-4,\n" +
                "            .col-sm-offset-5,\n" +
                "            .col-sm-offset-6,\n" +
                "            .col-sm-offset-7,\n" +
                "            .col-sm-offset-8,\n" +
                "            .col-sm-offset-9 {\n" +
                "                box-sizing: border-box;\n" +
                "                -webkit-box-flex: 0;\n" +
                "                -webkit-flex: 0 0 auto;\n" +
                "                -ms-flex: 0 0 auto;\n" +
                "                -webkit-flex: 0 0 auto;\n" +
                "                -ms-flex: 0 0 auto;\n" +
                "                flex: 0 0 auto;\n" +
                "                padding-right: .5rem;\n" +
                "                padding-left: .5rem;\n" +
                "            }\n" +
                "            .col-sm {\n" +
                "                -webkit-box-flex: 1;\n" +
                "                -webkit-flex-grow: 1;\n" +
                "                -ms-flex-positive: 1;\n" +
                "                -webkit-flex-grow: 1;\n" +
                "                -ms-flex-grow: 1;\n" +
                "                flex-grow: 1;\n" +
                "                -webkit-flex-basis: 0;\n" +
                "                -ms-flex-preferred-size: 0;\n" +
                "                -webkit-flex-basis: 0;\n" +
                "                -ms-flex-basis: 0;\n" +
                "                flex-basis: 0;\n" +
                "                max-width: 100%;\n" +
                "            }\n" +
                "            .col-sm-1 {\n" +
                "                -webkit-flex-basis: 8.333%;\n" +
                "                -ms-flex-preferred-size: 8.333%;\n" +
                "                -webkit-flex-basis: 8.333%;\n" +
                "                -ms-flex-basis: 8.333%;\n" +
                "                flex-basis: 8.333%;\n" +
                "                max-width: 8.333%;\n" +
                "            }\n" +
                "            .col-sm-2 {\n" +
                "                -webkit-flex-basis: 16.667%;\n" +
                "                -ms-flex-preferred-size: 16.667%;\n" +
                "                -webkit-flex-basis: 16.667%;\n" +
                "                -ms-flex-basis: 16.667%;\n" +
                "                flex-basis: 16.667%;\n" +
                "                max-width: 16.667%;\n" +
                "            }\n" +
                "            .col-sm-3 {\n" +
                "                -webkit-flex-basis: 25%;\n" +
                "                -ms-flex-preferred-size: 25%;\n" +
                "                -webkit-flex-basis: 25%;\n" +
                "                -ms-flex-basis: 25%;\n" +
                "                flex-basis: 25%;\n" +
                "                max-width: 25%;\n" +
                "            }\n" +
                "            .col-sm-4 {\n" +
                "                -webkit-flex-basis: 33.333%;\n" +
                "                -ms-flex-preferred-size: 33.333%;\n" +
                "                -webkit-flex-basis: 33.333%;\n" +
                "                -ms-flex-basis: 33.333%;\n" +
                "                flex-basis: 33.333%;\n" +
                "                max-width: 33.333%;\n" +
                "            }\n" +
                "            .col-sm-5 {\n" +
                "                -webkit-flex-basis: 41.667%;\n" +
                "                -ms-flex-preferred-size: 41.667%;\n" +
                "                -webkit-flex-basis: 41.667%;\n" +
                "                -ms-flex-basis: 41.667%;\n" +
                "                flex-basis: 41.667%;\n" +
                "                max-width: 41.667%;\n" +
                "            }\n" +
                "            .col-sm-6 {\n" +
                "                -webkit-flex-basis: 50%;\n" +
                "                -ms-flex-preferred-size: 50%;\n" +
                "                -webkit-flex-basis: 50%;\n" +
                "                -ms-flex-basis: 50%;\n" +
                "                flex-basis: 50%;\n" +
                "                max-width: 50%;\n" +
                "            }\n" +
                "            .col-sm-7 {\n" +
                "                -webkit-flex-basis: 58.333%;\n" +
                "                -ms-flex-preferred-size: 58.333%;\n" +
                "                -webkit-flex-basis: 58.333%;\n" +
                "                -ms-flex-basis: 58.333%;\n" +
                "                flex-basis: 58.333%;\n" +
                "                max-width: 58.333%;\n" +
                "            }\n" +
                "            .col-sm-8 {\n" +
                "                -webkit-flex-basis: 66.667%;\n" +
                "                -ms-flex-preferred-size: 66.667%;\n" +
                "                -webkit-flex-basis: 66.667%;\n" +
                "                -ms-flex-basis: 66.667%;\n" +
                "                flex-basis: 66.667%;\n" +
                "                max-width: 66.667%;\n" +
                "            }\n" +
                "            .col-sm-9 {\n" +
                "                -webkit-flex-basis: 75%;\n" +
                "                -ms-flex-preferred-size: 75%;\n" +
                "                -webkit-flex-basis: 75%;\n" +
                "                -ms-flex-basis: 75%;\n" +
                "                flex-basis: 75%;\n" +
                "                max-width: 75%;\n" +
                "            }\n" +
                "            .col-sm-10 {\n" +
                "                -webkit-flex-basis: 83.333%;\n" +
                "                -ms-flex-preferred-size: 83.333%;\n" +
                "                -webkit-flex-basis: 83.333%;\n" +
                "                -ms-flex-basis: 83.333%;\n" +
                "                flex-basis: 83.333%;\n" +
                "                max-width: 83.333%;\n" +
                "            }\n" +
                "            .col-sm-11 {\n" +
                "                -webkit-flex-basis: 91.667%;\n" +
                "                -ms-flex-preferred-size: 91.667%;\n" +
                "                -webkit-flex-basis: 91.667%;\n" +
                "                -ms-flex-basis: 91.667%;\n" +
                "                flex-basis: 91.667%;\n" +
                "                max-width: 91.667%;\n" +
                "            }\n" +
                "            .col-sm-12 {\n" +
                "                -webkit-flex-basis: 100%;\n" +
                "                -ms-flex-preferred-size: 100%;\n" +
                "                -webkit-flex-basis: 100%;\n" +
                "                -ms-flex-basis: 100%;\n" +
                "                flex-basis: 100%;\n" +
                "                max-width: 100%;\n" +
                "            }\n" +
                "            .col-sm-offset-1 {\n" +
                "                margin-left: 8.333%;\n" +
                "            }\n" +
                "            .col-sm-offset-2 {\n" +
                "                margin-left: 16.667%;\n" +
                "            }\n" +
                "            .col-sm-offset-3 {\n" +
                "                margin-left: 25%;\n" +
                "            }\n" +
                "            .col-sm-offset-4 {\n" +
                "                margin-left: 33.333%;\n" +
                "            }\n" +
                "            .col-sm-offset-5 {\n" +
                "                margin-left: 41.667%;\n" +
                "            }\n" +
                "            .col-sm-offset-6 {\n" +
                "                margin-left: 50%;\n" +
                "            }\n" +
                "            .col-sm-offset-7 {\n" +
                "                margin-left: 58.333%;\n" +
                "            }\n" +
                "            .col-sm-offset-8 {\n" +
                "                margin-left: 66.667%;\n" +
                "            }\n" +
                "            .col-sm-offset-9 {\n" +
                "                margin-left: 75%;\n" +
                "            }\n" +
                "            .col-sm-offset-10 {\n" +
                "                margin-left: 83.333%;\n" +
                "            }\n" +
                "            .col-sm-offset-11 {\n" +
                "                margin-left: 91.667%;\n" +
                "            }\n" +
                "            .start-sm {\n" +
                "                -webkit-box-pack: start;\n" +
                "                -webkit-justify-content: flex-start;\n" +
                "                -ms-flex-pack: start;\n" +
                "                -webkit-box-pack: start;\n" +
                "                -webkit-justify-content: flex-start;\n" +
                "                -ms-flex-pack: start;\n" +
                "                justify-content: flex-start;\n" +
                "                text-align: start;\n" +
                "            }\n" +
                "            .center-sm {\n" +
                "                -webkit-box-pack: center;\n" +
                "                -webkit-justify-content: center;\n" +
                "                -ms-flex-pack: center;\n" +
                "                -webkit-box-pack: center;\n" +
                "                -webkit-justify-content: center;\n" +
                "                -ms-flex-pack: center;\n" +
                "                justify-content: center;\n" +
                "                text-align: center;\n" +
                "            }\n" +
                "            .end-sm {\n" +
                "                -webkit-box-pack: end;\n" +
                "                -webkit-justify-content: flex-end;\n" +
                "                -ms-flex-pack: end;\n" +
                "                -webkit-box-pack: end;\n" +
                "                -webkit-justify-content: flex-end;\n" +
                "                -ms-flex-pack: end;\n" +
                "                justify-content: flex-end;\n" +
                "                text-align: end;\n" +
                "            }\n" +
                "            .top-sm {\n" +
                "                -webkit-box-align: start;\n" +
                "                -webkit-align-items: flex-start;\n" +
                "                -ms-flex-align: start;\n" +
                "                -webkit-align-items: flex-start;\n" +
                "                -webkit-box-align: flex-start;\n" +
                "                -ms-flex-align: flex-start;\n" +
                "                align-items: flex-start;\n" +
                "            }\n" +
                "            .middle-sm {\n" +
                "                -webkit-box-align: center;\n" +
                "                -webkit-align-items: center;\n" +
                "                -ms-flex-align: center;\n" +
                "                -webkit-align-items: center;\n" +
                "                -webkit-box-align: center;\n" +
                "                -ms-flex-align: center;\n" +
                "                align-items: center;\n" +
                "            }\n" +
                "            .bottom-sm {\n" +
                "                -webkit-box-align: end;\n" +
                "                -webkit-align-items: flex-end;\n" +
                "                -ms-flex-align: end;\n" +
                "                -webkit-align-items: flex-end;\n" +
                "                -webkit-box-align: flex-end;\n" +
                "                -ms-flex-align: flex-end;\n" +
                "                align-items: flex-end;\n" +
                "            }\n" +
                "            .around-sm {\n" +
                "                -webkit-justify-content: space-around;\n" +
                "                -ms-flex-pack: distribute;\n" +
                "                -webkit-box-pack: space-around;\n" +
                "                -webkit-justify-content: space-around;\n" +
                "                -ms-flex-pack: space-around;\n" +
                "                justify-content: space-around;\n" +
                "            }\n" +
                "            .between-sm {\n" +
                "                -webkit-box-pack: justify;\n" +
                "                -webkit-justify-content: space-between;\n" +
                "                -ms-flex-pack: justify;\n" +
                "                -webkit-box-pack: space-between;\n" +
                "                -webkit-justify-content: space-between;\n" +
                "                -ms-flex-pack: space-between;\n" +
                "                justify-content: space-between;\n" +
                "            }\n" +
                "            .first-sm {\n" +
                "                -webkit-box-ordinal-group: 0;\n" +
                "                -webkit-order: -1;\n" +
                "                -ms-flex-order: -1;\n" +
                "                -webkit-order: -1;\n" +
                "                -ms-flex-order: -1;\n" +
                "                order: -1;\n" +
                "            }\n" +
                "            .last-sm {\n" +
                "                -webkit-box-ordinal-group: 2;\n" +
                "                -webkit-order: 1;\n" +
                "                -ms-flex-order: 1;\n" +
                "                -webkit-order: 1;\n" +
                "                -ms-flex-order: 1;\n" +
                "                order: 1;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media only screen and (min-width:48em) {\n" +
                "            .container {\n" +
                "                width: 49rem;\n" +
                "            }\n" +
                "            .col-md,\n" +
                "            .col-md-1,\n" +
                "            .col-md-10,\n" +
                "            .col-md-11,\n" +
                "            .col-md-12,\n" +
                "            .col-md-2,\n" +
                "            .col-md-3,\n" +
                "            .col-md-4,\n" +
                "            .col-md-5,\n" +
                "            .col-md-6,\n" +
                "            .col-md-7,\n" +
                "            .col-md-8,\n" +
                "            .col-md-9,\n" +
                "            .col-md-offset-1,\n" +
                "            .col-md-offset-10,\n" +
                "            .col-md-offset-11,\n" +
                "            .col-md-offset-12,\n" +
                "            .col-md-offset-2,\n" +
                "            .col-md-offset-3,\n" +
                "            .col-md-offset-4,\n" +
                "            .col-md-offset-5,\n" +
                "            .col-md-offset-6,\n" +
                "            .col-md-offset-7,\n" +
                "            .col-md-offset-8,\n" +
                "            .col-md-offset-9 {\n" +
                "                box-sizing: border-box;\n" +
                "                -webkit-box-flex: 0;\n" +
                "                -webkit-flex: 0 0 auto;\n" +
                "                -ms-flex: 0 0 auto;\n" +
                "                -webkit-flex: 0 0 auto;\n" +
                "                -ms-flex: 0 0 auto;\n" +
                "                flex: 0 0 auto;\n" +
                "                padding-right: .5rem;\n" +
                "                padding-left: .5rem;\n" +
                "            }\n" +
                "            .col-md {\n" +
                "                -webkit-box-flex: 1;\n" +
                "                -webkit-flex-grow: 1;\n" +
                "                -ms-flex-positive: 1;\n" +
                "                -webkit-flex-grow: 1;\n" +
                "                -ms-flex-grow: 1;\n" +
                "                flex-grow: 1;\n" +
                "                -webkit-flex-basis: 0;\n" +
                "                -ms-flex-preferred-size: 0;\n" +
                "                -webkit-flex-basis: 0;\n" +
                "                -ms-flex-basis: 0;\n" +
                "                flex-basis: 0;\n" +
                "                max-width: 100%;\n" +
                "            }\n" +
                "            .col-md-1 {\n" +
                "                -webkit-flex-basis: 8.333%;\n" +
                "                -ms-flex-preferred-size: 8.333%;\n" +
                "                -webkit-flex-basis: 8.333%;\n" +
                "                -ms-flex-basis: 8.333%;\n" +
                "                flex-basis: 8.333%;\n" +
                "                max-width: 8.333%;\n" +
                "            }\n" +
                "            .col-md-2 {\n" +
                "                -webkit-flex-basis: 16.667%;\n" +
                "                -ms-flex-preferred-size: 16.667%;\n" +
                "                -webkit-flex-basis: 16.667%;\n" +
                "                -ms-flex-basis: 16.667%;\n" +
                "                flex-basis: 16.667%;\n" +
                "                max-width: 16.667%;\n" +
                "            }\n" +
                "            .col-md-3 {\n" +
                "                -webkit-flex-basis: 25%;\n" +
                "                -ms-flex-preferred-size: 25%;\n" +
                "                -webkit-flex-basis: 25%;\n" +
                "                -ms-flex-basis: 25%;\n" +
                "                flex-basis: 25%;\n" +
                "                max-width: 25%;\n" +
                "            }\n" +
                "            .col-md-4 {\n" +
                "                -webkit-flex-basis: 33.333%;\n" +
                "                -ms-flex-preferred-size: 33.333%;\n" +
                "                -webkit-flex-basis: 33.333%;\n" +
                "                -ms-flex-basis: 33.333%;\n" +
                "                flex-basis: 33.333%;\n" +
                "                max-width: 33.333%;\n" +
                "            }\n" +
                "            .col-md-5 {\n" +
                "                -webkit-flex-basis: 41.667%;\n" +
                "                -ms-flex-preferred-size: 41.667%;\n" +
                "                -webkit-flex-basis: 41.667%;\n" +
                "                -ms-flex-basis: 41.667%;\n" +
                "                flex-basis: 41.667%;\n" +
                "                max-width: 41.667%;\n" +
                "            }\n" +
                "            .col-md-6 {\n" +
                "                -webkit-flex-basis: 50%;\n" +
                "                -ms-flex-preferred-size: 50%;\n" +
                "                -webkit-flex-basis: 50%;\n" +
                "                -ms-flex-basis: 50%;\n" +
                "                flex-basis: 50%;\n" +
                "                max-width: 50%;\n" +
                "            }\n" +
                "            .col-md-7 {\n" +
                "                -webkit-flex-basis: 58.333%;\n" +
                "                -ms-flex-preferred-size: 58.333%;\n" +
                "                -webkit-flex-basis: 58.333%;\n" +
                "                -ms-flex-basis: 58.333%;\n" +
                "                flex-basis: 58.333%;\n" +
                "                max-width: 58.333%;\n" +
                "            }\n" +
                "            .col-md-8 {\n" +
                "                -webkit-flex-basis: 66.667%;\n" +
                "                -ms-flex-preferred-size: 66.667%;\n" +
                "                -webkit-flex-basis: 66.667%;\n" +
                "                -ms-flex-basis: 66.667%;\n" +
                "                flex-basis: 66.667%;\n" +
                "                max-width: 66.667%;\n" +
                "            }\n" +
                "            .col-md-9 {\n" +
                "                -webkit-flex-basis: 75%;\n" +
                "                -ms-flex-preferred-size: 75%;\n" +
                "                -webkit-flex-basis: 75%;\n" +
                "                -ms-flex-basis: 75%;\n" +
                "                flex-basis: 75%;\n" +
                "                max-width: 75%;\n" +
                "            }\n" +
                "            .col-md-10 {\n" +
                "                -webkit-flex-basis: 83.333%;\n" +
                "                -ms-flex-preferred-size: 83.333%;\n" +
                "                -webkit-flex-basis: 83.333%;\n" +
                "                -ms-flex-basis: 83.333%;\n" +
                "                flex-basis: 83.333%;\n" +
                "                max-width: 83.333%;\n" +
                "            }\n" +
                "            .col-md-11 {\n" +
                "                -webkit-flex-basis: 91.667%;\n" +
                "                -ms-flex-preferred-size: 91.667%;\n" +
                "                -webkit-flex-basis: 91.667%;\n" +
                "                -ms-flex-basis: 91.667%;\n" +
                "                flex-basis: 91.667%;\n" +
                "                max-width: 91.667%;\n" +
                "            }\n" +
                "            .col-md-12 {\n" +
                "                -webkit-flex-basis: 100%;\n" +
                "                -ms-flex-preferred-size: 100%;\n" +
                "                -webkit-flex-basis: 100%;\n" +
                "                -ms-flex-basis: 100%;\n" +
                "                flex-basis: 100%;\n" +
                "                max-width: 100%;\n" +
                "            }\n" +
                "            .col-md-offset-1 {\n" +
                "                margin-left: 8.333%;\n" +
                "            }\n" +
                "            .col-md-offset-2 {\n" +
                "                margin-left: 16.667%;\n" +
                "            }\n" +
                "            .col-md-offset-3 {\n" +
                "                margin-left: 25%;\n" +
                "            }\n" +
                "            .col-md-offset-4 {\n" +
                "                margin-left: 33.333%;\n" +
                "            }\n" +
                "            .col-md-offset-5 {\n" +
                "                margin-left: 41.667%;\n" +
                "            }\n" +
                "            .col-md-offset-6 {\n" +
                "                margin-left: 50%;\n" +
                "            }\n" +
                "            .col-md-offset-7 {\n" +
                "                margin-left: 58.333%;\n" +
                "            }\n" +
                "            .col-md-offset-8 {\n" +
                "                margin-left: 66.667%;\n" +
                "            }\n" +
                "            .col-md-offset-9 {\n" +
                "                margin-left: 75%;\n" +
                "            }\n" +
                "            .col-md-offset-10 {\n" +
                "                margin-left: 83.333%;\n" +
                "            }\n" +
                "            .col-md-offset-11 {\n" +
                "                margin-left: 91.667%;\n" +
                "            }\n" +
                "            .start-md {\n" +
                "                -webkit-box-pack: start;\n" +
                "                -webkit-justify-content: flex-start;\n" +
                "                -ms-flex-pack: start;\n" +
                "                -webkit-box-pack: start;\n" +
                "                -webkit-justify-content: flex-start;\n" +
                "                -ms-flex-pack: start;\n" +
                "                justify-content: flex-start;\n" +
                "                text-align: start;\n" +
                "            }\n" +
                "            .center-md {\n" +
                "                -webkit-box-pack: center;\n" +
                "                -webkit-justify-content: center;\n" +
                "                -ms-flex-pack: center;\n" +
                "                -webkit-box-pack: center;\n" +
                "                -webkit-justify-content: center;\n" +
                "                -ms-flex-pack: center;\n" +
                "                justify-content: center;\n" +
                "                text-align: center;\n" +
                "            }\n" +
                "            .end-md {\n" +
                "                -webkit-box-pack: end;\n" +
                "                -webkit-justify-content: flex-end;\n" +
                "                -ms-flex-pack: end;\n" +
                "                -webkit-box-pack: end;\n" +
                "                -webkit-justify-content: flex-end;\n" +
                "                -ms-flex-pack: end;\n" +
                "                justify-content: flex-end;\n" +
                "                text-align: end;\n" +
                "            }\n" +
                "            .top-md {\n" +
                "                -webkit-box-align: start;\n" +
                "                -webkit-align-items: flex-start;\n" +
                "                -ms-flex-align: start;\n" +
                "                -webkit-align-items: flex-start;\n" +
                "                -webkit-box-align: flex-start;\n" +
                "                -ms-flex-align: flex-start;\n" +
                "                align-items: flex-start;\n" +
                "            }\n" +
                "            .middle-md {\n" +
                "                -webkit-box-align: center;\n" +
                "                -webkit-align-items: center;\n" +
                "                -ms-flex-align: center;\n" +
                "                -webkit-align-items: center;\n" +
                "                -webkit-box-align: center;\n" +
                "                -ms-flex-align: center;\n" +
                "                align-items: center;\n" +
                "            }\n" +
                "            .bottom-md {\n" +
                "                -webkit-box-align: end;\n" +
                "                -webkit-align-items: flex-end;\n" +
                "                -ms-flex-align: end;\n" +
                "                -webkit-align-items: flex-end;\n" +
                "                -webkit-box-align: flex-end;\n" +
                "                -ms-flex-align: flex-end;\n" +
                "                align-items: flex-end;\n" +
                "            }\n" +
                "            .around-md {\n" +
                "                -webkit-justify-content: space-around;\n" +
                "                -ms-flex-pack: distribute;\n" +
                "                -webkit-box-pack: space-around;\n" +
                "                -webkit-justify-content: space-around;\n" +
                "                -ms-flex-pack: space-around;\n" +
                "                justify-content: space-around;\n" +
                "            }\n" +
                "            .between-md {\n" +
                "                -webkit-box-pack: justify;\n" +
                "                -webkit-justify-content: space-between;\n" +
                "                -ms-flex-pack: justify;\n" +
                "                -webkit-box-pack: space-between;\n" +
                "                -webkit-justify-content: space-between;\n" +
                "                -ms-flex-pack: space-between;\n" +
                "                justify-content: space-between;\n" +
                "            }\n" +
                "            .first-md {\n" +
                "                -webkit-box-ordinal-group: 0;\n" +
                "                -webkit-order: -1;\n" +
                "                -ms-flex-order: -1;\n" +
                "                -webkit-order: -1;\n" +
                "                -ms-flex-order: -1;\n" +
                "                order: -1;\n" +
                "            }\n" +
                "            .last-md {\n" +
                "                -webkit-box-ordinal-group: 2;\n" +
                "                -webkit-order: 1;\n" +
                "                -ms-flex-order: 1;\n" +
                "                -webkit-order: 1;\n" +
                "                -ms-flex-order: 1;\n" +
                "                order: 1;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media only screen and (min-width:64em) {\n" +
                "            .container {\n" +
                "                width: 65rem;\n" +
                "            }\n" +
                "            .col-lg,\n" +
                "            .col-lg-1,\n" +
                "            .col-lg-10,\n" +
                "            .col-lg-11,\n" +
                "            .col-lg-12,\n" +
                "            .col-lg-2,\n" +
                "            .col-lg-3,\n" +
                "            .col-lg-4,\n" +
                "            .col-lg-5,\n" +
                "            .col-lg-6,\n" +
                "            .col-lg-7,\n" +
                "            .col-lg-8,\n" +
                "            .col-lg-9,\n" +
                "            .col-lg-offset-1,\n" +
                "            .col-lg-offset-10,\n" +
                "            .col-lg-offset-11,\n" +
                "            .col-lg-offset-12,\n" +
                "            .col-lg-offset-2,\n" +
                "            .col-lg-offset-3,\n" +
                "            .col-lg-offset-4,\n" +
                "            .col-lg-offset-5,\n" +
                "            .col-lg-offset-6,\n" +
                "            .col-lg-offset-7,\n" +
                "            .col-lg-offset-8,\n" +
                "            .col-lg-offset-9 {\n" +
                "                box-sizing: border-box;\n" +
                "                -webkit-box-flex: 0;\n" +
                "                -webkit-flex: 0 0 auto;\n" +
                "                -ms-flex: 0 0 auto;\n" +
                "                -webkit-flex: 0 0 auto;\n" +
                "                -ms-flex: 0 0 auto;\n" +
                "                flex: 0 0 auto;\n" +
                "                padding-right: .5rem;\n" +
                "                padding-left: .5rem;\n" +
                "            }\n" +
                "            .col-lg {\n" +
                "                -webkit-box-flex: 1;\n" +
                "                -webkit-flex-grow: 1;\n" +
                "                -ms-flex-positive: 1;\n" +
                "                -webkit-flex-grow: 1;\n" +
                "                -ms-flex-grow: 1;\n" +
                "                flex-grow: 1;\n" +
                "                -webkit-flex-basis: 0;\n" +
                "                -ms-flex-preferred-size: 0;\n" +
                "                -webkit-flex-basis: 0;\n" +
                "                -ms-flex-basis: 0;\n" +
                "                flex-basis: 0;\n" +
                "                max-width: 100%;\n" +
                "            }\n" +
                "            .col-lg-1 {\n" +
                "                -webkit-flex-basis: 8.333%;\n" +
                "                -ms-flex-preferred-size: 8.333%;\n" +
                "                -webkit-flex-basis: 8.333%;\n" +
                "                -ms-flex-basis: 8.333%;\n" +
                "                flex-basis: 8.333%;\n" +
                "                max-width: 8.333%;\n" +
                "            }\n" +
                "            .col-lg-2 {\n" +
                "                -webkit-flex-basis: 16.667%;\n" +
                "                -ms-flex-preferred-size: 16.667%;\n" +
                "                -webkit-flex-basis: 16.667%;\n" +
                "                -ms-flex-basis: 16.667%;\n" +
                "                flex-basis: 16.667%;\n" +
                "                max-width: 16.667%;\n" +
                "            }\n" +
                "            .col-lg-3 {\n" +
                "                -webkit-flex-basis: 25%;\n" +
                "                -ms-flex-preferred-size: 25%;\n" +
                "                -webkit-flex-basis: 25%;\n" +
                "                -ms-flex-basis: 25%;\n" +
                "                flex-basis: 25%;\n" +
                "                max-width: 25%;\n" +
                "            }\n" +
                "            .col-lg-4 {\n" +
                "                -webkit-flex-basis: 33.333%;\n" +
                "                -ms-flex-preferred-size: 33.333%;\n" +
                "                -webkit-flex-basis: 33.333%;\n" +
                "                -ms-flex-basis: 33.333%;\n" +
                "                flex-basis: 33.333%;\n" +
                "                max-width: 33.333%;\n" +
                "            }\n" +
                "            .col-lg-5 {\n" +
                "                -webkit-flex-basis: 41.667%;\n" +
                "                -ms-flex-preferred-size: 41.667%;\n" +
                "                -webkit-flex-basis: 41.667%;\n" +
                "                -ms-flex-basis: 41.667%;\n" +
                "                flex-basis: 41.667%;\n" +
                "                max-width: 41.667%;\n" +
                "            }\n" +
                "            .col-lg-6 {\n" +
                "                -webkit-flex-basis: 50%;\n" +
                "                -ms-flex-preferred-size: 50%;\n" +
                "                -webkit-flex-basis: 50%;\n" +
                "                -ms-flex-basis: 50%;\n" +
                "                flex-basis: 50%;\n" +
                "                max-width: 50%;\n" +
                "            }\n" +
                "            .col-lg-7 {\n" +
                "                -webkit-flex-basis: 58.333%;\n" +
                "                -ms-flex-preferred-size: 58.333%;\n" +
                "                -webkit-flex-basis: 58.333%;\n" +
                "                -ms-flex-basis: 58.333%;\n" +
                "                flex-basis: 58.333%;\n" +
                "                max-width: 58.333%;\n" +
                "            }\n" +
                "            .col-lg-8 {\n" +
                "                -webkit-flex-basis: 66.667%;\n" +
                "                -ms-flex-preferred-size: 66.667%;\n" +
                "                -webkit-flex-basis: 66.667%;\n" +
                "                -ms-flex-basis: 66.667%;\n" +
                "                flex-basis: 66.667%;\n" +
                "                max-width: 66.667%;\n" +
                "            }\n" +
                "            .col-lg-9 {\n" +
                "                -webkit-flex-basis: 75%;\n" +
                "                -ms-flex-preferred-size: 75%;\n" +
                "                -webkit-flex-basis: 75%;\n" +
                "                -ms-flex-basis: 75%;\n" +
                "                flex-basis: 75%;\n" +
                "                max-width: 75%;\n" +
                "            }\n" +
                "            .col-lg-10 {\n" +
                "                -webkit-flex-basis: 83.333%;\n" +
                "                -ms-flex-preferred-size: 83.333%;\n" +
                "                -webkit-flex-basis: 83.333%;\n" +
                "                -ms-flex-basis: 83.333%;\n" +
                "                flex-basis: 83.333%;\n" +
                "                max-width: 83.333%;\n" +
                "            }\n" +
                "            .col-lg-11 {\n" +
                "                -webkit-flex-basis: 91.667%;\n" +
                "                -ms-flex-preferred-size: 91.667%;\n" +
                "                -webkit-flex-basis: 91.667%;\n" +
                "                -ms-flex-basis: 91.667%;\n" +
                "                flex-basis: 91.667%;\n" +
                "                max-width: 91.667%;\n" +
                "            }\n" +
                "            .col-lg-12 {\n" +
                "                -webkit-flex-basis: 100%;\n" +
                "                -ms-flex-preferred-size: 100%;\n" +
                "                -webkit-flex-basis: 100%;\n" +
                "                -ms-flex-basis: 100%;\n" +
                "                flex-basis: 100%;\n" +
                "                max-width: 100%;\n" +
                "            }\n" +
                "            .col-lg-offset-1 {\n" +
                "                margin-left: 8.333%;\n" +
                "            }\n" +
                "            .col-lg-offset-2 {\n" +
                "                margin-left: 16.667%;\n" +
                "            }\n" +
                "            .col-lg-offset-3 {\n" +
                "                margin-left: 25%;\n" +
                "            }\n" +
                "            .col-lg-offset-4 {\n" +
                "                margin-left: 33.333%;\n" +
                "            }\n" +
                "            .col-lg-offset-5 {\n" +
                "                margin-left: 41.667%;\n" +
                "            }\n" +
                "            .col-lg-offset-6 {\n" +
                "                margin-left: 50%;\n" +
                "            }\n" +
                "            .col-lg-offset-7 {\n" +
                "                margin-left: 58.333%;\n" +
                "            }\n" +
                "            .col-lg-offset-8 {\n" +
                "                margin-left: 66.667%;\n" +
                "            }\n" +
                "            .col-lg-offset-9 {\n" +
                "                margin-left: 75%;\n" +
                "            }\n" +
                "            .col-lg-offset-10 {\n" +
                "                margin-left: 83.333%;\n" +
                "            }\n" +
                "            .col-lg-offset-11 {\n" +
                "                margin-left: 91.667%;\n" +
                "            }\n" +
                "            .start-lg {\n" +
                "                -webkit-box-pack: start;\n" +
                "                -webkit-justify-content: flex-start;\n" +
                "                -ms-flex-pack: start;\n" +
                "                -webkit-box-pack: start;\n" +
                "                -webkit-justify-content: flex-start;\n" +
                "                -ms-flex-pack: start;\n" +
                "                justify-content: flex-start;\n" +
                "                text-align: start;\n" +
                "            }\n" +
                "            .center-lg {\n" +
                "                -webkit-box-pack: center;\n" +
                "                -webkit-justify-content: center;\n" +
                "                -ms-flex-pack: center;\n" +
                "                -webkit-box-pack: center;\n" +
                "                -webkit-justify-content: center;\n" +
                "                -ms-flex-pack: center;\n" +
                "                justify-content: center;\n" +
                "                text-align: center;\n" +
                "            }\n" +
                "            .end-lg {\n" +
                "                -webkit-box-pack: end;\n" +
                "                -webkit-justify-content: flex-end;\n" +
                "                -ms-flex-pack: end;\n" +
                "                -webkit-box-pack: end;\n" +
                "                -webkit-justify-content: flex-end;\n" +
                "                -ms-flex-pack: end;\n" +
                "                justify-content: flex-end;\n" +
                "                text-align: end;\n" +
                "            }\n" +
                "            .top-lg {\n" +
                "                -webkit-box-align: start;\n" +
                "                -webkit-align-items: flex-start;\n" +
                "                -ms-flex-align: start;\n" +
                "                -webkit-align-items: flex-start;\n" +
                "                -webkit-box-align: flex-start;\n" +
                "                -ms-flex-align: flex-start;\n" +
                "                align-items: flex-start;\n" +
                "            }\n" +
                "            .middle-lg {\n" +
                "                -webkit-box-align: center;\n" +
                "                -webkit-align-items: center;\n" +
                "                -ms-flex-align: center;\n" +
                "                -webkit-align-items: center;\n" +
                "                -webkit-box-align: center;\n" +
                "                -ms-flex-align: center;\n" +
                "                align-items: center;\n" +
                "            }\n" +
                "            .bottom-lg {\n" +
                "                -webkit-box-align: end;\n" +
                "                -webkit-align-items: flex-end;\n" +
                "                -ms-flex-align: end;\n" +
                "                -webkit-align-items: flex-end;\n" +
                "                -webkit-box-align: flex-end;\n" +
                "                -ms-flex-align: flex-end;\n" +
                "                align-items: flex-end;\n" +
                "            }\n" +
                "            .around-lg {\n" +
                "                -webkit-justify-content: space-around;\n" +
                "                -ms-flex-pack: distribute;\n" +
                "                -webkit-box-pack: space-around;\n" +
                "                -webkit-justify-content: space-around;\n" +
                "                -ms-flex-pack: space-around;\n" +
                "                justify-content: space-around;\n" +
                "            }\n" +
                "            .between-lg {\n" +
                "                -webkit-box-pack: justify;\n" +
                "                -webkit-justify-content: space-between;\n" +
                "                -ms-flex-pack: justify;\n" +
                "                -webkit-box-pack: space-between;\n" +
                "                -webkit-justify-content: space-between;\n" +
                "                -ms-flex-pack: space-between;\n" +
                "                justify-content: space-between;\n" +
                "            }\n" +
                "            .first-lg {\n" +
                "                -webkit-box-ordinal-group: 0;\n" +
                "                -webkit-order: -1;\n" +
                "                -ms-flex-order: -1;\n" +
                "                -webkit-order: -1;\n" +
                "                -ms-flex-order: -1;\n" +
                "                order: -1;\n" +
                "            }\n" +
                "            .last-lg {\n" +
                "                -webkit-box-ordinal-group: 2;\n" +
                "                -webkit-order: 1;\n" +
                "                -ms-flex-order: 1;\n" +
                "                -webkit-order: 1;\n" +
                "                -ms-flex-order: 1;\n" +
                "                order: 1;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "    <style type=\"text/css\" data-styled-components=\"iECmZH gilyel evpGNr byGfxM jFgvCd kktUDF jlPFGn eWjzRU jGqJlD kuqIKy ktKmOq iWVBpY hNogKD dggHpc jpHwD ipliua doYTvo eLCXzP hznfox cHZARy fhbjHj dRtAmU cULmEo hiKSiS cpoSvg kSOdgB GrYpZ dRiOjk cKjbEQ CLluW irjKNI dDRnsI YzqzA gEwPsy gLRoTZ bTVPzW eLBKpU bXYmZq jiDMyE cCPdDf kFuKSG JaVau iheQPW cDBKQv fKsNLk eRGRqS efxxAa eMaSBg fVrZls evjDXW bqVLhR gXwWwO etWFLc jhTbHC hRkhrw gEMvbe dJvZEL dBSBrt fJDlWs chprFS bxIgtu dQWvvR llGlaf cTengc idjmuw\"\n" +
                "           data-styled-components-is-local=\"true\">\n" +
                "        @media (max-width: 30rem) {\n" +
                "            .cpoSvg {\n" +
                "                font-size: 1rem !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 30rem) {\n" +
                "            .gEwPsy {\n" +
                "                font-size: 1rem !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 30rem) {\n" +
                "            .eLBKpU {\n" +
                "                font-size: 1.3090000000000002rem !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 30rem) {\n" +
                "            .bqVLhR {\n" +
                "                font-size: 1rem !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 30rem) {\n" +
                "            .jFgvCd {\n" +
                "                font-size: 1rem !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 30rem) {\n" +
                "            .eWjzRU {\n" +
                "                font-size: 1.3090000000000002rem !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 30rem) {\n" +
                "            .cKjbEQ {\n" +
                "                font-size: 1.3090000000000002rem !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 30rem) {\n" +
                "            .gLRoTZ {\n" +
                "                font-size: 1rem !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 30rem) {\n" +
                "            .etWFLc {\n" +
                "                font-size: 1rem !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 30rem) {\n" +
                "            .hNogKD {\n" +
                "                font-size: 1rem !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 30rem) {\n" +
                "            .jpHwD {\n" +
                "                font-size: 1rem !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 30rem) {\n" +
                "            .cULmEo {\n" +
                "                font-size: 1rem !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 30rem) {\n" +
                "            .hiKSiS {\n" +
                "                font-size: 1rem !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 30rem) {\n" +
                "            .bTVPzW {\n" +
                "                font-size: 1rem !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 30rem) {\n" +
                "            .jiDMyE {\n" +
                "                font-size: 1.618rem !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 30rem) {\n" +
                "            .cCPdDf {\n" +
                "                font-size: 1.618rem !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 30rem) {\n" +
                "            .JaVau {\n" +
                "                font-size: 1.3090000000000002rem !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 30rem) {\n" +
                "            .efxxAa {\n" +
                "                font-size: 1rem !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 30rem) {\n" +
                "            .jhTbHC {\n" +
                "                font-size: 1.3090000000000002rem !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 30rem) {\n" +
                "            .dBSBrt {\n" +
                "                font-size: 1rem !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 30rem) {\n" +
                "            .fJDlWs {\n" +
                "                font-size: 0.6180469715698392rem !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 30rem) {\n" +
                "            .chprFS {\n" +
                "                font-size: 0.6180469715698392rem !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 30rem) {\n" +
                "            .bxIgtu {\n" +
                "                font-size: 0.8090234857849197rem !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 30rem) {\n" +
                "            .dQWvvR {\n" +
                "                font-size: 0.8090234857849197rem !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width: 30rem) {\n" +
                "            .cTengc {\n" +
                "                font-size: 1.3090000000000002rem !important;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "    <style type=\"text/css\" data-styled-components=\"\" data-styled-components-is-local=\"false\">\n" +
                "        @media (max-width: 30rem) {\n" +
                "            #component-playground .containerInner .fullWidthMobile,\n" +
                "            .reactWrapper .containerInner .fullWidthMobile {\n" +
                "                padding-left: 0;\n" +
                "                padding-right: 0;\n" +
                "                overflow-x: hidden;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body style=\"min-width: 320px; background-color: #FFFFFF; width: 100%; margin: 0; padding: 0; -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%;\">\n" +
                "<table width=\"100%\" height=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"left\"\n" +
                "       valign=\"top\" style=\"border-collapse: collapse; border-spacing: 0;\">\n" +
                "    <tbody>\n" +
                "    <tr>\n" +
                "        <td align=\"center\" valign=\"top\" style=\"padding: 0;\">\n" +
                "            <table width=\"600\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse: collapse; border-spacing: 0; background-color: #F3F4F5; text-align: center;\"\n" +
                "                   border=\"0\" valign=\"top\" bgcolor=\"#F3F4F5\">\n" +
                "                <tbody>\n" +
                "                <tr>\n" +
                "                    <td style=\"padding: 0;\">\n" +
                "                        <div class=\"sc-bdVaJa gilyel\" display=\"block\" style=\"background-color:#07484C ;box-sizing: border-box; position: static; border-radius: 0; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); overflow: inherit; margin: 0rem 0rem 0rem 0rem; border-top: none; border-right: none; border-bottom: none; border-left: none; display: block;\">\n" +
                "                            <img class=\"sc-bwzfXH evpGNr\" src=\"cid:identifier1234\"\n" +
                "                                 alt=\"Patreon trademark\" width=\"20\" style=\"border: 0; width: 10rem;\">\n" +
                "                        </div>\n" +
                "                    </td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                    <td style=\"padding: 0;\">\n" +
                "                        <div class=\"sc-bdVaJa byGfxM\" display=\"block\" style=\"box-sizing: border-box; position: static; border-radius: 0; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); overflow: inherit; padding: 0rem 3rem 0rem 3rem; margin: 0rem 0rem 0rem 0rem; border-top: none; border-right: none; border-bottom: none; border-left: none; display: block;\"><span class=\"sc-bxivhb jFgvCd\" color=\"gray1\" scale=\"1\" style=\"color: #052D49; font-family: America, sans-serif; -webkit-letter-spacing: inherit; -moz-letter-spacing: inherit; -ms-letter-spacing: inherit; letter-spacing: inherit; margin: 0; opacity: 1; position: relative; text-align: inherit; text-transform: inherit; text-shadow: none; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); -webkit-user-select: inherit; -moz-user-select: inherit; -ms-user-select: inherit; user-select: inherit; font-size: 1rem; font-weight: 400; line-height: 1.5;\"><div class=\"sc-kgoBCf kktUDF\" overflow=\"hidden\" style=\"border-top: none; border-bottom: none; border-left: none; border-right: none; border-radius: 0; box-shadow: 0 2px 0 0 rgba(5, 45, 73, 0.06999999999999995); background-color: #FFFFFF; overflow: hidden;\"><div class=\"sc-bdVaJa jlPFGn\" display=\"block\" style=\"box-sizing: border-box; position: static; border-radius: 0; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); overflow: inherit; padding: 2rem 2rem 2rem 2rem; margin: 0rem 0rem 0rem 0rem; border-top: none; border-right: none; border-bottom: none; border-left: none; display: block;\"><div class=\"sc-htpNat cpoSvg\" color=\"gray1\" scale=\"1\" style=\"color: #052D49; font-family: America, sans-serif; -webkit-letter-spacing: inherit; -moz-letter-spacing: inherit; -ms-letter-spacing: inherit; letter-spacing: inherit; margin: 0; opacity: 1; position: relative; text-align: left; text-transform: inherit; text-shadow: none; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); -webkit-user-select: inherit; -moz-user-select: inherit; -ms-user-select: inherit; user-select: inherit; font-size: 1rem; font-weight: 400; line-height: 1.5;\"><span class=\"sc-bxivhb eWjzRU\" color=\"gray3\" scale=\"1\" size=\"2\" style=\"color: #4F687A; font-family: America, sans-serif; -webkit-letter-spacing: inherit; -moz-letter-spacing: inherit; -ms-letter-spacing: inherit; letter-spacing: inherit; margin: 0; opacity: 1; position: relative; text-align: inherit; text-transform: inherit; text-shadow: none; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); -webkit-user-select: inherit; -moz-user-select: inherit; -ms-user-select: inherit; user-select: inherit; font-size: 1.3090000000000002rem; font-weight: 400; line-height: 1.25;\">Almost done, " + userDto.getName() + "</span>\n" +
                "\n" +
                "                      <div class=\"sc-bdVaJa dggHpc\" display=\"block\" style=\"box-sizing: border-box; position: static; border-radius: 0; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); overflow: inherit; padding: 0rem 0rem 0rem 0rem; margin: 1.5rem 0rem 1.5rem 0rem; border-top: none; border-right: none; border-bottom: none; border-left: none; display: block;\">To complete your Verified Contracting sign up, we just need to verify your email address. If you did not request\n" +
                "                        to sign up, ignore this email and the link will expire on its own.</div>\n" +
                "                    </div>\n" +
                "                    <a class=\"sc-jzJRlG hznfox\" color=\"primary\" type=\"button\" href=\"" + frontendUrl + "/auth/verify-email/" + userDto.getId() + "\"\n" +
                "                       role=\"button\" style=\"-webkit-backface-visibility: hidden; backface-visibility: hidden; background-color: #07484C; border: 2px solid #F96854; border-radius: 0; box-sizing: border-box; display: inline-block; font-weight: 700; padding: 1rem 1.5rem; position: relative; text-align: center; text-decoration: none; text-transform: uppercase; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); -webkit-user-select: none; -moz-user-select: none; -ms-user-select: none; user-select: none; white-space: inherit; cursor: pointer; color: #FFFFFF; font-size: 1rem;\">\n" +
                "                      <div class=\"sc-fjdhpX ktKmOq\" style=\"visibility: visible;\">Verify your account</div>\n" +
                "                    </a>\n" +
                "                    </div>\n" +
                "                    </div>\n" +
                "                    </span>\n" +
                "                        </div>\n" +
                "                    </td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                    <td style=\"padding: 0;\">\n" +
                "                        <div class=\"sc-bdVaJa gilyel\" display=\"block\" style=\"box-sizing: border-box; position: static; border-radius: 0; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); overflow: inherit; padding: 3rem 3rem 3rem 3rem; margin: 0rem 0rem 0rem 0rem; border-top: none; border-right: none; border-bottom: none; border-left: none; display: block;\"><span class=\"sc-bxivhb jFgvCd\" color=\"gray1\" scale=\"1\" style=\"color: #052D49; font-family: America, sans-serif; -webkit-letter-spacing: inherit; -moz-letter-spacing: inherit; -ms-letter-spacing: inherit; letter-spacing: inherit; margin: 0; opacity: 1; position: relative; text-align: inherit; text-transform: inherit; text-shadow: none; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); -webkit-user-select: inherit; -moz-user-select: inherit; -ms-user-select: inherit; user-select: inherit; font-size: 1rem; font-weight: 400; line-height: 1.5;\"><div class=\"sc-bdVaJa cHZARy\" display=\"block\" style=\"box-sizing: border-box; position: static; border-radius: 0; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); overflow: inherit; padding: 0rem 0rem 3rem 0rem; margin: 0rem 0rem 0rem 0rem; border-top: none; border-right: none; border-bottom: none; border-left: none; display: block;\"></div><div class=\"sc-bdVaJa dRtAmU\" display=\"block\" style=\"box-sizing: border-box; position: static; border-radius: 0; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); overflow: inherit; padding: 0rem 0rem 1.5rem 0rem; margin: 0rem 0rem 0rem 0rem; border-top: none; border-right: none; border-bottom: none; border-left: none; display: block;\"><p class=\"sc-ifAKCX cULmEo\" color=\"gray3\" scale=\"1\" style=\"color: #4F687A; font-family: America, sans-serif; -webkit-letter-spacing: inherit; -moz-letter-spacing: inherit; -ms-letter-spacing: inherit; letter-spacing: inherit; opacity: 1; position: relative; text-align: inherit; text-transform: inherit; text-shadow: none; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); -webkit-user-select: inherit; -moz-user-select: inherit; -ms-user-select: inherit; user-select: inherit; font-size: 1rem; font-weight: 400; line-height: 1.5; margin: 0.5rem 0rem;\">the code will be expired within an hour!<br>Have any questions? Please contact us with <a class=\"sc-gqjmRU doYTvo\" href=\"\" color=\"blue\" scale=\"1\" target=\"_self\" style=\"background-color: transparent; color: #F96854; cursor: pointer; display: inline-block; font-weight: 700; max-width: 100%; overflow: hidden; text-decoration: none; text-decoration-skip: ink; text-overflow: ellipsis; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); vertical-align: bottom; font-size: 1rem; line-height: 1.5;\">verifiedcontracting@gmail.com</a>.</p></div><div class=\"sc-bdVaJa cHZARy\" display=\"block\" style=\"box-sizing: border-box; position: static; border-radius: 0; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); overflow: inherit; padding: 0rem 0rem 3rem 0rem; margin: 0rem 0rem 0rem 0rem; border-top: none; border-right: none; border-bottom: none; border-left: none; display: block;\"><a class=\"sc-gqjmRU doYTvo\" href=\"\" color=\"blue\" scale=\"1\" target=\"_self\" style=\"background-color: transparent; color: #F96854; cursor: pointer; display: inline-block; font-weight: 700; max-width: 100%; overflow: hidden; text-decoration: none; text-decoration-skip: ink; text-overflow: ellipsis; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); vertical-align: bottom; font-size: 1rem; line-height: 1.5;\">LinkedIn</a><span style=\"margin-left:1em;margin-right:1em;\">·</span>\n" +
                "\n" +
                "                      <a class=\"sc-gqjmRU doYTvo\" href=\"https://twitter.com/verifeidcontracting\" color=\"blue\" scale=\"1\"\n" +
                "                              target=\"_self\" style=\"background-color: transparent; color: #F96854; cursor: pointer; display: inline-block; font-weight: 700; max-width: 100%; overflow: hidden; text-decoration: none; text-decoration-skip: ink; text-overflow: ellipsis; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); vertical-align: bottom; font-size: 1rem; line-height: 1.5;\">Twitter</a><span style=\"margin-left:1em;margin-right:1em;\">·</span>\n" +
                "\n" +
                "                        <a class=\"sc-gqjmRU doYTvo\"\n" +
                "                           href=\"https://www.facebook.com/verifiedcontracting\" color=\"blue\" scale=\"1\" target=\"_self\" style=\"background-color: transparent; color: #F96854; cursor: pointer; display: inline-block; font-weight: 700; max-width: 100%; overflow: hidden; text-decoration: none; text-decoration-skip: ink; text-overflow: ellipsis; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); vertical-align: bottom; font-size: 1rem; line-height: 1.5;\">Facebook</a>\n" +
                "                    </div>\n" +
                "                    <p class=\"sc-ifAKCX hiKSiS\" color=\"gray3\" scale=\"1\" style=\"color: #4F687A; font-family: America, sans-serif; -webkit-letter-spacing: inherit; -moz-letter-spacing: inherit; -ms-letter-spacing: inherit; letter-spacing: inherit; margin: 0; opacity: 1; position: relative; text-align: inherit; text-transform: inherit; text-shadow: none; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); -webkit-user-select: inherit; -moz-user-select: inherit; -ms-user-select: inherit; user-select: inherit; font-size: 1rem; font-weight: 400; line-height: 1.5;\">Verfied Contracting</p>\n" +
                "                    <p class=\"sc-ifAKCX cULmEo\"\n" +
                "                       color=\"gray3\" scale=\"1\" style=\"color: #4F687A; font-family: America, sans-serif; -webkit-letter-spacing: inherit; -moz-letter-spacing: inherit; -ms-letter-spacing: inherit; letter-spacing: inherit; opacity: 1; position: relative; text-align: inherit; text-transform: inherit; text-shadow: none; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); -webkit-user-select: inherit; -moz-user-select: inherit; -ms-user-select: inherit; user-select: inherit; font-size: 1rem; font-weight: 400; line-height: 1.5; margin: 0.5rem 0rem;\">www.verifiedcontracting.com\n" +
                "                      <br>Powered by Wixis 360 (Pvt) Ltd\n" +
                "                        </span>\n" +
                "                        </div>\n" +
                "                    </td>\n" +
                "                </tr>\n" +
                "                </tbody>\n" +
                "            </table>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "    </tbody>\n" +
                "</table>\n" +
                "\n" +
                "</body>\n" +
                "\n" +
                "</html>";

        emailService.send(userDto.getEmail(),"Email Verification", trHtml);
        return userDto;
    }

    @Override
    public UserDto verify(String id) throws JsonProcessingException {
        String cached = (String) redisCached.getCached(("USER_"+id).getBytes());

        if (cached != null) {
            ObjectMapper mapper = new ObjectMapper();
            UserDto userDto = mapper.readValue(cached, UserDto.class);
            userDto.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
            userDto.setCreatedTime(new Date());

            Optional<Role> optionalRole = roleDao.findByName(userDto.getRole());

            if (optionalRole.isPresent()) {
                Role role = optionalRole.get();
                userDto.setRoleId(role.getId());
            }

            int isSaved = userDao.save(getUser(userDto));
            if (isSaved > 0) {
                redisCached.deleteCached(("USER_"+id).getBytes());
                if (userDto.getRole().equalsIgnoreCase("CONTRACTOR")) {
                    UserPreferredZipCode userPreferredZipCode = new UserPreferredZipCode();
                    userPreferredZipCode.setId(UUID.randomUUID().toString());
                    userPreferredZipCode.setUserId(userDto.getId());
                    userPreferredZipCode.setZipCode(userDto.getZipCode());
                    userPreferredZipCode.setLongitude(userDto.getLongitude());
                    userPreferredZipCode.setLatitude(userDto.getLatitude());
                    userPreferredZipCodeDao.save(userPreferredZipCode);
                }
                return userDto;
            }

            return null;
        }

        return null;
    }

    @Override
    public UserDto findById(String id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<User> optionalUser = userDao.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            UserDto userDto = getUserDto(user);
            Optional<Role> optionalRole = roleDao.findById(user.getRoleId());

            if (optionalRole.isPresent()) {
                Role role = optionalRole.get();
                userDto.setRole(role.getName());
            }

            List<UserPreferredZipCode> userPreferredZipCodes = userPreferredZipCodeDao.findAllByUserId(user.getId());

            for (UserPreferredZipCode userPreferredZipCode : userPreferredZipCodes) {
                if (!userDto.getZipCode().equalsIgnoreCase(userPreferredZipCode.getZipCode())) {
                    userDto.getPreferredZipCodes().add(getUserPreferredZipCodeDto(userPreferredZipCode));
                }
            }

            List<UserAbility> userAbilities = userAbilityDao.findAllByUserId(user.getId());

            for (UserAbility userAbility : userAbilities) {
                userDto.getAbilities().add(userAbility.getAbilityId());
            }

            List<UserSkill> userSkills = userSkillDao.findAllByUserId(user.getId());

            for (UserSkill userSkill : userSkills) {
                userDto.getSkills().add(userSkill.getSkillId());
            }

            UserFavouriteUser userFavouriteUser = new UserFavouriteUser();
            userFavouriteUser.setUserId(userDetails.getId());
            userFavouriteUser.setFavouriteUserId(id);

            Optional<UserFavouriteUser> optionalUserFavouriteUser = userFavouriteUserDao.find(userFavouriteUser);

            if (optionalUserFavouriteUser.isPresent()) {
                userDto.setIsFavourite(1);
            } else {
                userDto.setIsFavourite(0);
            }

            return userDto;
        }

        return null;
    }

    @Override
    public UserDto findByEmail(String email) {
        return userDao.findByEmail(email).isPresent() ? getUserDto(userDao.findByEmail(email).get()) : null;
    }

    @Override
    public List<UserDto> findAll() {
        List<User> users = userDao.findAll();
        return users.stream().map(this::getUserDto).toList();
    }

    @Override
    public Page<UserDto> findAllBySortAndPage(Pageable page, String role, String search, String abilities, String skills) {
        Page<User> userPage = userDao.findAllBySortAndPage(page, role, search, abilities, skills);
        List<UserDto> userDtoList = StreamSupport.stream(userPage.spliterator(), false)
                .map(this::getUserDto)
                .collect(Collectors.toList());
        return new PageImpl<>(userDtoList, page, userPage.getTotalElements());
    }

    @Override
    public Page<UserDto> findAllByDistanceRange(Pageable page, String longitude, String latitude, int distance, String role, String search, String abilities, String skills) {
        Page<User> userPage = userDao.findAllByDistanceRange(page, longitude, latitude, distance, role, search, abilities, skills);
        List<UserDto> userDtoList = StreamSupport.stream(userPage.spliterator(), false)
                .map(this::getUserDto)
                .collect(Collectors.toList());
        return new PageImpl<>(userDtoList, page, userPage.getTotalElements());
    }

    @Override
    public UserDto update(UserDto userDto) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(userDto.getProfilePicture());
            FileUtils.writeByteArrayToFile(new File(profilePictureUploadPath + userDto.getId() + ".png"), decodedBytes);
            userDto.setProfilePicture(userDto.getId() + ".png");
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        Optional<User> optionalUser = userDao.findById(userDto.getId());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Optional<Role> optionalRole = roleDao.findById(user.getRoleId());

            if (optionalRole.isPresent()) {
                Role role = optionalRole.get();
                userDto.setRole(role.getName());
            }

            userPreferredZipCodeDao.deleteByUserId(user.getId());

            if (userDto.getRole().equalsIgnoreCase("CONTRACTOR")) {
                UserPreferredZipCode userPreferredZipCode = new UserPreferredZipCode();
                userPreferredZipCode.setId(UUID.randomUUID().toString());
                userPreferredZipCode.setUserId(userDto.getId());
                userPreferredZipCode.setZipCode(userDto.getZipCode());
                userPreferredZipCode.setLongitude(userDto.getLongitude());
                userPreferredZipCode.setLatitude(userDto.getLatitude());
                userPreferredZipCodeDao.save(userPreferredZipCode);
            }

            List<UserPreferredZipCodeDto> userPreferredZipCodeDtoList = userDto.getPreferredZipCodes();

            for (UserPreferredZipCodeDto userPreferredZipCodeDto : userPreferredZipCodeDtoList) {
                UserPreferredZipCode userPreferredZipCode = new UserPreferredZipCode();
                userPreferredZipCode.setId(UUID.randomUUID().toString());
                userPreferredZipCode.setUserId(user.getId());
                userPreferredZipCode.setZipCode(userPreferredZipCodeDto.getZipCode());
                userPreferredZipCode.setLongitude(userPreferredZipCodeDto.getLongitude());
                userPreferredZipCode.setLatitude(userPreferredZipCodeDto.getLatitude());

                userPreferredZipCodeDao.save(userPreferredZipCode);
            }
        }

        return userDao.update(getUser(userDto)) > 0 ? userDto : null;
    }

    @Override
    public UserDto updateAbilitySkill(UserAbilitySkillDto userAbilitySkillDto) {
        Optional<User> optionalUser = userDao.findById(userAbilitySkillDto.getId());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            userAbilityDao.deleteByUserId(user.getId());

            List<String> abilitiesList = userAbilitySkillDto.getAbilities();

            for (String ability : abilitiesList) {
                UserAbility userAbility = new UserAbility();
                userAbility.setId(UUID.randomUUID().toString());
                userAbility.setUserId(user.getId());
                userAbility.setAbilityId(ability);

                userAbilityDao.save(userAbility);
            }

            userSkillDao.deleteByUserId(user.getId());

            List<String> skills = userAbilitySkillDto.getSkills();

            for (String skill : skills) {
                UserSkill userSkill = new UserSkill();
                userSkill.setId(UUID.randomUUID().toString());
                userSkill.setUserId(user.getId());
                userSkill.setSkillId(skill);

                userSkillDao.save(userSkill);
            }

            UserDto userDto = getUserDto(user);

            Optional<Role> optionalRole = roleDao.findById(user.getRoleId());

            if (optionalRole.isPresent()) {
                Role role = optionalRole.get();
                userDto.setRole(role.getName());
            }

            List<UserAbility> userAbilities = userAbilityDao.findAllByUserId(user.getId());

            for (UserAbility userAbility : userAbilities) {
                userDto.getAbilities().add(userAbility.getAbilityId());
            }

            List<UserSkill> userSkills = userSkillDao.findAllByUserId(user.getId());

            for (UserSkill userSkill : userSkills) {
                userDto.getSkills().add(userSkill.getSkillId());
            }

            return userDto;
        }

        return null;
    }

    @Override
    public boolean updatePassword(UpdateUserPasswordDto updateUserPasswordDto) {
        Optional<User> optionalUser = userDao.findById(updateUserPasswordDto.getId());

        if (optionalUser.isEmpty()){
            return false;
        }

        User user = optionalUser.get();

        Boolean checkPassword = Utility.checkPassword(updateUserPasswordDto.getCurrentPassword(), user.getPassword());
        if (!checkPassword){
            return false;
        }

        String encodePassword = Utility.encodePassword(updateUserPasswordDto.getPassword());
        return userDao.updatePassword(user.getId(), encodePassword) > 0;
    }

    @Override
    public boolean forgotPassword(UserDto userDto) throws MessagingException, IOException {
        RequestUserPassword requestUserPassword = new RequestUserPassword();
        requestUserPassword.setId(UUID.randomUUID().toString());
        requestUserPassword.setUserId(userDto.getId());

        int isSaved = requestUserPasswordDao.save(requestUserPassword);

        if (isSaved > 0) {
            String trHtml = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
                    "<html xmlns=\"http://www.w3.org/1999/xhtml\" style=\"font-family: sans-serif; -ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;\">\n" +
                    "\n" +
                    "<head>\n" +
                    "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Password Reset</title>\n" +
                    "    <style type=\"text/css\" data-styled-components=\"\" data-styled-components-is-local=\"false\">\n" +
                    "        @media only screen and (min-width:30em) {\n" +
                    "            .container {\n" +
                    "                width: 31rem;\n" +
                    "            }\n" +
                    "            .col-sm,\n" +
                    "            .col-sm-1,\n" +
                    "            .col-sm-10,\n" +
                    "            .col-sm-11,\n" +
                    "            .col-sm-12,\n" +
                    "            .col-sm-2,\n" +
                    "            .col-sm-3,\n" +
                    "            .col-sm-4,\n" +
                    "            .col-sm-5,\n" +
                    "            .col-sm-6,\n" +
                    "            .col-sm-7,\n" +
                    "            .col-sm-8,\n" +
                    "            .col-sm-9,\n" +
                    "            .col-sm-offset-1,\n" +
                    "            .col-sm-offset-10,\n" +
                    "            .col-sm-offset-11,\n" +
                    "            .col-sm-offset-12,\n" +
                    "            .col-sm-offset-2,\n" +
                    "            .col-sm-offset-3,\n" +
                    "            .col-sm-offset-4,\n" +
                    "            .col-sm-offset-5,\n" +
                    "            .col-sm-offset-6,\n" +
                    "            .col-sm-offset-7,\n" +
                    "            .col-sm-offset-8,\n" +
                    "            .col-sm-offset-9 {\n" +
                    "                box-sizing: border-box;\n" +
                    "                -webkit-box-flex: 0;\n" +
                    "                -webkit-flex: 0 0 auto;\n" +
                    "                -ms-flex: 0 0 auto;\n" +
                    "                -webkit-flex: 0 0 auto;\n" +
                    "                -ms-flex: 0 0 auto;\n" +
                    "                flex: 0 0 auto;\n" +
                    "                padding-right: .5rem;\n" +
                    "                padding-left: .5rem;\n" +
                    "            }\n" +
                    "            .col-sm {\n" +
                    "                -webkit-box-flex: 1;\n" +
                    "                -webkit-flex-grow: 1;\n" +
                    "                -ms-flex-positive: 1;\n" +
                    "                -webkit-flex-grow: 1;\n" +
                    "                -ms-flex-grow: 1;\n" +
                    "                flex-grow: 1;\n" +
                    "                -webkit-flex-basis: 0;\n" +
                    "                -ms-flex-preferred-size: 0;\n" +
                    "                -webkit-flex-basis: 0;\n" +
                    "                -ms-flex-basis: 0;\n" +
                    "                flex-basis: 0;\n" +
                    "                max-width: 100%;\n" +
                    "            }\n" +
                    "            .col-sm-1 {\n" +
                    "                -webkit-flex-basis: 8.333%;\n" +
                    "                -ms-flex-preferred-size: 8.333%;\n" +
                    "                -webkit-flex-basis: 8.333%;\n" +
                    "                -ms-flex-basis: 8.333%;\n" +
                    "                flex-basis: 8.333%;\n" +
                    "                max-width: 8.333%;\n" +
                    "            }\n" +
                    "            .col-sm-2 {\n" +
                    "                -webkit-flex-basis: 16.667%;\n" +
                    "                -ms-flex-preferred-size: 16.667%;\n" +
                    "                -webkit-flex-basis: 16.667%;\n" +
                    "                -ms-flex-basis: 16.667%;\n" +
                    "                flex-basis: 16.667%;\n" +
                    "                max-width: 16.667%;\n" +
                    "            }\n" +
                    "            .col-sm-3 {\n" +
                    "                -webkit-flex-basis: 25%;\n" +
                    "                -ms-flex-preferred-size: 25%;\n" +
                    "                -webkit-flex-basis: 25%;\n" +
                    "                -ms-flex-basis: 25%;\n" +
                    "                flex-basis: 25%;\n" +
                    "                max-width: 25%;\n" +
                    "            }\n" +
                    "            .col-sm-4 {\n" +
                    "                -webkit-flex-basis: 33.333%;\n" +
                    "                -ms-flex-preferred-size: 33.333%;\n" +
                    "                -webkit-flex-basis: 33.333%;\n" +
                    "                -ms-flex-basis: 33.333%;\n" +
                    "                flex-basis: 33.333%;\n" +
                    "                max-width: 33.333%;\n" +
                    "            }\n" +
                    "            .col-sm-5 {\n" +
                    "                -webkit-flex-basis: 41.667%;\n" +
                    "                -ms-flex-preferred-size: 41.667%;\n" +
                    "                -webkit-flex-basis: 41.667%;\n" +
                    "                -ms-flex-basis: 41.667%;\n" +
                    "                flex-basis: 41.667%;\n" +
                    "                max-width: 41.667%;\n" +
                    "            }\n" +
                    "            .col-sm-6 {\n" +
                    "                -webkit-flex-basis: 50%;\n" +
                    "                -ms-flex-preferred-size: 50%;\n" +
                    "                -webkit-flex-basis: 50%;\n" +
                    "                -ms-flex-basis: 50%;\n" +
                    "                flex-basis: 50%;\n" +
                    "                max-width: 50%;\n" +
                    "            }\n" +
                    "            .col-sm-7 {\n" +
                    "                -webkit-flex-basis: 58.333%;\n" +
                    "                -ms-flex-preferred-size: 58.333%;\n" +
                    "                -webkit-flex-basis: 58.333%;\n" +
                    "                -ms-flex-basis: 58.333%;\n" +
                    "                flex-basis: 58.333%;\n" +
                    "                max-width: 58.333%;\n" +
                    "            }\n" +
                    "            .col-sm-8 {\n" +
                    "                -webkit-flex-basis: 66.667%;\n" +
                    "                -ms-flex-preferred-size: 66.667%;\n" +
                    "                -webkit-flex-basis: 66.667%;\n" +
                    "                -ms-flex-basis: 66.667%;\n" +
                    "                flex-basis: 66.667%;\n" +
                    "                max-width: 66.667%;\n" +
                    "            }\n" +
                    "            .col-sm-9 {\n" +
                    "                -webkit-flex-basis: 75%;\n" +
                    "                -ms-flex-preferred-size: 75%;\n" +
                    "                -webkit-flex-basis: 75%;\n" +
                    "                -ms-flex-basis: 75%;\n" +
                    "                flex-basis: 75%;\n" +
                    "                max-width: 75%;\n" +
                    "            }\n" +
                    "            .col-sm-10 {\n" +
                    "                -webkit-flex-basis: 83.333%;\n" +
                    "                -ms-flex-preferred-size: 83.333%;\n" +
                    "                -webkit-flex-basis: 83.333%;\n" +
                    "                -ms-flex-basis: 83.333%;\n" +
                    "                flex-basis: 83.333%;\n" +
                    "                max-width: 83.333%;\n" +
                    "            }\n" +
                    "            .col-sm-11 {\n" +
                    "                -webkit-flex-basis: 91.667%;\n" +
                    "                -ms-flex-preferred-size: 91.667%;\n" +
                    "                -webkit-flex-basis: 91.667%;\n" +
                    "                -ms-flex-basis: 91.667%;\n" +
                    "                flex-basis: 91.667%;\n" +
                    "                max-width: 91.667%;\n" +
                    "            }\n" +
                    "            .col-sm-12 {\n" +
                    "                -webkit-flex-basis: 100%;\n" +
                    "                -ms-flex-preferred-size: 100%;\n" +
                    "                -webkit-flex-basis: 100%;\n" +
                    "                -ms-flex-basis: 100%;\n" +
                    "                flex-basis: 100%;\n" +
                    "                max-width: 100%;\n" +
                    "            }\n" +
                    "            .col-sm-offset-1 {\n" +
                    "                margin-left: 8.333%;\n" +
                    "            }\n" +
                    "            .col-sm-offset-2 {\n" +
                    "                margin-left: 16.667%;\n" +
                    "            }\n" +
                    "            .col-sm-offset-3 {\n" +
                    "                margin-left: 25%;\n" +
                    "            }\n" +
                    "            .col-sm-offset-4 {\n" +
                    "                margin-left: 33.333%;\n" +
                    "            }\n" +
                    "            .col-sm-offset-5 {\n" +
                    "                margin-left: 41.667%;\n" +
                    "            }\n" +
                    "            .col-sm-offset-6 {\n" +
                    "                margin-left: 50%;\n" +
                    "            }\n" +
                    "            .col-sm-offset-7 {\n" +
                    "                margin-left: 58.333%;\n" +
                    "            }\n" +
                    "            .col-sm-offset-8 {\n" +
                    "                margin-left: 66.667%;\n" +
                    "            }\n" +
                    "            .col-sm-offset-9 {\n" +
                    "                margin-left: 75%;\n" +
                    "            }\n" +
                    "            .col-sm-offset-10 {\n" +
                    "                margin-left: 83.333%;\n" +
                    "            }\n" +
                    "            .col-sm-offset-11 {\n" +
                    "                margin-left: 91.667%;\n" +
                    "            }\n" +
                    "            .start-sm {\n" +
                    "                -webkit-box-pack: start;\n" +
                    "                -webkit-justify-content: flex-start;\n" +
                    "                -ms-flex-pack: start;\n" +
                    "                -webkit-box-pack: start;\n" +
                    "                -webkit-justify-content: flex-start;\n" +
                    "                -ms-flex-pack: start;\n" +
                    "                justify-content: flex-start;\n" +
                    "                text-align: start;\n" +
                    "            }\n" +
                    "            .center-sm {\n" +
                    "                -webkit-box-pack: center;\n" +
                    "                -webkit-justify-content: center;\n" +
                    "                -ms-flex-pack: center;\n" +
                    "                -webkit-box-pack: center;\n" +
                    "                -webkit-justify-content: center;\n" +
                    "                -ms-flex-pack: center;\n" +
                    "                justify-content: center;\n" +
                    "                text-align: center;\n" +
                    "            }\n" +
                    "            .end-sm {\n" +
                    "                -webkit-box-pack: end;\n" +
                    "                -webkit-justify-content: flex-end;\n" +
                    "                -ms-flex-pack: end;\n" +
                    "                -webkit-box-pack: end;\n" +
                    "                -webkit-justify-content: flex-end;\n" +
                    "                -ms-flex-pack: end;\n" +
                    "                justify-content: flex-end;\n" +
                    "                text-align: end;\n" +
                    "            }\n" +
                    "            .top-sm {\n" +
                    "                -webkit-box-align: start;\n" +
                    "                -webkit-align-items: flex-start;\n" +
                    "                -ms-flex-align: start;\n" +
                    "                -webkit-align-items: flex-start;\n" +
                    "                -webkit-box-align: flex-start;\n" +
                    "                -ms-flex-align: flex-start;\n" +
                    "                align-items: flex-start;\n" +
                    "            }\n" +
                    "            .middle-sm {\n" +
                    "                -webkit-box-align: center;\n" +
                    "                -webkit-align-items: center;\n" +
                    "                -ms-flex-align: center;\n" +
                    "                -webkit-align-items: center;\n" +
                    "                -webkit-box-align: center;\n" +
                    "                -ms-flex-align: center;\n" +
                    "                align-items: center;\n" +
                    "            }\n" +
                    "            .bottom-sm {\n" +
                    "                -webkit-box-align: end;\n" +
                    "                -webkit-align-items: flex-end;\n" +
                    "                -ms-flex-align: end;\n" +
                    "                -webkit-align-items: flex-end;\n" +
                    "                -webkit-box-align: flex-end;\n" +
                    "                -ms-flex-align: flex-end;\n" +
                    "                align-items: flex-end;\n" +
                    "            }\n" +
                    "            .around-sm {\n" +
                    "                -webkit-justify-content: space-around;\n" +
                    "                -ms-flex-pack: distribute;\n" +
                    "                -webkit-box-pack: space-around;\n" +
                    "                -webkit-justify-content: space-around;\n" +
                    "                -ms-flex-pack: space-around;\n" +
                    "                justify-content: space-around;\n" +
                    "            }\n" +
                    "            .between-sm {\n" +
                    "                -webkit-box-pack: justify;\n" +
                    "                -webkit-justify-content: space-between;\n" +
                    "                -ms-flex-pack: justify;\n" +
                    "                -webkit-box-pack: space-between;\n" +
                    "                -webkit-justify-content: space-between;\n" +
                    "                -ms-flex-pack: space-between;\n" +
                    "                justify-content: space-between;\n" +
                    "            }\n" +
                    "            .first-sm {\n" +
                    "                -webkit-box-ordinal-group: 0;\n" +
                    "                -webkit-order: -1;\n" +
                    "                -ms-flex-order: -1;\n" +
                    "                -webkit-order: -1;\n" +
                    "                -ms-flex-order: -1;\n" +
                    "                order: -1;\n" +
                    "            }\n" +
                    "            .last-sm {\n" +
                    "                -webkit-box-ordinal-group: 2;\n" +
                    "                -webkit-order: 1;\n" +
                    "                -ms-flex-order: 1;\n" +
                    "                -webkit-order: 1;\n" +
                    "                -ms-flex-order: 1;\n" +
                    "                order: 1;\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        @media only screen and (min-width:48em) {\n" +
                    "            .container {\n" +
                    "                width: 49rem;\n" +
                    "            }\n" +
                    "            .col-md,\n" +
                    "            .col-md-1,\n" +
                    "            .col-md-10,\n" +
                    "            .col-md-11,\n" +
                    "            .col-md-12,\n" +
                    "            .col-md-2,\n" +
                    "            .col-md-3,\n" +
                    "            .col-md-4,\n" +
                    "            .col-md-5,\n" +
                    "            .col-md-6,\n" +
                    "            .col-md-7,\n" +
                    "            .col-md-8,\n" +
                    "            .col-md-9,\n" +
                    "            .col-md-offset-1,\n" +
                    "            .col-md-offset-10,\n" +
                    "            .col-md-offset-11,\n" +
                    "            .col-md-offset-12,\n" +
                    "            .col-md-offset-2,\n" +
                    "            .col-md-offset-3,\n" +
                    "            .col-md-offset-4,\n" +
                    "            .col-md-offset-5,\n" +
                    "            .col-md-offset-6,\n" +
                    "            .col-md-offset-7,\n" +
                    "            .col-md-offset-8,\n" +
                    "            .col-md-offset-9 {\n" +
                    "                box-sizing: border-box;\n" +
                    "                -webkit-box-flex: 0;\n" +
                    "                -webkit-flex: 0 0 auto;\n" +
                    "                -ms-flex: 0 0 auto;\n" +
                    "                -webkit-flex: 0 0 auto;\n" +
                    "                -ms-flex: 0 0 auto;\n" +
                    "                flex: 0 0 auto;\n" +
                    "                padding-right: .5rem;\n" +
                    "                padding-left: .5rem;\n" +
                    "            }\n" +
                    "            .col-md {\n" +
                    "                -webkit-box-flex: 1;\n" +
                    "                -webkit-flex-grow: 1;\n" +
                    "                -ms-flex-positive: 1;\n" +
                    "                -webkit-flex-grow: 1;\n" +
                    "                -ms-flex-grow: 1;\n" +
                    "                flex-grow: 1;\n" +
                    "                -webkit-flex-basis: 0;\n" +
                    "                -ms-flex-preferred-size: 0;\n" +
                    "                -webkit-flex-basis: 0;\n" +
                    "                -ms-flex-basis: 0;\n" +
                    "                flex-basis: 0;\n" +
                    "                max-width: 100%;\n" +
                    "            }\n" +
                    "            .col-md-1 {\n" +
                    "                -webkit-flex-basis: 8.333%;\n" +
                    "                -ms-flex-preferred-size: 8.333%;\n" +
                    "                -webkit-flex-basis: 8.333%;\n" +
                    "                -ms-flex-basis: 8.333%;\n" +
                    "                flex-basis: 8.333%;\n" +
                    "                max-width: 8.333%;\n" +
                    "            }\n" +
                    "            .col-md-2 {\n" +
                    "                -webkit-flex-basis: 16.667%;\n" +
                    "                -ms-flex-preferred-size: 16.667%;\n" +
                    "                -webkit-flex-basis: 16.667%;\n" +
                    "                -ms-flex-basis: 16.667%;\n" +
                    "                flex-basis: 16.667%;\n" +
                    "                max-width: 16.667%;\n" +
                    "            }\n" +
                    "            .col-md-3 {\n" +
                    "                -webkit-flex-basis: 25%;\n" +
                    "                -ms-flex-preferred-size: 25%;\n" +
                    "                -webkit-flex-basis: 25%;\n" +
                    "                -ms-flex-basis: 25%;\n" +
                    "                flex-basis: 25%;\n" +
                    "                max-width: 25%;\n" +
                    "            }\n" +
                    "            .col-md-4 {\n" +
                    "                -webkit-flex-basis: 33.333%;\n" +
                    "                -ms-flex-preferred-size: 33.333%;\n" +
                    "                -webkit-flex-basis: 33.333%;\n" +
                    "                -ms-flex-basis: 33.333%;\n" +
                    "                flex-basis: 33.333%;\n" +
                    "                max-width: 33.333%;\n" +
                    "            }\n" +
                    "            .col-md-5 {\n" +
                    "                -webkit-flex-basis: 41.667%;\n" +
                    "                -ms-flex-preferred-size: 41.667%;\n" +
                    "                -webkit-flex-basis: 41.667%;\n" +
                    "                -ms-flex-basis: 41.667%;\n" +
                    "                flex-basis: 41.667%;\n" +
                    "                max-width: 41.667%;\n" +
                    "            }\n" +
                    "            .col-md-6 {\n" +
                    "                -webkit-flex-basis: 50%;\n" +
                    "                -ms-flex-preferred-size: 50%;\n" +
                    "                -webkit-flex-basis: 50%;\n" +
                    "                -ms-flex-basis: 50%;\n" +
                    "                flex-basis: 50%;\n" +
                    "                max-width: 50%;\n" +
                    "            }\n" +
                    "            .col-md-7 {\n" +
                    "                -webkit-flex-basis: 58.333%;\n" +
                    "                -ms-flex-preferred-size: 58.333%;\n" +
                    "                -webkit-flex-basis: 58.333%;\n" +
                    "                -ms-flex-basis: 58.333%;\n" +
                    "                flex-basis: 58.333%;\n" +
                    "                max-width: 58.333%;\n" +
                    "            }\n" +
                    "            .col-md-8 {\n" +
                    "                -webkit-flex-basis: 66.667%;\n" +
                    "                -ms-flex-preferred-size: 66.667%;\n" +
                    "                -webkit-flex-basis: 66.667%;\n" +
                    "                -ms-flex-basis: 66.667%;\n" +
                    "                flex-basis: 66.667%;\n" +
                    "                max-width: 66.667%;\n" +
                    "            }\n" +
                    "            .col-md-9 {\n" +
                    "                -webkit-flex-basis: 75%;\n" +
                    "                -ms-flex-preferred-size: 75%;\n" +
                    "                -webkit-flex-basis: 75%;\n" +
                    "                -ms-flex-basis: 75%;\n" +
                    "                flex-basis: 75%;\n" +
                    "                max-width: 75%;\n" +
                    "            }\n" +
                    "            .col-md-10 {\n" +
                    "                -webkit-flex-basis: 83.333%;\n" +
                    "                -ms-flex-preferred-size: 83.333%;\n" +
                    "                -webkit-flex-basis: 83.333%;\n" +
                    "                -ms-flex-basis: 83.333%;\n" +
                    "                flex-basis: 83.333%;\n" +
                    "                max-width: 83.333%;\n" +
                    "            }\n" +
                    "            .col-md-11 {\n" +
                    "                -webkit-flex-basis: 91.667%;\n" +
                    "                -ms-flex-preferred-size: 91.667%;\n" +
                    "                -webkit-flex-basis: 91.667%;\n" +
                    "                -ms-flex-basis: 91.667%;\n" +
                    "                flex-basis: 91.667%;\n" +
                    "                max-width: 91.667%;\n" +
                    "            }\n" +
                    "            .col-md-12 {\n" +
                    "                -webkit-flex-basis: 100%;\n" +
                    "                -ms-flex-preferred-size: 100%;\n" +
                    "                -webkit-flex-basis: 100%;\n" +
                    "                -ms-flex-basis: 100%;\n" +
                    "                flex-basis: 100%;\n" +
                    "                max-width: 100%;\n" +
                    "            }\n" +
                    "            .col-md-offset-1 {\n" +
                    "                margin-left: 8.333%;\n" +
                    "            }\n" +
                    "            .col-md-offset-2 {\n" +
                    "                margin-left: 16.667%;\n" +
                    "            }\n" +
                    "            .col-md-offset-3 {\n" +
                    "                margin-left: 25%;\n" +
                    "            }\n" +
                    "            .col-md-offset-4 {\n" +
                    "                margin-left: 33.333%;\n" +
                    "            }\n" +
                    "            .col-md-offset-5 {\n" +
                    "                margin-left: 41.667%;\n" +
                    "            }\n" +
                    "            .col-md-offset-6 {\n" +
                    "                margin-left: 50%;\n" +
                    "            }\n" +
                    "            .col-md-offset-7 {\n" +
                    "                margin-left: 58.333%;\n" +
                    "            }\n" +
                    "            .col-md-offset-8 {\n" +
                    "                margin-left: 66.667%;\n" +
                    "            }\n" +
                    "            .col-md-offset-9 {\n" +
                    "                margin-left: 75%;\n" +
                    "            }\n" +
                    "            .col-md-offset-10 {\n" +
                    "                margin-left: 83.333%;\n" +
                    "            }\n" +
                    "            .col-md-offset-11 {\n" +
                    "                margin-left: 91.667%;\n" +
                    "            }\n" +
                    "            .start-md {\n" +
                    "                -webkit-box-pack: start;\n" +
                    "                -webkit-justify-content: flex-start;\n" +
                    "                -ms-flex-pack: start;\n" +
                    "                -webkit-box-pack: start;\n" +
                    "                -webkit-justify-content: flex-start;\n" +
                    "                -ms-flex-pack: start;\n" +
                    "                justify-content: flex-start;\n" +
                    "                text-align: start;\n" +
                    "            }\n" +
                    "            .center-md {\n" +
                    "                -webkit-box-pack: center;\n" +
                    "                -webkit-justify-content: center;\n" +
                    "                -ms-flex-pack: center;\n" +
                    "                -webkit-box-pack: center;\n" +
                    "                -webkit-justify-content: center;\n" +
                    "                -ms-flex-pack: center;\n" +
                    "                justify-content: center;\n" +
                    "                text-align: center;\n" +
                    "            }\n" +
                    "            .end-md {\n" +
                    "                -webkit-box-pack: end;\n" +
                    "                -webkit-justify-content: flex-end;\n" +
                    "                -ms-flex-pack: end;\n" +
                    "                -webkit-box-pack: end;\n" +
                    "                -webkit-justify-content: flex-end;\n" +
                    "                -ms-flex-pack: end;\n" +
                    "                justify-content: flex-end;\n" +
                    "                text-align: end;\n" +
                    "            }\n" +
                    "            .top-md {\n" +
                    "                -webkit-box-align: start;\n" +
                    "                -webkit-align-items: flex-start;\n" +
                    "                -ms-flex-align: start;\n" +
                    "                -webkit-align-items: flex-start;\n" +
                    "                -webkit-box-align: flex-start;\n" +
                    "                -ms-flex-align: flex-start;\n" +
                    "                align-items: flex-start;\n" +
                    "            }\n" +
                    "            .middle-md {\n" +
                    "                -webkit-box-align: center;\n" +
                    "                -webkit-align-items: center;\n" +
                    "                -ms-flex-align: center;\n" +
                    "                -webkit-align-items: center;\n" +
                    "                -webkit-box-align: center;\n" +
                    "                -ms-flex-align: center;\n" +
                    "                align-items: center;\n" +
                    "            }\n" +
                    "            .bottom-md {\n" +
                    "                -webkit-box-align: end;\n" +
                    "                -webkit-align-items: flex-end;\n" +
                    "                -ms-flex-align: end;\n" +
                    "                -webkit-align-items: flex-end;\n" +
                    "                -webkit-box-align: flex-end;\n" +
                    "                -ms-flex-align: flex-end;\n" +
                    "                align-items: flex-end;\n" +
                    "            }\n" +
                    "            .around-md {\n" +
                    "                -webkit-justify-content: space-around;\n" +
                    "                -ms-flex-pack: distribute;\n" +
                    "                -webkit-box-pack: space-around;\n" +
                    "                -webkit-justify-content: space-around;\n" +
                    "                -ms-flex-pack: space-around;\n" +
                    "                justify-content: space-around;\n" +
                    "            }\n" +
                    "            .between-md {\n" +
                    "                -webkit-box-pack: justify;\n" +
                    "                -webkit-justify-content: space-between;\n" +
                    "                -ms-flex-pack: justify;\n" +
                    "                -webkit-box-pack: space-between;\n" +
                    "                -webkit-justify-content: space-between;\n" +
                    "                -ms-flex-pack: space-between;\n" +
                    "                justify-content: space-between;\n" +
                    "            }\n" +
                    "            .first-md {\n" +
                    "                -webkit-box-ordinal-group: 0;\n" +
                    "                -webkit-order: -1;\n" +
                    "                -ms-flex-order: -1;\n" +
                    "                -webkit-order: -1;\n" +
                    "                -ms-flex-order: -1;\n" +
                    "                order: -1;\n" +
                    "            }\n" +
                    "            .last-md {\n" +
                    "                -webkit-box-ordinal-group: 2;\n" +
                    "                -webkit-order: 1;\n" +
                    "                -ms-flex-order: 1;\n" +
                    "                -webkit-order: 1;\n" +
                    "                -ms-flex-order: 1;\n" +
                    "                order: 1;\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        @media only screen and (min-width:64em) {\n" +
                    "            .container {\n" +
                    "                width: 65rem;\n" +
                    "            }\n" +
                    "            .col-lg,\n" +
                    "            .col-lg-1,\n" +
                    "            .col-lg-10,\n" +
                    "            .col-lg-11,\n" +
                    "            .col-lg-12,\n" +
                    "            .col-lg-2,\n" +
                    "            .col-lg-3,\n" +
                    "            .col-lg-4,\n" +
                    "            .col-lg-5,\n" +
                    "            .col-lg-6,\n" +
                    "            .col-lg-7,\n" +
                    "            .col-lg-8,\n" +
                    "            .col-lg-9,\n" +
                    "            .col-lg-offset-1,\n" +
                    "            .col-lg-offset-10,\n" +
                    "            .col-lg-offset-11,\n" +
                    "            .col-lg-offset-12,\n" +
                    "            .col-lg-offset-2,\n" +
                    "            .col-lg-offset-3,\n" +
                    "            .col-lg-offset-4,\n" +
                    "            .col-lg-offset-5,\n" +
                    "            .col-lg-offset-6,\n" +
                    "            .col-lg-offset-7,\n" +
                    "            .col-lg-offset-8,\n" +
                    "            .col-lg-offset-9 {\n" +
                    "                box-sizing: border-box;\n" +
                    "                -webkit-box-flex: 0;\n" +
                    "                -webkit-flex: 0 0 auto;\n" +
                    "                -ms-flex: 0 0 auto;\n" +
                    "                -webkit-flex: 0 0 auto;\n" +
                    "                -ms-flex: 0 0 auto;\n" +
                    "                flex: 0 0 auto;\n" +
                    "                padding-right: .5rem;\n" +
                    "                padding-left: .5rem;\n" +
                    "            }\n" +
                    "            .col-lg {\n" +
                    "                -webkit-box-flex: 1;\n" +
                    "                -webkit-flex-grow: 1;\n" +
                    "                -ms-flex-positive: 1;\n" +
                    "                -webkit-flex-grow: 1;\n" +
                    "                -ms-flex-grow: 1;\n" +
                    "                flex-grow: 1;\n" +
                    "                -webkit-flex-basis: 0;\n" +
                    "                -ms-flex-preferred-size: 0;\n" +
                    "                -webkit-flex-basis: 0;\n" +
                    "                -ms-flex-basis: 0;\n" +
                    "                flex-basis: 0;\n" +
                    "                max-width: 100%;\n" +
                    "            }\n" +
                    "            .col-lg-1 {\n" +
                    "                -webkit-flex-basis: 8.333%;\n" +
                    "                -ms-flex-preferred-size: 8.333%;\n" +
                    "                -webkit-flex-basis: 8.333%;\n" +
                    "                -ms-flex-basis: 8.333%;\n" +
                    "                flex-basis: 8.333%;\n" +
                    "                max-width: 8.333%;\n" +
                    "            }\n" +
                    "            .col-lg-2 {\n" +
                    "                -webkit-flex-basis: 16.667%;\n" +
                    "                -ms-flex-preferred-size: 16.667%;\n" +
                    "                -webkit-flex-basis: 16.667%;\n" +
                    "                -ms-flex-basis: 16.667%;\n" +
                    "                flex-basis: 16.667%;\n" +
                    "                max-width: 16.667%;\n" +
                    "            }\n" +
                    "            .col-lg-3 {\n" +
                    "                -webkit-flex-basis: 25%;\n" +
                    "                -ms-flex-preferred-size: 25%;\n" +
                    "                -webkit-flex-basis: 25%;\n" +
                    "                -ms-flex-basis: 25%;\n" +
                    "                flex-basis: 25%;\n" +
                    "                max-width: 25%;\n" +
                    "            }\n" +
                    "            .col-lg-4 {\n" +
                    "                -webkit-flex-basis: 33.333%;\n" +
                    "                -ms-flex-preferred-size: 33.333%;\n" +
                    "                -webkit-flex-basis: 33.333%;\n" +
                    "                -ms-flex-basis: 33.333%;\n" +
                    "                flex-basis: 33.333%;\n" +
                    "                max-width: 33.333%;\n" +
                    "            }\n" +
                    "            .col-lg-5 {\n" +
                    "                -webkit-flex-basis: 41.667%;\n" +
                    "                -ms-flex-preferred-size: 41.667%;\n" +
                    "                -webkit-flex-basis: 41.667%;\n" +
                    "                -ms-flex-basis: 41.667%;\n" +
                    "                flex-basis: 41.667%;\n" +
                    "                max-width: 41.667%;\n" +
                    "            }\n" +
                    "            .col-lg-6 {\n" +
                    "                -webkit-flex-basis: 50%;\n" +
                    "                -ms-flex-preferred-size: 50%;\n" +
                    "                -webkit-flex-basis: 50%;\n" +
                    "                -ms-flex-basis: 50%;\n" +
                    "                flex-basis: 50%;\n" +
                    "                max-width: 50%;\n" +
                    "            }\n" +
                    "            .col-lg-7 {\n" +
                    "                -webkit-flex-basis: 58.333%;\n" +
                    "                -ms-flex-preferred-size: 58.333%;\n" +
                    "                -webkit-flex-basis: 58.333%;\n" +
                    "                -ms-flex-basis: 58.333%;\n" +
                    "                flex-basis: 58.333%;\n" +
                    "                max-width: 58.333%;\n" +
                    "            }\n" +
                    "            .col-lg-8 {\n" +
                    "                -webkit-flex-basis: 66.667%;\n" +
                    "                -ms-flex-preferred-size: 66.667%;\n" +
                    "                -webkit-flex-basis: 66.667%;\n" +
                    "                -ms-flex-basis: 66.667%;\n" +
                    "                flex-basis: 66.667%;\n" +
                    "                max-width: 66.667%;\n" +
                    "            }\n" +
                    "            .col-lg-9 {\n" +
                    "                -webkit-flex-basis: 75%;\n" +
                    "                -ms-flex-preferred-size: 75%;\n" +
                    "                -webkit-flex-basis: 75%;\n" +
                    "                -ms-flex-basis: 75%;\n" +
                    "                flex-basis: 75%;\n" +
                    "                max-width: 75%;\n" +
                    "            }\n" +
                    "            .col-lg-10 {\n" +
                    "                -webkit-flex-basis: 83.333%;\n" +
                    "                -ms-flex-preferred-size: 83.333%;\n" +
                    "                -webkit-flex-basis: 83.333%;\n" +
                    "                -ms-flex-basis: 83.333%;\n" +
                    "                flex-basis: 83.333%;\n" +
                    "                max-width: 83.333%;\n" +
                    "            }\n" +
                    "            .col-lg-11 {\n" +
                    "                -webkit-flex-basis: 91.667%;\n" +
                    "                -ms-flex-preferred-size: 91.667%;\n" +
                    "                -webkit-flex-basis: 91.667%;\n" +
                    "                -ms-flex-basis: 91.667%;\n" +
                    "                flex-basis: 91.667%;\n" +
                    "                max-width: 91.667%;\n" +
                    "            }\n" +
                    "            .col-lg-12 {\n" +
                    "                -webkit-flex-basis: 100%;\n" +
                    "                -ms-flex-preferred-size: 100%;\n" +
                    "                -webkit-flex-basis: 100%;\n" +
                    "                -ms-flex-basis: 100%;\n" +
                    "                flex-basis: 100%;\n" +
                    "                max-width: 100%;\n" +
                    "            }\n" +
                    "            .col-lg-offset-1 {\n" +
                    "                margin-left: 8.333%;\n" +
                    "            }\n" +
                    "            .col-lg-offset-2 {\n" +
                    "                margin-left: 16.667%;\n" +
                    "            }\n" +
                    "            .col-lg-offset-3 {\n" +
                    "                margin-left: 25%;\n" +
                    "            }\n" +
                    "            .col-lg-offset-4 {\n" +
                    "                margin-left: 33.333%;\n" +
                    "            }\n" +
                    "            .col-lg-offset-5 {\n" +
                    "                margin-left: 41.667%;\n" +
                    "            }\n" +
                    "            .col-lg-offset-6 {\n" +
                    "                margin-left: 50%;\n" +
                    "            }\n" +
                    "            .col-lg-offset-7 {\n" +
                    "                margin-left: 58.333%;\n" +
                    "            }\n" +
                    "            .col-lg-offset-8 {\n" +
                    "                margin-left: 66.667%;\n" +
                    "            }\n" +
                    "            .col-lg-offset-9 {\n" +
                    "                margin-left: 75%;\n" +
                    "            }\n" +
                    "            .col-lg-offset-10 {\n" +
                    "                margin-left: 83.333%;\n" +
                    "            }\n" +
                    "            .col-lg-offset-11 {\n" +
                    "                margin-left: 91.667%;\n" +
                    "            }\n" +
                    "            .start-lg {\n" +
                    "                -webkit-box-pack: start;\n" +
                    "                -webkit-justify-content: flex-start;\n" +
                    "                -ms-flex-pack: start;\n" +
                    "                -webkit-box-pack: start;\n" +
                    "                -webkit-justify-content: flex-start;\n" +
                    "                -ms-flex-pack: start;\n" +
                    "                justify-content: flex-start;\n" +
                    "                text-align: start;\n" +
                    "            }\n" +
                    "            .center-lg {\n" +
                    "                -webkit-box-pack: center;\n" +
                    "                -webkit-justify-content: center;\n" +
                    "                -ms-flex-pack: center;\n" +
                    "                -webkit-box-pack: center;\n" +
                    "                -webkit-justify-content: center;\n" +
                    "                -ms-flex-pack: center;\n" +
                    "                justify-content: center;\n" +
                    "                text-align: center;\n" +
                    "            }\n" +
                    "            .end-lg {\n" +
                    "                -webkit-box-pack: end;\n" +
                    "                -webkit-justify-content: flex-end;\n" +
                    "                -ms-flex-pack: end;\n" +
                    "                -webkit-box-pack: end;\n" +
                    "                -webkit-justify-content: flex-end;\n" +
                    "                -ms-flex-pack: end;\n" +
                    "                justify-content: flex-end;\n" +
                    "                text-align: end;\n" +
                    "            }\n" +
                    "            .top-lg {\n" +
                    "                -webkit-box-align: start;\n" +
                    "                -webkit-align-items: flex-start;\n" +
                    "                -ms-flex-align: start;\n" +
                    "                -webkit-align-items: flex-start;\n" +
                    "                -webkit-box-align: flex-start;\n" +
                    "                -ms-flex-align: flex-start;\n" +
                    "                align-items: flex-start;\n" +
                    "            }\n" +
                    "            .middle-lg {\n" +
                    "                -webkit-box-align: center;\n" +
                    "                -webkit-align-items: center;\n" +
                    "                -ms-flex-align: center;\n" +
                    "                -webkit-align-items: center;\n" +
                    "                -webkit-box-align: center;\n" +
                    "                -ms-flex-align: center;\n" +
                    "                align-items: center;\n" +
                    "            }\n" +
                    "            .bottom-lg {\n" +
                    "                -webkit-box-align: end;\n" +
                    "                -webkit-align-items: flex-end;\n" +
                    "                -ms-flex-align: end;\n" +
                    "                -webkit-align-items: flex-end;\n" +
                    "                -webkit-box-align: flex-end;\n" +
                    "                -ms-flex-align: flex-end;\n" +
                    "                align-items: flex-end;\n" +
                    "            }\n" +
                    "            .around-lg {\n" +
                    "                -webkit-justify-content: space-around;\n" +
                    "                -ms-flex-pack: distribute;\n" +
                    "                -webkit-box-pack: space-around;\n" +
                    "                -webkit-justify-content: space-around;\n" +
                    "                -ms-flex-pack: space-around;\n" +
                    "                justify-content: space-around;\n" +
                    "            }\n" +
                    "            .between-lg {\n" +
                    "                -webkit-box-pack: justify;\n" +
                    "                -webkit-justify-content: space-between;\n" +
                    "                -ms-flex-pack: justify;\n" +
                    "                -webkit-box-pack: space-between;\n" +
                    "                -webkit-justify-content: space-between;\n" +
                    "                -ms-flex-pack: space-between;\n" +
                    "                justify-content: space-between;\n" +
                    "            }\n" +
                    "            .first-lg {\n" +
                    "                -webkit-box-ordinal-group: 0;\n" +
                    "                -webkit-order: -1;\n" +
                    "                -ms-flex-order: -1;\n" +
                    "                -webkit-order: -1;\n" +
                    "                -ms-flex-order: -1;\n" +
                    "                order: -1;\n" +
                    "            }\n" +
                    "            .last-lg {\n" +
                    "                -webkit-box-ordinal-group: 2;\n" +
                    "                -webkit-order: 1;\n" +
                    "                -ms-flex-order: 1;\n" +
                    "                -webkit-order: 1;\n" +
                    "                -ms-flex-order: 1;\n" +
                    "                order: 1;\n" +
                    "            }\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "    <style type=\"text/css\" data-styled-components=\"iECmZH gilyel evpGNr byGfxM jFgvCd kktUDF jlPFGn eWjzRU jGqJlD kuqIKy ktKmOq iWVBpY hNogKD dggHpc jpHwD ipliua doYTvo eLCXzP hznfox cHZARy fhbjHj dRtAmU cULmEo hiKSiS cpoSvg kSOdgB GrYpZ dRiOjk cKjbEQ CLluW irjKNI dDRnsI YzqzA gEwPsy gLRoTZ bTVPzW eLBKpU bXYmZq jiDMyE cCPdDf kFuKSG JaVau iheQPW cDBKQv fKsNLk eRGRqS efxxAa eMaSBg fVrZls evjDXW bqVLhR gXwWwO etWFLc jhTbHC hRkhrw gEMvbe dJvZEL dBSBrt fJDlWs chprFS bxIgtu dQWvvR llGlaf cTengc idjmuw\"\n" +
                    "           data-styled-components-is-local=\"true\">\n" +
                    "        @media (max-width: 30rem) {\n" +
                    "            .cpoSvg {\n" +
                    "                font-size: 1rem !important;\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        @media (max-width: 30rem) {\n" +
                    "            .gEwPsy {\n" +
                    "                font-size: 1rem !important;\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        @media (max-width: 30rem) {\n" +
                    "            .eLBKpU {\n" +
                    "                font-size: 1.3090000000000002rem !important;\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        @media (max-width: 30rem) {\n" +
                    "            .bqVLhR {\n" +
                    "                font-size: 1rem !important;\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        @media (max-width: 30rem) {\n" +
                    "            .jFgvCd {\n" +
                    "                font-size: 1rem !important;\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        @media (max-width: 30rem) {\n" +
                    "            .eWjzRU {\n" +
                    "                font-size: 1.3090000000000002rem !important;\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        @media (max-width: 30rem) {\n" +
                    "            .cKjbEQ {\n" +
                    "                font-size: 1.3090000000000002rem !important;\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        @media (max-width: 30rem) {\n" +
                    "            .gLRoTZ {\n" +
                    "                font-size: 1rem !important;\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        @media (max-width: 30rem) {\n" +
                    "            .etWFLc {\n" +
                    "                font-size: 1rem !important;\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        @media (max-width: 30rem) {\n" +
                    "            .hNogKD {\n" +
                    "                font-size: 1rem !important;\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        @media (max-width: 30rem) {\n" +
                    "            .jpHwD {\n" +
                    "                font-size: 1rem !important;\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        @media (max-width: 30rem) {\n" +
                    "            .cULmEo {\n" +
                    "                font-size: 1rem !important;\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        @media (max-width: 30rem) {\n" +
                    "            .hiKSiS {\n" +
                    "                font-size: 1rem !important;\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        @media (max-width: 30rem) {\n" +
                    "            .bTVPzW {\n" +
                    "                font-size: 1rem !important;\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        @media (max-width: 30rem) {\n" +
                    "            .jiDMyE {\n" +
                    "                font-size: 1.618rem !important;\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        @media (max-width: 30rem) {\n" +
                    "            .cCPdDf {\n" +
                    "                font-size: 1.618rem !important;\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        @media (max-width: 30rem) {\n" +
                    "            .JaVau {\n" +
                    "                font-size: 1.3090000000000002rem !important;\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        @media (max-width: 30rem) {\n" +
                    "            .efxxAa {\n" +
                    "                font-size: 1rem !important;\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        @media (max-width: 30rem) {\n" +
                    "            .jhTbHC {\n" +
                    "                font-size: 1.3090000000000002rem !important;\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        @media (max-width: 30rem) {\n" +
                    "            .dBSBrt {\n" +
                    "                font-size: 1rem !important;\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        @media (max-width: 30rem) {\n" +
                    "            .fJDlWs {\n" +
                    "                font-size: 0.6180469715698392rem !important;\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        @media (max-width: 30rem) {\n" +
                    "            .chprFS {\n" +
                    "                font-size: 0.6180469715698392rem !important;\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        @media (max-width: 30rem) {\n" +
                    "            .bxIgtu {\n" +
                    "                font-size: 0.8090234857849197rem !important;\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        @media (max-width: 30rem) {\n" +
                    "            .dQWvvR {\n" +
                    "                font-size: 0.8090234857849197rem !important;\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        @media (max-width: 30rem) {\n" +
                    "            .cTengc {\n" +
                    "                font-size: 1.3090000000000002rem !important;\n" +
                    "            }\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "    <style type=\"text/css\" data-styled-components=\"\" data-styled-components-is-local=\"false\">\n" +
                    "        @media (max-width: 30rem) {\n" +
                    "            #component-playground .containerInner .fullWidthMobile,\n" +
                    "            .reactWrapper .containerInner .fullWidthMobile {\n" +
                    "                padding-left: 0;\n" +
                    "                padding-right: 0;\n" +
                    "                overflow-x: hidden;\n" +
                    "            }\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "\n" +
                    "<body style=\"min-width: 320px; background-color: #FFFFFF; width: 100%; margin: 0; padding: 0; -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%;\">\n" +
                    "<table width=\"100%\" height=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"left\"\n" +
                    "       valign=\"top\" style=\"border-collapse: collapse; border-spacing: 0;\">\n" +
                    "    <tbody>\n" +
                    "    <tr>\n" +
                    "        <td align=\"center\" valign=\"top\" style=\"padding: 0;\">\n" +
                    "            <table width=\"600\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse: collapse; border-spacing: 0; background-color: #F3F4F5; text-align: center;\"\n" +
                    "                   border=\"0\" valign=\"top\" bgcolor=\"#F3F4F5\">\n" +
                    "                <tbody>\n" +
                    "                <tr>\n" +
                    "                    <td style=\"padding: 0;\">\n" +
                    "                        <div class=\"sc-bdVaJa gilyel\" display=\"block\" style=\"background-color:#07484C ;box-sizing: border-box; position: static; border-radius: 0; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); overflow: inherit; margin: 0rem 0rem 0rem 0rem; border-top: none; border-right: none; border-bottom: none; border-left: none; display: block;\">\n" +
                    "                            <img class=\"sc-bwzfXH evpGNr\" src=\"cid:identifier1234\"\n" +
                    "                                 alt=\"Patreon trademark\" width=\"20\" style=\"border: 0; width: 10rem;\">\n" +
                    "                        </div>\n" +
                    "                    </td>\n" +
                    "                </tr>\n" +
                    "                <tr>\n" +
                    "                    <td style=\"padding: 0;\">\n" +
                    "                        <div class=\"sc-bdVaJa byGfxM\" display=\"block\" style=\"box-sizing: border-box; position: static; border-radius: 0; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); overflow: inherit; padding: 0rem 3rem 0rem 3rem; margin: 0rem 0rem 0rem 0rem; border-top: none; border-right: none; border-bottom: none; border-left: none; display: block;\"><span class=\"sc-bxivhb jFgvCd\" color=\"gray1\" scale=\"1\" style=\"color: #052D49; font-family: America, sans-serif; -webkit-letter-spacing: inherit; -moz-letter-spacing: inherit; -ms-letter-spacing: inherit; letter-spacing: inherit; margin: 0; opacity: 1; position: relative; text-align: inherit; text-transform: inherit; text-shadow: none; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); -webkit-user-select: inherit; -moz-user-select: inherit; -ms-user-select: inherit; user-select: inherit; font-size: 1rem; font-weight: 400; line-height: 1.5;\"><div class=\"sc-kgoBCf kktUDF\" overflow=\"hidden\" style=\"border-top: none; border-bottom: none; border-left: none; border-right: none; border-radius: 0; box-shadow: 0 2px 0 0 rgba(5, 45, 73, 0.06999999999999995); background-color: #FFFFFF; overflow: hidden;\"><div class=\"sc-bdVaJa jlPFGn\" display=\"block\" style=\"box-sizing: border-box; position: static; border-radius: 0; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); overflow: inherit; padding: 2rem 2rem 2rem 2rem; margin: 0rem 0rem 0rem 0rem; border-top: none; border-right: none; border-bottom: none; border-left: none; display: block;\"><div class=\"sc-htpNat cpoSvg\" color=\"gray1\" scale=\"1\" style=\"color: #052D49; font-family: America, sans-serif; -webkit-letter-spacing: inherit; -moz-letter-spacing: inherit; -ms-letter-spacing: inherit; letter-spacing: inherit; margin: 0; opacity: 1; position: relative; text-align: left; text-transform: inherit; text-shadow: none; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); -webkit-user-select: inherit; -moz-user-select: inherit; -ms-user-select: inherit; user-select: inherit; font-size: 1rem; font-weight: 400; line-height: 1.5;\"><div class=\"sc-bxivhb eWjzRU\" color=\"gray3\" scale=\"1\" size=\"2\" style=\"color: #4F687A; font-family: America, sans-serif; -webkit-letter-spacing: inherit; -moz-letter-spacing: inherit; -ms-letter-spacing: inherit; letter-spacing: inherit; margin: 0; opacity: 1; position: relative; text-align: inherit; text-transform: inherit; text-shadow: none; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); -webkit-user-select: inherit; -moz-user-select: inherit; -ms-user-select: inherit; user-select: inherit; font-size: 2.1090000000000002rem; font-weight: 400; line-height: 1.25; font-weight: bold; text-align: center;\">Reset Your Password</div>\n" +
                    "\n" +
                    "                      <div class=\"sc-bdVaJa dggHpc\" display=\"block\" style=\"box-sizing: border-box; position: static; border-radius: 0; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); overflow: inherit; padding: 0rem 0rem 0rem 0rem; margin: 1rem 0rem 1.5rem 0rem; border-top: none; border-right: none; border-bottom: none; border-left: none; display: block;\">Tap the button below to reset your account password. If you didn't request a new password, you can safely delete this email.</div>\n" +
                    "                    </div>\n" +
                    "                    <a class=\"sc-jzJRlG hznfox\" color=\"primary\" type=\"button\" href=\"" + frontendUrl + "/auth/reset-password/" + userDto.getId() + "\"\n" +
                    "                       role=\"button\" style=\"-webkit-backface-visibility: hidden; backface-visibility: hidden; background-color: #07484C; border: 2px solid #F96854; border-radius: 0; box-sizing: border-box; display: inline-block; font-weight: 700; padding: 1rem 1.5rem; position: relative; text-align: center; text-decoration: none; text-transform: uppercase; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); -webkit-user-select: none; -moz-user-select: none; -ms-user-select: none; user-select: none; white-space: inherit; cursor: pointer; color: #FFFFFF; font-size: 1rem;\">\n" +
                    "                      <div class=\"sc-fjdhpX ktKmOq\" style=\"visibility: visible;\">RESET YOUR PASSWORD</div>\n" +
                    "                    </a>\n" +
                    "                    </div>\n" +
                    "                    </div>\n" +
                    "                    </span>\n" +
                    "                        </div>\n" +
                    "                    </td>\n" +
                    "                </tr>\n" +
                    "                <tr>\n" +
                    "                    <td style=\"padding: 0;\">\n" +
                    "                        <div class=\"sc-bdVaJa gilyel\" display=\"block\" style=\"box-sizing: border-box; position: static; border-radius: 0; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); overflow: inherit; padding: 3rem 3rem 3rem 3rem; margin: 0rem 0rem 0rem 0rem; border-top: none; border-right: none; border-bottom: none; border-left: none; display: block;\"><span class=\"sc-bxivhb jFgvCd\" color=\"gray1\" scale=\"1\" style=\"color: #052D49; font-family: America, sans-serif; -webkit-letter-spacing: inherit; -moz-letter-spacing: inherit; -ms-letter-spacing: inherit; letter-spacing: inherit; margin: 0; opacity: 1; position: relative; text-align: inherit; text-transform: inherit; text-shadow: none; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); -webkit-user-select: inherit; -moz-user-select: inherit; -ms-user-select: inherit; user-select: inherit; font-size: 1rem; font-weight: 400; line-height: 1.5;\"><div class=\"sc-bdVaJa cHZARy\" display=\"block\" style=\"box-sizing: border-box; position: static; border-radius: 0; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); overflow: inherit; padding: 0rem 0rem 3rem 0rem; margin: 0rem 0rem 0rem 0rem; border-top: none; border-right: none; border-bottom: none; border-left: none; display: block;\"></div><div class=\"sc-bdVaJa dRtAmU\" display=\"block\" style=\"box-sizing: border-box; position: static; border-radius: 0; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); overflow: inherit; padding: 0rem 0rem 1.5rem 0rem; margin: 0rem 0rem 0rem 0rem; border-top: none; border-right: none; border-bottom: none; border-left: none; display: block;\"><p class=\"sc-ifAKCX cULmEo\" color=\"gray3\" scale=\"1\" style=\"color: #4F687A; font-family: America, sans-serif; -webkit-letter-spacing: inherit; -moz-letter-spacing: inherit; -ms-letter-spacing: inherit; letter-spacing: inherit; opacity: 1; position: relative; text-align: inherit; text-transform: inherit; text-shadow: none; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); -webkit-user-select: inherit; -moz-user-select: inherit; -ms-user-select: inherit; user-select: inherit; font-size: 1rem; font-weight: 400; line-height: 1.5; margin: 0.5rem 0rem;\">The link will be expired in 2 days<br>Have any questions? Please contact us with <a class=\"sc-gqjmRU doYTvo\" href=\"\" color=\"blue\" scale=\"1\" target=\"_self\" style=\"background-color: transparent; color: #F96854; cursor: pointer; display: inline-block; font-weight: 700; max-width: 100%; overflow: hidden; text-decoration: none; text-decoration-skip: ink; text-overflow: ellipsis; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); vertical-align: bottom; font-size: 1rem; line-height: 1.5;\">verifiedcontracting@gmail.com</a>.</p></div><div class=\"sc-bdVaJa cHZARy\" display=\"block\" style=\"box-sizing: border-box; position: static; border-radius: 0; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); overflow: inherit; padding: 0rem 0rem 3rem 0rem; margin: 0rem 0rem 0rem 0rem; border-top: none; border-right: none; border-bottom: none; border-left: none; display: block;\"><a class=\"sc-gqjmRU doYTvo\" href=\"\" color=\"blue\" scale=\"1\" target=\"_self\" style=\"background-color: transparent; color: #F96854; cursor: pointer; display: inline-block; font-weight: 700; max-width: 100%; overflow: hidden; text-decoration: none; text-decoration-skip: ink; text-overflow: ellipsis; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); vertical-align: bottom; font-size: 1rem; line-height: 1.5;\">LinkedIn</a><span style=\"margin-left:1em;margin-right:1em;\">·</span>\n" +
                    "\n" +
                    "                      <a class=\"sc-gqjmRU doYTvo\" href=\"https://twitter.com/verifeidcontracting\" color=\"blue\" scale=\"1\"\n" +
                    "                              target=\"_self\" style=\"background-color: transparent; color: #F96854; cursor: pointer; display: inline-block; font-weight: 700; max-width: 100%; overflow: hidden; text-decoration: none; text-decoration-skip: ink; text-overflow: ellipsis; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); vertical-align: bottom; font-size: 1rem; line-height: 1.5;\">Twitter</a><span style=\"margin-left:1em;margin-right:1em;\">·</span>\n" +
                    "\n" +
                    "                        <a class=\"sc-gqjmRU doYTvo\"\n" +
                    "                           href=\"https://www.facebook.com/verifiedcontracting\" color=\"blue\" scale=\"1\" target=\"_self\" style=\"background-color: transparent; color: #F96854; cursor: pointer; display: inline-block; font-weight: 700; max-width: 100%; overflow: hidden; text-decoration: none; text-decoration-skip: ink; text-overflow: ellipsis; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); vertical-align: bottom; font-size: 1rem; line-height: 1.5;\">Facebook</a>\n" +
                    "                    </div>\n" +
                    "                    <p class=\"sc-ifAKCX hiKSiS\" color=\"gray3\" scale=\"1\" style=\"color: #4F687A; font-family: America, sans-serif; -webkit-letter-spacing: inherit; -moz-letter-spacing: inherit; -ms-letter-spacing: inherit; letter-spacing: inherit; margin: 0; opacity: 1; position: relative; text-align: inherit; text-transform: inherit; text-shadow: none; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); -webkit-user-select: inherit; -moz-user-select: inherit; -ms-user-select: inherit; user-select: inherit; font-size: 1rem; font-weight: 400; line-height: 1.5;\">Verfied Contracting</p>\n" +
                    "                    <p class=\"sc-ifAKCX cULmEo\"\n" +
                    "                       color=\"gray3\" scale=\"1\" style=\"color: #4F687A; font-family: America, sans-serif; -webkit-letter-spacing: inherit; -moz-letter-spacing: inherit; -ms-letter-spacing: inherit; letter-spacing: inherit; opacity: 1; position: relative; text-align: inherit; text-transform: inherit; text-shadow: none; -webkit-transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); transition: all 300ms cubic-bezier(0.19, 1, 0.22, 1); -webkit-user-select: inherit; -moz-user-select: inherit; -ms-user-select: inherit; user-select: inherit; font-size: 1rem; font-weight: 400; line-height: 1.5; margin: 0.5rem 0rem;\">www.verifiedcontracting.com\n" +
                    "                      <br>Powered by Wixis 360 (Pvt) Ltd\n" +
                    "                        </span>\n" +
                    "                        </div>\n" +
                    "                    </td>\n" +
                    "                </tr>\n" +
                    "                </tbody>\n" +
                    "            </table>\n" +
                    "        </td>\n" +
                    "    </tr>\n" +
                    "    </tbody>\n" +
                    "</table>\n" +
                    "\n" +
                    "</body>\n" +
                    "\n" +
                    "</html>";

            emailService.send(userDto.getEmail(),"Verified Contracting - Request new password", trHtml);
            return true;
        }
        return false;
    }

    @Override
    public boolean resetPassword(UpdateUserPasswordDto updateUserPasswordDto) {
        Optional<User> optionalUser = userDao.findById(updateUserPasswordDto.getId());
        if (optionalUser.isEmpty()){
            return false;
        }
        User user = optionalUser.get();
        String encodePassword = Utility.encodePassword(updateUserPasswordDto.getPassword());
        int isUpdated = userDao.updatePassword(user.getId(), encodePassword);
        if (isUpdated > 0) {
            requestUserPasswordDao.deleteByUserId(user.getId());
            return true;
        }
        return false;
    }

    private UserDto getUserDto(User user){
        return mapper.map(user,UserDto.class);
    }

    private User getUser(UserDto userDto){
        return mapper.map(userDto,User.class);
    }

    private UserPreferredZipCodeDto getUserPreferredZipCodeDto(UserPreferredZipCode userPreferredZipCode){
        return mapper.map(userPreferredZipCode,UserPreferredZipCodeDto.class);
    }
}
