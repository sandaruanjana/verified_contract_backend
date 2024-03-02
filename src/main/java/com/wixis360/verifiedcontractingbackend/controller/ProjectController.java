package com.wixis360.verifiedcontractingbackend.controller;

import com.wixis360.verifiedcontractingbackend.dto.*;
import com.wixis360.verifiedcontractingbackend.enums.APIResponseStatus;
import com.wixis360.verifiedcontractingbackend.security.service.UserDetailsImpl;
import com.wixis360.verifiedcontractingbackend.service.ProjectBidService;
import com.wixis360.verifiedcontractingbackend.service.ProjectImageService;
import com.wixis360.verifiedcontractingbackend.service.ProjectProgressService;
import com.wixis360.verifiedcontractingbackend.service.ProjectService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/v1/project")
@AllArgsConstructor
public class ProjectController {
    private ProjectService projectService;
    private ProjectImageService projectImageService;
    private ProjectBidService projectBidService;
    private ProjectProgressService projectProgressService;

    @PostMapping
    public ResponseEntity<APIResponse> save(@RequestBody ProjectDto projectDto) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        projectDto.setUserId(userDetails.getId());
        APIResponse<ProjectDto> responseDTO = APIResponse
                .<ProjectDto>builder()
                .status(APIResponseStatus.SUCCESS.name())
                .results(projectService.save(projectDto))
                .build();
        return ResponseEntity.created(null).body(responseDTO);
    }

    @GetMapping("/public/distance")
    public ResponseEntity<APIPageResponse<List<ProjectDto>>> getAllByIsPublicAndDistance(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int pageSize,
                                                                   @RequestParam(defaultValue = "NAME") String sortField,
                                                                   @RequestParam(defaultValue = "ASC") String sortOrder,
                                                                   @RequestParam(required = false) String searchTerm,
                                                                   @RequestParam String userId,
                                                                   @RequestParam int isPublic,
                                                                   @RequestParam int radius,
                                                                   @RequestParam(required = false) String category,
                                                                   @RequestParam String categoryOneId,
                                                                   @RequestParam String categoryTwoId,
                                                                   @RequestParam String categoryThreeId) {
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.fromString(sortOrder), sortField));
        Page<ProjectDto> projectDtoPage = projectService.findAll(pageable, userId, isPublic, radius, searchTerm, category, categoryOneId, categoryTwoId, categoryThreeId);
        APIPageResponse<List<ProjectDto>> responseDTO = APIPageResponse
                .<List<ProjectDto>>builder()
                .response(projectDtoPage.getContent())
                .total(projectDtoPage.getTotalElements())
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/assign/private")
    public ResponseEntity<APIPageResponse<List<ProjectDto>>> getAllAssignPrivate(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int pageSize,
                                                                   @RequestParam(defaultValue = "NAME") String sortField,
                                                                   @RequestParam(defaultValue = "ASC") String sortOrder,
                                                                   @RequestParam(required = false) String searchTerm,
                                                                   @RequestParam String status) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.fromString(sortOrder), sortField));
        Page<ProjectDto> projectDtoPage = projectService.findAllPrivateByAssignUserId(pageable, userDetails.getId(), status, searchTerm);
        APIPageResponse<List<ProjectDto>> responseDTO = APIPageResponse
                .<List<ProjectDto>>builder()
                .response(projectDtoPage.getContent())
                .total(projectDtoPage.getTotalElements())
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/assign/status")
    public ResponseEntity<APIPageResponse<List<ProjectDto>>> getAllAssignByStatus(@RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "10") int pageSize,
                                                                           @RequestParam(defaultValue = "NAME") String sortField,
                                                                           @RequestParam(defaultValue = "ASC") String sortOrder,
                                                                           @RequestParam(required = false) String searchTerm,
                                                                           @RequestParam String status) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.fromString(sortOrder), sortField));
        Page<ProjectDto> projectDtoPage = projectService.findAllByAssignUserId(pageable, userDetails.getId(), status, searchTerm);
        APIPageResponse<List<ProjectDto>> responseDTO = APIPageResponse
                .<List<ProjectDto>>builder()
                .response(projectDtoPage.getContent())
                .total(projectDtoPage.getTotalElements())
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/assignee/status")
    public ResponseEntity<APIPageResponse<List<ProjectDto>>> getAllAssigneeByStatus(@RequestParam(defaultValue = "0") int page,
                                                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                                                  @RequestParam(defaultValue = "NAME") String sortField,
                                                                                  @RequestParam(defaultValue = "ASC") String sortOrder,
                                                                                  @RequestParam(required = false) String searchTerm,
                                                                                  @RequestParam String status) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.fromString(sortOrder), sortField));
        Page<ProjectDto> projectDtoPage = projectService.findAllByUserId(pageable, userDetails.getId(), status, searchTerm);
        APIPageResponse<List<ProjectDto>> responseDTO = APIPageResponse
                .<List<ProjectDto>>builder()
                .response(projectDtoPage.getContent())
                .total(projectDtoPage.getTotalElements())
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PatchMapping("/status")
    public ResponseEntity<APIResponse> updateStatus(@RequestParam String id, @RequestParam String status,
                                                    @RequestParam(required = false) String rejectReason) {
        APIResponse<ProjectDto> responseDTO = APIResponse
                .<ProjectDto>builder()
                .status(APIResponseStatus.SUCCESS.name())
                .results(projectService.updateStatus(id, status, rejectReason))
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PatchMapping("/status/assign")
    public ResponseEntity<APIResponse> updateStatusAndAssignUser(@RequestParam String id, @RequestParam String status,
                                                                         @RequestParam String userId) {
        APIResponse<ProjectDto> responseDTO = APIResponse
                .<ProjectDto>builder()
                .status(APIResponseStatus.SUCCESS.name())
                .results(projectService.updateStatusAndAssignUser(id, status, userId))
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/image")
    public ResponseEntity<APIPageResponse<List<ProjectImageDto>>> getImages(@RequestParam String projectId) {
        List<ProjectImageDto> projectImageDtoList = projectImageService.findAllByProjectId(projectId);
        APIPageResponse<List<ProjectImageDto>> responseDTO = APIPageResponse
                .<List<ProjectImageDto>>builder()
                .response(projectImageDtoList)
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/bid")
    public ResponseEntity<APIResponse> saveBid(@RequestBody ProjectBidDto projectBidDto) {
        APIResponse<ProjectBidDto> responseDTO = APIResponse
                .<ProjectBidDto>builder()
                .status(APIResponseStatus.SUCCESS.name())
                .results(projectBidService.save(projectBidDto))
                .build();
        return ResponseEntity.created(null).body(responseDTO);
    }

    @GetMapping("/bid")
    public ResponseEntity<APIPageResponse<List<ProjectBidDto>>> getAllBids(@RequestParam(defaultValue = "0") int page,
                                                                                    @RequestParam(defaultValue = "10") int pageSize,
                                                                                    @RequestParam(defaultValue = "NAME") String sortField,
                                                                                    @RequestParam(defaultValue = "ASC") String sortOrder,
                                                                                    @RequestParam(required = false) String searchTerm,
                                                                                    @RequestParam String projectId) {
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.fromString(sortOrder), sortField));
        Page<ProjectBidDto> projectBidDtoPage = projectBidService.findAllByProjectId(pageable, projectId, searchTerm);
        APIPageResponse<List<ProjectBidDto>> responseDTO = APIPageResponse
                .<List<ProjectBidDto>>builder()
                .response(projectBidDtoPage.getContent())
                .total(projectBidDtoPage.getTotalElements())
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/progress")
    public ResponseEntity<APIResponse> saveProgress(@RequestBody ProjectProgressDto projectProgressDto) {
        APIResponse<ProjectProgressDto> responseDTO = APIResponse
                .<ProjectProgressDto>builder()
                .status(APIResponseStatus.SUCCESS.name())
                .results(projectProgressService.save(projectProgressDto))
                .build();
        return ResponseEntity.created(null).body(responseDTO);
    }

    @GetMapping("/progress")
    public ResponseEntity<APIPageResponse<List<ProjectProgressDto>>> getProgress(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int pageSize,
                                                                   @RequestParam(defaultValue = "NAME") String sortField,
                                                                   @RequestParam(defaultValue = "ASC") String sortOrder,
                                                                   @RequestParam(required = false) String searchTerm,
                                                                   @RequestParam String projectId) {
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.fromString(sortOrder), sortField));
        Page<ProjectProgressDto> projectProgressDtoPage = projectProgressService.findAll(pageable, projectId, searchTerm);
        APIPageResponse<List<ProjectProgressDto>> responseDTO = APIPageResponse
                .<List<ProjectProgressDto>>builder()
                .response(projectProgressDtoPage.getContent())
                .total(projectProgressDtoPage.getTotalElements())
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/progress/week/{projectId}")
    public ResponseEntity<?> getProgressLastWeekNumberByProjectId(@PathVariable String projectId) {
        APIResponse<Integer> responseDTO = APIResponse
                .<Integer>builder()
                .status(APIResponseStatus.SUCCESS.name())
                .results(projectProgressService.findLastWeekNumberByProjectId(projectId))
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProjectById(@PathVariable String id) {
        boolean delete = projectService.delete(id);
        APIResponse<Boolean> responseDTO = APIResponse
                .<Boolean>builder()
                .status(APIResponseStatus.SUCCESS.name())
                .results(delete)
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
