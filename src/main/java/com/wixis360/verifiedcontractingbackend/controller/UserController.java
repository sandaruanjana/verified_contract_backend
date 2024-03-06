package com.wixis360.verifiedcontractingbackend.controller;

import com.wixis360.verifiedcontractingbackend.dto.*;
import com.wixis360.verifiedcontractingbackend.enums.APIResponseStatus;
import com.wixis360.verifiedcontractingbackend.security.service.UserDetailsImpl;
import com.wixis360.verifiedcontractingbackend.service.UserFavouriteUserService;
import com.wixis360.verifiedcontractingbackend.service.UserRatingService;
import com.wixis360.verifiedcontractingbackend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
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
@RequestMapping("api/v1/user")
@AllArgsConstructor
public class UserController {
    private UserService userService;
    private UserFavouriteUserService userFavouriteUserService;
    private UserRatingService userRatingService;

    @GetMapping("/distance")
    public ResponseEntity<APIPageResponse<List<UserDto>>> readByDistanceRange(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int pageSize,
                                                               @RequestParam(defaultValue = "NAME") String sortField,
                                                               @RequestParam(defaultValue = "ASC") String sortOrder,
                                                               @RequestParam(required = false) String searchTerm,
                                                               @RequestParam(required = false) String abilities,
                                                               @RequestParam(required = false) String skills,
                                                               @RequestParam String longitude,
                                                               @RequestParam String latitude,
                                                               @RequestParam String role) {
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.fromString(sortOrder), sortField));
        Page<UserDto> customerDtoPage = userService.findAllByDistanceRange(pageable, longitude, latitude, 1000, role, searchTerm, abilities, skills);
        APIPageResponse<List<UserDto>> responseDTO = APIPageResponse
                .<List<UserDto>>builder()
                .response(customerDtoPage.getContent())
                .total(customerDtoPage.getTotalElements())
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        UserDto userDtoResponse = userService.findById(id);
        APIResponse<UserDto> responseDTO = APIResponse
                .<UserDto>builder()
                .status(APIResponseStatus.SUCCESS.name())
                .results(userDtoResponse)
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<APIResponse> update(@RequestBody UserDto userDto) {
        UserDto userDtoResponse = userService.update(userDto);

        if (userDtoResponse == null) {
            APIResponse<UserDto> responseDTO = APIResponse
                    .<UserDto>builder()
                    .status(APIResponseStatus.ERROR.name())
                    .results(null)
                    .build();
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }

        APIResponse<UserDto> responseDTO = APIResponse
                .<UserDto>builder()
                .status(APIResponseStatus.SUCCESS.name())
                .results(userDtoResponse)
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PutMapping("/ability_skill")
    public ResponseEntity<APIResponse> updateAbilitySkill(@RequestBody UserAbilitySkillDto userAbilitySkillDto) {
        UserDto userDtoResponse = userService.updateAbilitySkill(userAbilitySkillDto);

        if (userDtoResponse == null) {
            APIResponse<UserDto> responseDTO = APIResponse
                    .<UserDto>builder()
                    .status(APIResponseStatus.ERROR.name())
                    .results(null)
                    .build();
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }

        APIResponse<UserDto> responseDTO = APIResponse
                .<UserDto>builder()
                .status(APIResponseStatus.SUCCESS.name())
                .results(userDtoResponse)
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PutMapping("/password")
    public ResponseEntity<APIResponse> updatePassword(@RequestBody UpdateUserPasswordDto updateUserPasswordDto) {
        boolean isUpdated = userService.updatePassword(updateUserPasswordDto);

        if (isUpdated) {
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

    @PostMapping("/favourite")
    public ResponseEntity<APIResponse> saveFavourite(@RequestBody UserFavouriteUserDto userFavouriteUserDto) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userFavouriteUserDto.setUserId(userDetails.getId());

        boolean isSaved = userFavouriteUserService.save(userFavouriteUserDto);

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

    @GetMapping("/favourite")
    public ResponseEntity<APIPageResponse<List<UserDto>>> getFavourite(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int pageSize,
                                                               @RequestParam(defaultValue = "NAME") String sortField,
                                                               @RequestParam(defaultValue = "ASC") String sortOrder,
                                                               @RequestParam(required = false) String searchTerm,
                                                               @RequestParam String id) {
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.fromString(sortOrder), sortField));
        Page<UserDto> customerDtoPage = userFavouriteUserService.findAllBySortAndPage(pageable, id, searchTerm);
        APIPageResponse<List<UserDto>> responseDTO = APIPageResponse
                .<List<UserDto>>builder()
                .response(customerDtoPage.getContent())
                .total(customerDtoPage.getTotalElements())
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/rating")
    public ResponseEntity<APIResponse> saveRating(@RequestBody UserRatingDto userRatingDto) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userRatingDto.setUserId(userDetails.getId());

        boolean isSaved = userRatingService.save(userRatingDto);

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

    @GetMapping("/rating")
    public ResponseEntity<APIResponse> getRating(@RequestParam String projectId) {

        try {
            UserRatingDto rating = userRatingService.getRating(projectId);
            APIResponse<UserRatingDto> responseDTO = APIResponse
                    .<UserRatingDto>builder()
                    .status(APIResponseStatus.SUCCESS.name())
                    .results(rating)
                    .build();
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {

            if(e instanceof EmptyResultDataAccessException){
                APIResponse<UserRatingDto> responseDTO = APIResponse
                        .<UserRatingDto>builder()
                        .status(APIResponseStatus.SUCCESS.name())
                        .results(null)
                        .build();
                return new ResponseEntity<>(responseDTO, HttpStatus.OK);
            }

            APIResponse<UserRatingDto> responseDTO = APIResponse
                    .<UserRatingDto>builder()
                    .status(APIResponseStatus.ERROR.name())
                    .results(null)
                    .build();
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);

        }
    }

    @GetMapping("/rating/average/{contractorId}")
    public ResponseEntity<APIResponse> getAverageRateForContractor(@PathVariable String contractorId) {

        try {
            double averageRateForContractor = userRatingService.getAverageRateForContractor(contractorId);
            APIResponse<Double> responseDTO = APIResponse
                    .<Double>builder()
                    .status(APIResponseStatus.SUCCESS.name())
                    .results(averageRateForContractor)
                    .build();
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            APIResponse<UserRatingDto> responseDTO = APIResponse
                    .<UserRatingDto>builder()
                    .status(APIResponseStatus.ERROR.name())
                    .results(null)
                    .build();
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);

        }
    }
}
