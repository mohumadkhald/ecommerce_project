package com.projects.ecommerce.service;

import com.projects.ecommerce.exception.ProductException;
import com.projects.ecommerce.model.Product;
import com.projects.ecommerce.model.Rating;
import com.projects.ecommerce.model.User;
import com.projects.ecommerce.repo.RatingRepo;
import com.projects.ecommerce.requests.RatingRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class RatingServiceImplement implements RatingService{
    private RatingRepo ratingRepo;
    private ProductService productService;
    @Override
    public Rating createRate(RatingRequest request, User user) throws ProductException {
        Product product = productService.findProductById(request.getProductId());
        Rating rating = new Rating();
        rating.setUser(user);
        rating.setProduct(product);
        rating.setRating(request.getRating());
        rating.setCreatedAt(LocalDateTime.now());
        ratingRepo.save(rating);
        return rating;
    }

    @Override
    public List<Rating> getProductsRating(Long productId) {
        return ratingRepo.getAllProductsRating(productId);
    }
}
