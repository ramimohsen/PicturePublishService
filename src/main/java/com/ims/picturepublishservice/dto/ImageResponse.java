package com.ims.picturepublishservice.dto;


import com.ims.picturepublishservice.enums.Category;
import com.ims.picturepublishservice.enums.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageResponse {

    private String name;
    private int id;
    private String url;
    private String type;
    private Status status;
    private Category category;
    private String description;

}
