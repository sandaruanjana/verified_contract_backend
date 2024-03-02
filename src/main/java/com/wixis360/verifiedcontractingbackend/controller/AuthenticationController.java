package com.wixis360.verifiedcontractingbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wixis360.verifiedcontractingbackend.dto.*;
import com.wixis360.verifiedcontractingbackend.enums.APIResponseStatus;
import com.wixis360.verifiedcontractingbackend.security.jwt.JwtUtils;
import com.wixis360.verifiedcontractingbackend.security.service.UserDetailsImpl;
import com.wixis360.verifiedcontractingbackend.service.RequestUserPasswordService;
import com.wixis360.verifiedcontractingbackend.service.UserService;
import com.wixis360.verifiedcontractingbackend.util.Utility;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api")
public class AuthenticationController {
    @Autowired
    UserService userService;
    @Autowired
    RequestUserPasswordService requestUserPasswordService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> getToken(@RequestBody UserDto userDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        long expiresIn = jwtUtils.getExpirationMsFromJwtToken(jwt);

        return ResponseEntity.ok(new TokenResponse(jwt, expiresIn));
    }

    @GetMapping("/user")
    public ResponseEntity<APIResponse> get() {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserDto userDto = userService.findById(userDetails.getId());
            APIResponse<UserDto> responseDTO = APIResponse
                    .<UserDto>builder()
                    .status(APIResponseStatus.SUCCESS.name())
                    .results(userDto)
                    .build();
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<APIResponse> signup(@RequestBody UserDto userDto) throws IOException, MessagingException {
        UserDto userDtoResponse = userService.save(userDto);

        if (userDtoResponse == null) {
            APIResponse<UserDto> responseDTO = APIResponse
                    .<UserDto>builder()
                    .status(APIResponseStatus.ERROR.name())
                    .results(null)
                    .build();
            return new ResponseEntity<>(responseDTO, HttpStatus.CONFLICT);
        }

        APIResponse<UserDto> responseDTO = APIResponse
                .<UserDto>builder()
                .status(APIResponseStatus.SUCCESS.name())
                .results(userDtoResponse)
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/verify/{id}")
    public ResponseEntity<APIResponse> verify(@PathVariable String id) throws JsonProcessingException {
        UserDto userDtoResponse = userService.verify(id);

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

    @PostMapping("/forgot_password")
    public ResponseEntity<APIResponse> forgotPassword(@RequestParam String email) throws IOException, MessagingException {
        UserDto userDto = userService.findByEmail(email);

        if (userDto == null) {
            APIResponse<UserDto> responseDTO = APIResponse
                    .<UserDto>builder()
                    .status(APIResponseStatus.ERROR.name())
                    .results(null)
                    .build();
            return new ResponseEntity<>(responseDTO, HttpStatus.NO_CONTENT);
        }

        boolean isSent = userService.forgotPassword(userDto);

        if (isSent) {
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

    @GetMapping("/reset_password/{userId}")
    public ResponseEntity<APIResponse> checkResetPasswordLinkExpired(@PathVariable String userId) {
        RequestUserPasswordDto requestUserPasswordDto = requestUserPasswordService.findByUserId(userId);
        if (requestUserPasswordDto == null) {
            APIResponse<UserDto> responseDTO = APIResponse
                    .<UserDto>builder()
                    .status(APIResponseStatus.ERROR.name())
                    .results(null)
                    .build();
            return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
        }

        Date expireDate = Utility.incrementDate(requestUserPasswordDto.getCreatedTime(), 2);
        Date currentDate = new Date();

        if (currentDate.after(expireDate)) {
            requestUserPasswordService.deleteByUserId(userId);
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
                .results(null)
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PatchMapping("/reset_password")
    public ResponseEntity<APIResponse> resetPassword(@RequestBody UpdateUserPasswordDto updateUserPasswordDto) {
        boolean isUpdated = userService.resetPassword(updateUserPasswordDto);

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
}
