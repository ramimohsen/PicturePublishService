package com.ims.picturepublishservice.repository;

import com.ims.picturepublishservice.entity.Role;
import com.ims.picturepublishservice.enums.SystemRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(SystemRole name);
}
