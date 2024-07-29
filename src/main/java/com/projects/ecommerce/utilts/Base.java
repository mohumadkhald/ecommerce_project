package com.projects.ecommerce.utilts;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;

@MappedSuperclass
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(BaseEntityListener.class)
public class Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(updatable = false, nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(insertable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;

    @PrePersist
    public void prePersist() {
        if (this.createdBy == null) {
            this.createdBy = getAuthenticatedUsername();
        }
    }

    @PreUpdate
    public void preUpdate() {
        String username = getAuthenticatedUsername();
        if (username != null) {
            this.updatedBy = username;
        } else {
            this.updatedBy = this.createdBy; // or some default value
        }
        this.updatedAt = LocalDateTime.now();
    }

    public static String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        // Check if the authentication is of type OAuth2Authentication
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2User oauth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();
            // Extract email or name from the OAuth2User
            return oauth2User.getAttribute("email");
        }

        return authentication.getName();
    }

}


//@Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)  //(strategy = GenerationType.SEQUENCE, generator = "author_id_gen")
////    @SequenceGenerator(name = "author_sequence", sequenceName = "author_sequence", allocationSize = 1)
////    @TableGenerator(name = "author_id_gen", table = "id_generator", pkColumnName = "id_name", valueColumnName ="id_value", allocationSize = 1)
//    private Integer id;