package com.wixis360.verifiedcontractingbackend.service.impl;

import com.wixis360.verifiedcontractingbackend.dao.ProjectBidDao;
import com.wixis360.verifiedcontractingbackend.dao.ProjectDao;
import com.wixis360.verifiedcontractingbackend.dao.UserDao;
import com.wixis360.verifiedcontractingbackend.dto.ProjectBidDto;
import com.wixis360.verifiedcontractingbackend.model.Project;
import com.wixis360.verifiedcontractingbackend.model.ProjectBid;
import com.wixis360.verifiedcontractingbackend.model.User;
import com.wixis360.verifiedcontractingbackend.security.service.UserDetailsImpl;
import com.wixis360.verifiedcontractingbackend.service.EmailService;
import com.wixis360.verifiedcontractingbackend.service.ProjectBidService;
import com.wixis360.verifiedcontractingbackend.util.Utility;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Transactional
@Service
@AllArgsConstructor
public class ProjectBidServiceImpl implements ProjectBidService {
    private ProjectDao projectDao;
    private ProjectBidDao projectBidDao;
    private UserDao userDao;
    private EmailService emailService;
    private ModelMapper mapper;

    @Override
    public ProjectBidDto save(ProjectBidDto projectBidDto) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        projectBidDto.setId(UUID.randomUUID().toString());
        projectBidDto.setUserId(userDetails.getId());
        projectBidDto.setUserName(userDetails.getName());
        projectBidDto.setCreatedTime(new Date());

        projectBidDao.deleteByProjectIdAndUserId(projectBidDto.getProjectId(), projectBidDto.getUserId());

        if (projectBidDto.getAmount() == 0) {
            return projectBidDto;
        }

        if (projectBidDao.save(getProjectBid(projectBidDto)) > 0) {
            Optional<Project> optionalProject = projectDao.findById(projectBidDto.getProjectId());
            if (optionalProject.isPresent()) {
                Project project = optionalProject.get();
                Optional<User> optionalUser = userDao.findById(project.getUserId());
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    emailService.sendPlain(user.getEmail(), "Verified Contracting - New Bid", "You have a new bid for your project " + project.getName() + " from " + projectBidDto.getUserName() + " for $" + Utility.convertDouble(projectBidDto.getAmount()) + "");
                }
            }
            return projectBidDto;
        }

        return null;
    }

    @Override
    public Page<ProjectBidDto> findAllByProjectId(Pageable page, String projectId, String search) {
        Page<ProjectBid> projectBidPage = projectBidDao.findAllByProjectId(page, projectId, search);
        List<ProjectBidDto> projectBidDtoList = StreamSupport.stream(projectBidPage.spliterator(), false)
                .map(this::getProjectBidDto)
                .collect(Collectors.toList());
        return new PageImpl<>(projectBidDtoList, page, projectBidPage.getTotalElements());
    }

    private ProjectBid getProjectBid(ProjectBidDto projectBidDto) {
        return mapper.map(projectBidDto, ProjectBid.class);
    }

    private ProjectBidDto getProjectBidDto(ProjectBid projectBid) {
        return mapper.map(projectBid, ProjectBidDto.class);
    }
}
