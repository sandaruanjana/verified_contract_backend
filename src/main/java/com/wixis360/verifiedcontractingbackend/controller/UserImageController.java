package com.wixis360.verifiedcontractingbackend.controller;

import com.wixis360.verifiedcontractingbackend.dto.*;
import com.wixis360.verifiedcontractingbackend.enums.APIResponseStatus;
import com.wixis360.verifiedcontractingbackend.service.UserImageService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/v1/user_image")
@AllArgsConstructor
public class UserImageController {
    private UserImageService userImageService;

    @PostMapping
    public ResponseEntity<APIResponse> upload(@RequestPart MultipartFile imageFile, @RequestParam String userId,
                                              @RequestParam String description, @RequestParam int isPublic) throws IOException {
        UploadUserImageDto uploadUserImageDto = new UploadUserImageDto();
        uploadUserImageDto.setImageFile(imageFile);
        uploadUserImageDto.setUserId(userId);
        uploadUserImageDto.setDescription(description);
        uploadUserImageDto.setIsPublic(isPublic);

        boolean isSaved = userImageService.save(uploadUserImageDto);

        if (isSaved) {
            APIResponse<UserDto> responseDTO = APIResponse
                    .<UserDto>builder()
                    .status(APIResponseStatus.SUCCESS.name())
                    .results(null)
                    .build();
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        }

        APIResponse<UserDto> responseDTO = APIResponse
                .<UserDto>builder()
                .status(APIResponseStatus.ERROR.name())
                .results(null)
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<APIPageResponse<List<UserImageDto>>> get(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int pageSize,
                                                                       @RequestParam(defaultValue = "NAME") String sortField,
                                                                       @RequestParam(defaultValue = "ASC") String sortOrder,
                                                                       @RequestParam(required = false) String searchTerm,
                                                                       @RequestParam String userId,
                                                                       @RequestParam int isPublic) {
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.fromString(sortOrder), sortField));
        Page<UserImageDto> userImageDtoPage = userImageService.findAll(pageable, userId, isPublic, searchTerm);
        APIPageResponse<List<UserImageDto>> responseDTO = APIPageResponse
                .<List<UserImageDto>>builder()
                .response(userImageDtoPage.getContent())
                .total(userImageDtoPage.getTotalElements())
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse> delete(@PathVariable String id) throws IOException {
        boolean isDeleted = userImageService.deleteById(id);
        if (isDeleted) {
            APIResponse<UserDto> responseDTO = APIResponse
                    .<UserDto>builder()
                    .status(APIResponseStatus.SUCCESS.name())
                    .results(null)
                    .build();
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }
        APIResponse<UserDto> responseDTO = APIResponse
                .<UserDto>builder()
                .status(APIResponseStatus.ERROR.name())
                .results(null)
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
    }
}
