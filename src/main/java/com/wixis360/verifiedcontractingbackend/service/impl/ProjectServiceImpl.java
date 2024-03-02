package com.wixis360.verifiedcontractingbackend.service.impl;

import com.wixis360.verifiedcontractingbackend.dao.*;
import com.wixis360.verifiedcontractingbackend.dto.ProjectDto;
import com.wixis360.verifiedcontractingbackend.dto.ProjectImageDto;
import com.wixis360.verifiedcontractingbackend.dto.UserDto;
import com.wixis360.verifiedcontractingbackend.enums.ProjectStatus;
import com.wixis360.verifiedcontractingbackend.model.*;
import com.wixis360.verifiedcontractingbackend.service.EmailService;
import com.wixis360.verifiedcontractingbackend.service.ProjectService;
import com.wixis360.verifiedcontractingbackend.service.UserService;
import com.wixis360.verifiedcontractingbackend.util.ImageCompressUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
public class ProjectServiceImpl implements ProjectService {
    private final ProjectDao projectDao;
    private final ProjectImageDao projectImageDao;
    private final ProjectBidDao projectBidDao;
    private final UserDao userDao;
    private final UserImageDao userImageDao;
    private final EmailService emailService;
    private final ModelMapper mapper;
    @Value("${image.upload.path}")
    String imageUploadPath;

    @Override
    public ProjectDto save(ProjectDto projectDto) {
        projectDto.setId(UUID.randomUUID().toString());
        projectDto.setCreatedTime(new Date());

        if (projectDto.getAssignUserId() != null) {
            Optional<User> optionalUser = userDao.findById(projectDto.getAssignUserId());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                emailService.sendPlain(user.getEmail(), "Verified Contracting - New Project", "You have a new project to work on. Please check your my projects section.");
            }
        }

        int isProjectSaved = projectDao.save(getProject(projectDto));

