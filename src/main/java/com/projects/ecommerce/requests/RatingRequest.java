package com.projects.ecommerce.requests;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RatingRequest {
    private Long productId;
    private double rating;
}
