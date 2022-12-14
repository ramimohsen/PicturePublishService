package com.ims.picturepublishservice.dto;


import com.ims.picturepublishservice.enums.Category;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ImageUploadRequest {
    @NotNull
    private Category category;
    @NotNull
    private String description;
}