        if (isProjectSaved > 0) {
            List<ProjectImageDto> projectImageDtoList = projectDto.getImages();

            if (projectImageDtoList != null) {
                for (ProjectImageDto projectImageDto : projectImageDtoList) {
                    projectImageDto.setId(UUID.randomUUID().toString());
                    projectImageDto.setProjectId(projectDto.getId());
                    try {
                        byte[] decodedBytes = Base64.getDecoder().decode(projectImageDto.getName().split(",")[1]);
                        String name = projectImageDto.getName();
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

                        projectImageDto.setName(projectImageDto.getId() + "." + extension);

                        FileUtils.writeByteArrayToFile(new File(imageUploadPath + projectImageDto.getId() + "." + extension), decodedBytes);
                        File projectImageFile = new File(imageUploadPath + projectImageDto.getId() + "." + extension);
                        byte[] bytes = ImageCompressUtil.compressImage(projectImageFile);
                        Path path = Paths.get(imageUploadPath + projectImageFile.getName());
                        Files.write(path, bytes);

                        projectImageDto.setUploadTime(new Date());
                        projectImageDao.save(getProjectImage(projectImageDto));

                        UserImage userImage = new UserImage();
                        userImage.setId(UUID.randomUUID().toString());
                        userImage.setUserId(projectDto.getUserId());
                        userImage.setProjectImageId(projectImageDto.getId());
                        userImage.setName(userImage.getId() + "." + extension);
                        userImage.setDescription(projectImageDto.getDescription());
                        userImage.setIsPublic(projectDto.getIsPublic());
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

            return projectDto;
        }

        return null;
    }

    @Override
    public Page<ProjectDto> findAll(Pageable page, String userId, int isPublic, int radius, String search, String category, String categoryOneId, String categoryTwoId, String categoryThreeId) {
        Page<Project> projectPage = projectDao.findAll(page, userId, isPublic, radius, search, category, categoryOneId, categoryTwoId, categoryThreeId);
        List<ProjectDto> projectDtoList = StreamSupport.stream(projectPage.spliterator(), false)
                .map(this::getProjectDto)
                .collect(Collectors.toList());
        List<ProjectDto> projectList = new ArrayList<>();
        for (ProjectDto projectDto : projectDtoList) {
            Optional<ProjectBid> optionalProjectBid = projectBidDao.findByProjectIdAndUserId(projectDto.getId(), userId);
            if (optionalProjectBid.isPresent()) {
                projectDto.setIsBided(1);
                projectDto.setBidAmount(optionalProjectBid.get().getAmount());
            }
            projectList.add(projectDto);
        }
        return new PageImpl<>(projectList, page, projectPage.getTotalElements());
    }

    @Override
    public Page<ProjectDto> findAllByAssignUserId(Pageable page, String assignUserId, String status, String search) {
        Page<Project> projectPage = projectDao.findAllByAssignUserId(page, assignUserId, status, search);
        List<ProjectDto> projectDtoList = StreamSupport.stream(projectPage.spliterator(), false)
                .map(this::getProjectDto)
                .collect(Collectors.toList());
        return new PageImpl<>(projectDtoList, page, projectPage.getTotalElements());
    }

    @Override
    public Page<ProjectDto> findAllPrivateByAssignUserId(Pageable page, String assignUserId, String status, String search) {
        Page<Project> projectPage = projectDao.findAllPrivateByAssignUserId(page, assignUserId, status, search);
        List<ProjectDto> projectDtoList = StreamSupport.stream(projectPage.spliterator(), false)
                .map(this::getProjectDto)
                .collect(Collectors.toList());
        return new PageImpl<>(projectDtoList, page, projectPage.getTotalElements());
    }

    @Override
    public Page<ProjectDto> findAllByUserId(Pageable page, String userId, String status, String search) {
        Page<Project> projectPage = projectDao.findAllByUserId(page, userId, status, search);
        List<ProjectDto> projectDtoList = StreamSupport.stream(projectPage.spliterator(), false)
                .map(this::getProjectDto)
                .collect(Collectors.toList());
        return new PageImpl<>(projectDtoList, page, projectPage.getTotalElements());
    }

    @Override
    public ProjectDto updateStatus(String id, String status, String rejectReason) {
        Optional<Project> optionalProject = projectDao.findById(id);
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();
            int isUpdated = 0;

            if (status.equalsIgnoreCase(ProjectStatus.REJECTED.name())) {
                if (rejectReason.isBlank()) {
                    rejectReason = null;
                }
                isUpdated = projectDao.updateStatusAndRejectReason(id, status, rejectReason);
            } else {
                isUpdated = projectDao.updateStatus(id, status);
            }

            if (isUpdated > 0) {
                if (status.equalsIgnoreCase(ProjectStatus.ACCEPTED.name())) {
                    if (project.getIsPublic() == 0) {
                        Optional<User> optionalUser = userDao.findById(project.getUserId());
                        if (optionalUser.isPresent()) {
                            User user = optionalUser.get();
                            emailService.sendPlain(user.getEmail(), "Verified Contracting - Accepted - Project: " + project.getName(), "Your private project " + project.getName() + " has been accepted.");
                        }
                    } else {
                        Optional<User> optionalUser = userDao.findById(project.getAssignUserId());
                        if (optionalUser.isPresent()) {
                            User user = optionalUser.get();
                            emailService.sendPlain(user.getEmail(), "Verified Contracting - Bid Accepted - Project: " + project.getName(), "Your bid has been accepted for the project " + project.getName() + ".");
                        }
                    }
                } else if (status.equalsIgnoreCase(ProjectStatus.REJECTED.name())) {
                    Optional<User> optionalUser = userDao.findById(project.getUserId());
                    if (optionalUser.isPresent()) {
                        User user = optionalUser.get();
                        emailService.sendPlain(user.getEmail(), "Verified Contracting - Rejected - Project: " + project.getName(), "Your private project " + project.getName() + " has been rejected.");
                    }
                } else if (status.equalsIgnoreCase(ProjectStatus.COMPLETED.name())) {
                    Optional<User> optionalUser = userDao.findById(project.getUserId());
                    if (optionalUser.isPresent()) {
                        User user = optionalUser.get();
                        emailService.sendPlain(user.getEmail(), "Verified Contracting - Completed - Project: " + project.getName(), "Your private project " + project.getName() + " has been completed.");
                    }
                } else if (status.equalsIgnoreCase(ProjectStatus.IN_PROGRESS.name())) {
                    Optional<User> optionalUser = userDao.findById(project.getUserId());
                    if (optionalUser.isPresent()) {
                        User user = optionalUser.get();
                        emailService.sendPlain(user.getEmail(), "Verified Contracting - Started - Project: " + project.getName(), "Your project " + project.getName() + " has been started.");
                    }
                }

                project.setStatus(status);
                return getProjectDto(project);
            }
            return null;
        }
        return null;
    }

    @Override
    public ProjectDto updateStatusAndAssignUser(String id, String status, String assignUserId) {
        Optional<Project> optionalProject = projectDao.findById(id);
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();
            int isUpdated = projectDao.updateStatusAndAssignUser(id, status, assignUserId);

            if (isUpdated > 0) {
                Optional<User> optionalUser = userDao.findById(assignUserId);
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    if (project.getIsPublic() == 0) {
                        emailService.sendPlain(user.getEmail(), "Verified Contracting - New Project", "You have a new project to work on. Please check your my projects section.");
                    } else {
                        emailService.sendPlain(user.getEmail(), "Verified Contracting - Bid Accepted - Project: " + project.getName(), "Your bid has been accepted for the project " + project.getName() + ".");
                    }
                }
                project.setAssignUserId(assignUserId);
                project.setStatus(status);
                return getProjectDto(project);
            }
            return null;
        }
        return null;
    }

    @Override
    public boolean delete(String id) {
        return projectDao.delete(id) > 0;
    }

    private Project getProject(ProjectDto projectDto) {
        return mapper.map(projectDto, Project.class);
    }

    private ProjectDto getProjectDto(Project project) {
        return mapper.map(project, ProjectDto.class);
    }

    private ProjectImage getProjectImage(ProjectImageDto projectImageDto) {
        return mapper.map(projectImageDto, ProjectImage.class);
    }
}
