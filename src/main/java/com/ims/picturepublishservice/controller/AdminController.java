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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final ImageService imageService;

    @GetMapping("/images")
    @PreAuthorize("hasRole('ADMIN') ")
    public ResponseEntity<List<ImageResponse>> getUnProcessedImages() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(this.imageService.getImagesByStatus(Status.PROCESSING, "/api/admin/images/"));
    }

    @GetMapping("/images/{id}")
    @PreAuthorize("hasRole('ADMIN') ")
    public ResponseEntity<byte[]> getImage(@PathVariable String id,
                                           @RequestHeader(name = AUTHORIZATION_HEADER) String token)
            throws EntityNotFoundException {
        Image image = imageService.getImageById(Integer.valueOf(id), token);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                        + image.getName() + "\"")
                .body(image.getData());
    }


    @PostMapping("/images/{id}/accept")
    @PreAuthorize("hasRole('ADMIN') ")
    public ImageResponse acceptImage(@PathVariable String id) throws EntityNotFoundException {
        return this.imageService.acceptImage(id);
    }

    @PostMapping("/images/{id}/reject")
    @PreAuthorize("hasRole('ADMIN') ")
    public ImageResponse rejectImage(@PathVariable String id) throws EntityNotFoundException {
        return this.imageService.rejectImage(id);
    }
}
