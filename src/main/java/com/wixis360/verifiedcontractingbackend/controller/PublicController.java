package com.wixis360.verifiedcontractingbackend.controller;

import jakarta.activation.FileTypeMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api")
public class PublicController {
    @Value("${profile.picture.upload.path}")
    String profilePictureUploadPath;
    @Value("${image.upload.path}")
    String imageUploadPath;

    @GetMapping(value = "/uploads/profile_picture/{name:.+}")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable String name) throws IOException {
        File img = new File(profilePictureUploadPath + name);
        return ResponseEntity.ok().contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(img))).body(Files.readAllBytes(img.toPath()));
    }

    @GetMapping(value = "/uploads/image/{name:.+}")
    public ResponseEntity<byte[]> getImage(@PathVariable String name) throws IOException {
        File img = new File(imageUploadPath + name);
        return ResponseEntity.ok().contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(img))).body(Files.readAllBytes(img.toPath()));
    }
}
