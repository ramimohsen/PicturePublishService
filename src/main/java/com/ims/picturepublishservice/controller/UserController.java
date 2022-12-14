package com.ims.picturepublishservice.controller;

import com.ims.picturepublishservice.dto.ImageResponse;
import com.ims.picturepublishservice.exception.EntityNotFoundException;
import com.ims.picturepublishservice.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Validated
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final ImageService imageService;

    @PostMapping("/image/upload")
    @PreAuthorize("hasRole('USER')")
    public ImageResponse uploadImage(@RequestParam("file") MultipartFile file,
                                     @RequestPart("image") String image,
                                     @RequestHeader(name = AUTHORIZATION_HEADER) String token) throws IOException,
            EntityNotFoundException {
        return this.imageService.store(file, image, token);
    }
}


