package com.projects.ecommerce.product.service;

import org.springframework.http.ResponseEntity;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface ProductOwnerService {
    ResponseEntity<?> removeProductByCreatedBy(String email, Integer productId);
    ResponseEntity<?> removeProductsByCreatedBy(String email, List<Integer> productIds);
    ResponseEntity<?> setDiscount(String email, Integer productId, Double discount) throws AccessDeniedException, AccessDeniedException;
    ResponseEntity<?> setDiscounts(String email, List<Integer> productIds, Double discount) throws AccessDeniedException;
}
