package com.projects.ecommerce.service;

import com.projects.ecommerce.exception.ProductException;
import com.projects.ecommerce.model.Rating;
import com.projects.ecommerce.model.User;
import com.projects.ecommerce.requests.RatingRequest;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface RatingService {
    public Rating createRate(RatingRequest request, User user) throws ProductException;
    public List<Rating> getProductsRating(Long productId);
}
