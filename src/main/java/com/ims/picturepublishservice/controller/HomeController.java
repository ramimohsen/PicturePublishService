package com.ims.picturepublishservice.controller;


import com.ims.picturepublishservice.dto.ImageResponse;
import com.ims.picturepublishservice.entity.Image;
import com.ims.picturepublishservice.enums.Status;
import com.ims.picturepublishservice.exception.EntityNotFoundException;
import com.ims.picturepublishservice.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final ImageService imageService;

    @GetMapping("/images")
    public ResponseEntity<List<ImageResponse>> getAllAcceptedImages() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(this.imageService.getImagesByStatus(Status.ACCEPTED, "/api/home/images/"));
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable String id,
                                           @RequestHeader(name = AUTHORIZATION_HEADER) String token)
            throws EntityNotFoundException {
        Image image = imageService.getImageById(Integer.valueOf(id), token);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                        + image.getName() + "\"")
                .body(image.getData());
    }
}
