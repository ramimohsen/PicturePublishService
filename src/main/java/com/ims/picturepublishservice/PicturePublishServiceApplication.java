package com.ims.picturepublishservice;


import com.ims.picturepublishservice.entity.Role;
import com.ims.picturepublishservice.entity.User;
import com.ims.picturepublishservice.enums.SystemRole;
import com.ims.picturepublishservice.service.RoleService;
import com.ims.picturepublishservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@SpringBootApplication
public class PicturePublishServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(PicturePublishServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(PicturePublishServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(RoleService roleService, UserService userService, PasswordEncoder encoder) {
        return args -> {
            if (roleService.rolesDoesNotExist()) {
                List<Role> roles = new ArrayList<Role>() {{
                    add(Role.builder().name(SystemRole.ROLE_USER).build());
                    add(Role.builder().name(SystemRole.ROLE_ADMIN).build());
                }};
                List<Role> roleList = roleService.save(roles);
                logger.info("System roles loaded to database {}", roleList);
            }

            if (!userService.isUserNameExists("admin")) {
                Role userRole = roleService.getUserRole();
                userService.save(User.builder().email("admin@yahoo.com")
                        .username("admin")
                        .password(encoder.encode("admin123"))
                        .roles(new HashSet<Role>() {{
                            add(userRole);
                        }})
                        .build());
            }
        };
    }
}
