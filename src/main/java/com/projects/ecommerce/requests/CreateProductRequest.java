package com.projects.ecommerce.requests;

import com.projects.ecommerce.model.Category;
import com.projects.ecommerce.model.Rating;
import com.projects.ecommerce.model.Review;
import com.projects.ecommerce.model.Size;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private int price;

    private int discountedPrice;

    private int discountPercent;

    private int quantity;

    private String brand;

    private String color;

    @ManyToOne
    @JoinColumn(name = "size_id")
    private Size size;

    private String imageUrl;

    private int categoryId;
    // Constructors, getters, and setters...
}
