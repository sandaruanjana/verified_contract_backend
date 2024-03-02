package com.wixis360.verifiedcontractingbackend.service.impl;

import com.wixis360.verifiedcontractingbackend.dao.*;
import com.wixis360.verifiedcontractingbackend.dto.ProjectProgressDto;
import com.wixis360.verifiedcontractingbackend.dto.ProjectProgressImageDto;
import com.wixis360.verifiedcontractingbackend.model.*;
import com.wixis360.verifiedcontractingbackend.security.service.UserDetailsImpl;
import com.wixis360.verifiedcontractingbackend.service.EmailService;
import com.wixis360.verifiedcontractingbackend.service.ProjectProgressService;
import com.wixis360.verifiedcontractingbackend.util.ImageCompressUtil;
import com.wixis360.verifiedcontractingbackend.util.Utility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectProgressServiceImpl implements ProjectProgressService {
    private final ProjectDao projectDao;
    private final ProjectProgressDao projectProgressDao;
    private final ProjectProgressImageDao projectProgressImageDao;
    private final UserDao userDao;
    private final UserImageDao userImageDao;
    private final EmailService emailService;
    private final ModelMapper mapper;
    @Value("${image.upload.path}")
    String imageUploadPath;

    @Override
    public ProjectProgressDto save(ProjectProgressDto projectProgressDto) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        projectProgressDto.setId(UUID.randomUUID().toString());
        projectProgressDto.setCreatedTime(new Date());

        int isProjectProgressSaved = projectProgressDao.save(getProjectProgress(projectProgressDto));

        if (isProjectProgressSaved > 0) {
            Optional<Project> optionalProject = projectDao.findById(projectProgressDto.getProjectId());
            if (optionalProject.isPresent()) {
                Project project = optionalProject.get();
                Optional<User> optionalUser = userDao.findById(project.getUserId());
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    emailService.sendPlain(user.getEmail(), "Verified Contracting - New Project Progress", "You have a new project progress for your project " + project.getName() + " from " + userDetails.getName() + "");
                }
            }

            List<ProjectProgressImageDto> projectProgressImageDtoList = projectProgressDto.getImages();

            if (projectProgressImageDtoList != null) {
                for (ProjectProgressImageDto projectProgressImageDto : projectProgressImageDtoList) {
                    projectProgressImageDto.setId(UUID.randomUUID().toString());
                    projectProgressImageDto.setProjectProgressId(projectProgressDto.getId());
                    projectProgressImageDto.setProjectId(projectProgressDto.getProjectId());
                    try {
                        byte[] decodedBytes = Base64.getDecoder().decode(projectProgressImageDto.getName().split(",")[1]);
                        String name = projectProgressImageDto.getName();
                        String base64ImageType = name.split(",")[0];
                        String extension;
                        switch (base64ImageType) {
                            case "data:image/jpeg;base64":
                                extension = "jpeg";
                                break;
                            case "data:image/png;base64":
                                extension = "png";
                                break;
                            case "data:image/jpg;base64":
                                extension = "jpg";
                                break;
                            case "data:image/gif;base64":
                                extension = "gif";
                                break;
                            default://should write cases for more images types
                                extension = "png";
                                break;
                        }

                        projectProgressImageDto.setName(projectProgressImageDto.getId() + "." + extension);

                        FileUtils.writeByteArrayToFile(new File(imageUploadPath + projectProgressImageDto.getId() + "." + extension), decodedBytes);
                        File projectImageFile = new File(imageUploadPath + projectProgressImageDto.getId() + "." + extension);
                        byte[] bytes = ImageCompressUtil.compressImage(projectImageFile);
                        Path path = Paths.get(imageUploadPath + projectImageFile.getName());
                        Files.write(path, bytes);

                        projectProgressImageDto.setUploadTime(new Date());
                        projectProgressImageDao.save(getProjectProgressImage(projectProgressImageDto));

                        UserImage userImage = new UserImage();
                        userImage.setId(UUID.randomUUID().toString());
                        userImage.setUserId(userDetails.getId());
                        userImage.setProjectImageId(projectProgressImageDto.getId());
                        userImage.setName(userImage.getId() + "." + extension);
                        userImage.setDescription(projectProgressImageDto.getDescription());
                        userImage.setIsPublic(0);
                        userImage.setUploadTime(new Date());

                        FileUtils.writeByteArrayToFile(new File(imageUploadPath + userImage.getId() + "." + extension), decodedBytes);
                        File userImageFile = new File(imageUploadPath + userImage.getId() + "." + extension);
                        byte[] userImageBytes = ImageCompressUtil.compressImage(userImageFile);
                        Path userImagePath = Paths.get(imageUploadPath + userImageFile.getName());
                        Files.write(userImagePath, userImageBytes);

                        userImageDao.save(userImage);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }
            }

            return projectProgressDto;
        }

        return null;
    }

    @Override
    public Page<ProjectProgressDto> findAll(Pageable page, String projectId, String search) {
        Page<ProjectProgress> projectProgressPage = projectProgressDao.findAll(page, projectId, search);
        List<ProjectProgressDto> projectProgressDtoList = StreamSupport.stream(projectProgressPage.spliterator(), false)
                .map(this::getProjectProgressDto)
                .collect(Collectors.toList());
        for (ProjectProgressDto projectProgressDto : projectProgressDtoList) {
            List<ProjectProgressImage> projectProgressImages = projectProgressImageDao.findAllByProjectProgressId(projectProgressDto.getId());
            List<ProjectProgressImageDto> projectProgressImageDtoList = StreamSupport.stream(projectProgressImages.spliterator(), false)
                    .map(this::getProjectProgressImageDto)
                    .collect(Collectors.toList());
            projectProgressDto.getImages().addAll(projectProgressImageDtoList);
        }
        return new PageImpl<>(projectProgressDtoList, page, projectProgressPage.getTotalElements());
    }

    @Override
    public int findLastWeekNumberByProjectId(String projectId) {
        return projectProgressDao.findLastWeekNumberByProjectId(projectId);
    }

    private ProjectProgress getProjectProgress(ProjectProgressDto projectProgressDto) {
        return mapper.map(projectProgressDto, ProjectProgress.class);
    }

    private ProjectProgressDto getProjectProgressDto(ProjectProgress projectProgress) {
        return mapper.map(projectProgress, ProjectProgressDto.class);
    }

    private ProjectProgressImage getProjectProgressImage(ProjectProgressImageDto projectProgressImageDto) {
        return mapper.map(projectProgressImageDto, ProjectProgressImage.class);
    }

    private ProjectProgressImageDto getProjectProgressImageDto(ProjectProgressImage projectProgressImage) {
        return mapper.map(projectProgressImage, ProjectProgressImageDto.class);
    }
}
