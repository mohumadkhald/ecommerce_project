package com.projects.ecommerce.utilts;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@MappedSuperclass
public class Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(
            updatable = false,
            nullable = false
    )
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(
            insertable = false
    )
    @UpdateTimestamp
    private LocalDateTime updatedAt;


    private String createdBy;

    @PrePersist
    public void prePersist() {
        if (this.createdBy == null) {
            // Get the currently authenticated user
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username;
            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }
            this.createdBy = username;
        }
    }


    private String updatedBy;
}
//    @PreUpdate
//    public void preUpdate() {
//        // Get the currently authenticated user
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String username;
//        if (principal instanceof UserDetails) {
//            username = ((UserDetails) principal).getUsername();
//        } else {
//            username = principal.toString();
//        }
//        this.updatedBy = username;
//    }








//@Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)  //(strategy = GenerationType.SEQUENCE, generator = "author_id_gen")
////    @SequenceGenerator(name = "author_sequence", sequenceName = "author_sequence", allocationSize = 1)
////    @TableGenerator(name = "author_id_gen", table = "id_generator", pkColumnName = "id_name", valueColumnName ="id_value", allocationSize = 1)
//    private Integer id;