package com.wixis360.verifiedcontractingbackend.service.impl;

import com.wixis360.verifiedcontractingbackend.dao.RequestUserPasswordDao;
import com.wixis360.verifiedcontractingbackend.dto.RequestUserPasswordDto;
import com.wixis360.verifiedcontractingbackend.model.RequestUserPassword;
import com.wixis360.verifiedcontractingbackend.service.RequestUserPasswordService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@AllArgsConstructor
public class RequestUserPasswordServiceImpl implements RequestUserPasswordService {
    private RequestUserPasswordDao requestUserPasswordDao;
    private ModelMapper mapper;

    @Override
    public RequestUserPasswordDto findByUserId(String userId) {
        return requestUserPasswordDao.findByUserId(userId).map(this::getRequestUserPasswordDto).orElse(null);
    }

    @Override
    public boolean deleteByUserId(String userId) {
        return requestUserPasswordDao.deleteByUserId(userId) > 0;
    }

    private RequestUserPasswordDto getRequestUserPasswordDto(RequestUserPassword requestUserPassword) {
        return mapper.map(requestUserPassword, RequestUserPasswordDto.class);
    }
}
