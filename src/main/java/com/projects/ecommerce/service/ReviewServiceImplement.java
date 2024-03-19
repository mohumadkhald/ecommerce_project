package com.projects.ecommerce.service;

import com.projects.ecommerce.exception.ProductException;
import com.projects.ecommerce.model.Product;
import com.projects.ecommerce.model.Review;
import com.projects.ecommerce.model.User;
import com.projects.ecommerce.repo.ReviewRepo;
import com.projects.ecommerce.requests.ReviewRequest.ReviewRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class ReviewServiceImplement implements ReviewService{
    private ReviewRepo reviewRepo;
    private ProductService productService;
    @Override
    public Review createReview(ReviewRequest request, User user) throws ProductException {
        Product product = productService.findProductById(request.getProductId());

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setReview(request.getReview());
        review.setCreatedAt(LocalDateTime.now());

        return reviewRepo.save(review);

    }

    @Override
    public List<Review> getAllReviews(Long productId) {

        return reviewRepo.getAllProductsReview(productId);
    }
}
