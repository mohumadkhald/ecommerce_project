package com.projects.ecommerce.user.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "address")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id", unique = true, nullable = false, updatable = false)
    private Integer addressId;

    @Column(name = "full_address")
    private String fullAddress;

    @Column(name = "postal_code")
    private String postalCode;

    private String city;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
