package com.ims.picturepublishservice.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ims.picturepublishservice.dto.ImageResponse;
import com.ims.picturepublishservice.dto.ImageUploadRequest;
import com.ims.picturepublishservice.entity.Image;
import com.ims.picturepublishservice.entity.User;
import com.ims.picturepublishservice.enums.Status;
import com.ims.picturepublishservice.enums.SystemRole;
import com.ims.picturepublishservice.exception.EntityNotFoundException;
import com.ims.picturepublishservice.repository.ImageRepository;
import com.ims.picturepublishservice.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    private final ImageRepository imageRepository;

    private final JwtUtils jwtUtils;

    private final UserService userService;


    @Transactional
    public List<ImageResponse> getImagesByStatus(Status status, String path) {
        return this.imageRepository.findAllByStatus(status).stream()
                .map(image -> {
                    String fileDownloadUri = ServletUriComponentsBuilder
                            .fromCurrentContextPath()
                            .path(path)
                            .path(image.getId().toString())
                            .toUriString();
                    return ImageResponse.builder()
                            .category(image.getCategory())
                            .description(image.getDescription())
                            .type(image.getType())
                            .name(image.getName())
                            .status(image.getStatus())
                            .id(image.getId()).url(fileDownloadUri).build();
                }).collect(Collectors.toList());
    }

    @Transactional
    public Image getImageById(Integer id, String token) throws EntityNotFoundException {

        Image image = this.imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("image is not found."));

        User user = this.userService
                .findByUserName(jwtUtils.getUserNameFromJwtToken(token.replace("Bearer ", "")));

        if (image.getStatus().equals(Status.PROCESSING) && user.getRoles()
                .stream().anyMatch(role -> role.getName().equals(SystemRole.ROLE_USER))) {
            throw new MethodNotAllowedException("You are not allowed to access this resource", null);
        }
        return image;
    }


    @Transactional
    public ImageResponse acceptImage(String id) throws EntityNotFoundException {

        Image image = this.imageRepository.findById(Integer.valueOf(id))
                .orElseThrow(() -> new EntityNotFoundException("image is not found."));

        image.setStatus(Status.ACCEPTED);
        image.setProcessedAt(LocalDateTime.now());
        Image savedImage = this.imageRepository.save(image);

        return ImageResponse.builder()
                .category(savedImage.getCategory())
                .description(savedImage.getDescription())
                .type(savedImage.getType())
                .name(savedImage.getName())
                .status(savedImage.getStatus())
                .id(savedImage.getId()).build();
    }

    @Transactional
    public ImageResponse rejectImage(String id) throws EntityNotFoundException {

        Image image = this.imageRepository.findById(Integer.valueOf(id))
                .orElseThrow(() -> new EntityNotFoundException("image is not found."));

        image.setStatus(Status.REJECTED);
        image.setProcessedAt(LocalDateTime.now());
        image.setData(null);
        Image savedImage = this.imageRepository.save(image);

        return ImageResponse.builder()
                .category(savedImage.getCategory())
                .description(savedImage.getDescription())
                .type(savedImage.getType())
                .name(savedImage.getName())
                .status(savedImage.getStatus())
                .id(savedImage.getId()).build();
    }
    public ImageResponse store(MultipartFile file, String imageRequest, String token) throws IOException,
            EntityNotFoundException {

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        ImageUploadRequest imageUploadRequest = this.mapToJson(imageRequest);
        User user = this.userService
                .findByUserName(jwtUtils.getUserNameFromJwtToken(token.replace("Bearer ", "")));

        Image image = Image.builder().type(file.getContentType())
                .category(imageUploadRequest.getCategory())
                .description(imageUploadRequest.getDescription())
                .data(file.getBytes())
                .name(fileName)
                .status(Status.PROCESSING).user(user).build();

        Image savedImage = this.imageRepository.save(image);

        return ImageResponse.builder()
                .category(savedImage.getCategory())
                .description(savedImage.getDescription())
                .type(savedImage.getType())
                .name(savedImage.getName())
                .status(savedImage.getStatus())
                .id(savedImage.getId()).build();
    }

    private ImageUploadRequest mapToJson(String image) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(image, ImageUploadRequest.class);
    }

}
