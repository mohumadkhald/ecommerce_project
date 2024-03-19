package com.projects.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Product {

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

    @Column(name = "size")
    @Enumerated(EnumType.STRING)
    private Size size;

    private String imageUrl;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    private int numRating;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private LocalDateTime createdAt;

    public Product(String title, String description, int price, int discountPercent, int quantity, String brand, String color, Set<Size> sizes, String imageUrl, int numRating, Category category, LocalDateTime createdAt) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.discountPercent = discountPercent;
        this.quantity = quantity;
        this.brand = brand;
        this.color = color;
        this.imageUrl = imageUrl;
        this.numRating = numRating;
        this.category = category;
        this.createdAt = createdAt;

        // Calculate discounted price
        this.discountedPrice = calculateDiscountedPrice(price, discountPercent);
    }

    public Product(String title, String description, int price, int discountedPrice, int discountPercent, int quantity, String brand, String color, Set<Size> sizes, String imageUrl, int i, Category thirdCategory, LocalDateTime now) {
    }

    // Method to calculate discounted price
    private int calculateDiscountedPrice(int price, int discountPercent) {
        BigDecimal discountDecimal = BigDecimal.valueOf(discountPercent).divide(BigDecimal.valueOf(100));
        BigDecimal discountedAmount = BigDecimal.valueOf(price).multiply(discountDecimal);
        return price - discountedAmount.intValue();
    }

    public void setUpdatedAt(LocalDateTime now) {
    }
}
