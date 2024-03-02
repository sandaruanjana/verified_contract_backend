package com.wixis360.verifiedcontractingbackend.service.impl;

import com.wixis360.verifiedcontractingbackend.dao.UserImageDao;
import com.wixis360.verifiedcontractingbackend.dto.UploadUserImageDto;
import com.wixis360.verifiedcontractingbackend.dto.UserImageDto;
import com.wixis360.verifiedcontractingbackend.model.UserImage;
import com.wixis360.verifiedcontractingbackend.service.UserImageService;
import com.wixis360.verifiedcontractingbackend.util.ImageCompressUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Transactional
@Service
@RequiredArgsConstructor
public class UserImageServiceImpl implements UserImageService {
    private final UserImageDao userImageDao;
    private final ModelMapper mapper;
    @Value("${image.upload.path}")
    String imageUploadPath;

    @Override
    public boolean save(UploadUserImageDto uploadUserImageDto) throws IOException {
        MultipartFile imageFile = uploadUserImageDto.getImageFile();

        String extension = "";

        int i = imageFile.getOriginalFilename().lastIndexOf('.');

        if (i > 0) {
            extension = imageFile.getOriginalFilename().substring(i + 1);
        }

        String fileName = UUID.randomUUID().toString() + "." + extension;

        byte[] bytes = ImageCompressUtil.compressImage(imageFile);
        Path path = Paths.get(imageUploadPath + fileName);
        Files.write(path, bytes);

        UserImage userImage = getUserImage(uploadUserImageDto);
        userImage.setId(UUID.randomUUID().toString());
        userImage.setName(fileName);
        userImage.setUploadTime(new Date());

        return userImageDao.save(userImage) > 0;
    }

    @Override
    public Page<UserImageDto> findAll(Pageable page, String userId, int isPublic, String search) {
        Page<UserImage> userImagePage = userImageDao.findAll(page, userId, isPublic, search);
        List<UserImageDto> userImageDtoList = StreamSupport.stream(userImagePage.spliterator(), false)
                .map(this::getUserImageDto)
                .collect(Collectors.toList());
        return new PageImpl<>(userImageDtoList, page, userImagePage.getTotalElements());
    }

    @Override
    public boolean deleteById(String id) throws IOException {
        Optional<UserImage> optionalUserImage = userImageDao.findById(id);
        if (optionalUserImage.isPresent()) {
            UserImage userImage = optionalUserImage.get();
            if (userImageDao.deleteById(id) > 0) {
                Files.deleteIfExists(Paths.get(imageUploadPath + userImage.getName()));
                return true;
            }
            return false;
        }
        return false;
    }

    private UserImageDto getUserImageDto(UserImage userImage) {
        return mapper.map(userImage, UserImageDto.class);
    }

    private UserImage getUserImage(UploadUserImageDto uploadUserImageDto) {
        return mapper.map(uploadUserImageDto, UserImage.class);
    }
}
