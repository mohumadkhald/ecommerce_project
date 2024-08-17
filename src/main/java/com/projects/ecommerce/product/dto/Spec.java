package com.projects.ecommerce.product.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@AllArgsConstructor
public class Spec {
    @NotBlank(message = "Color cannot be null or empty")
    private String color;
    @NotBlank(message = "Size cannot be null or empty")
    private String size;
    private String img; // Image URL or path
    @NotNull(message = "Quantity cannot be null")
    @Positive(message = "Quantity must be a positive integer")
    private Integer quantity;

    @JsonCreator
    public Spec(@JsonProperty("size") String size,
                @JsonProperty("color") String color,
                @JsonProperty("quantity") Integer quantity,
                @JsonProperty("img") String img) {
        this.size = size;
        this.color = color;
        this.quantity = quantity;
        this.img = img;
    }
}
