package com.ims.picturepublishservice.service;


import com.ims.picturepublishservice.entity.Role;
import com.ims.picturepublishservice.enums.SystemRole;
import com.ims.picturepublishservice.exception.EntityNotFoundException;
import com.ims.picturepublishservice.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;


    public Role getUserRole() throws EntityNotFoundException {
        return this.roleRepository.findByName(SystemRole.ROLE_USER)
                .orElseThrow(() -> new EntityNotFoundException("Role is not found."));
    }

    public Role getAdminRole() throws EntityNotFoundException {
        return this.roleRepository.findByName(SystemRole.ROLE_ADMIN)
                .orElseThrow(() -> new EntityNotFoundException("Role is not found."));
    }

    public List<Role> save(List<Role> roles) {
        return this.roleRepository.saveAll(roles);
    }

    public boolean rolesDoesNotExist() {
        return this.roleRepository.findAll().isEmpty();
    }
}
