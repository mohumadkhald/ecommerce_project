package com.projects.ecommerce.admin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
@EnableMethodSecurity
public class AdminController {
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin/test")
    @JsonIgnore
    public ResponseEntity<String> sayHey(){
        return ResponseEntity.ok("Hey admin");
    }

}
