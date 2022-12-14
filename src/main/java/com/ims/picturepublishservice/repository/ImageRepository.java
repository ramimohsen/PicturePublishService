package com.ims.picturepublishservice.repository;

import com.ims.picturepublishservice.entity.Image;
import com.ims.picturepublishservice.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
    List<Image> findAllByStatus(Status status);
}
