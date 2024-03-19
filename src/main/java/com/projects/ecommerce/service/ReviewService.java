package com.projects.ecommerce.service;

import com.projects.ecommerce.exception.ProductException;
import com.projects.ecommerce.model.Review;
import com.projects.ecommerce.model.User;
import com.projects.ecommerce.requests.ReviewRequest.ReviewRequest;

import java.util.List;

public interface ReviewService {
    public Review createReview(ReviewRequest request, User user) throws ProductException;

    public List<Review> getAllReviews(Long productId);
}
