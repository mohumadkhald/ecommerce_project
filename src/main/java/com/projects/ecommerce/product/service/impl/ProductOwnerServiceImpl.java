package com.projects.ecommerce.product.service.impl;

import com.projects.ecommerce.product.domain.Product;
import com.projects.ecommerce.product.exception.wrapper.ProductNotFoundException;
import com.projects.ecommerce.product.repository.ProductRepository;
import com.projects.ecommerce.user.expetion.NotFoundException;
import com.projects.ecommerce.user.model.Role;
import com.projects.ecommerce.user.repository.UserRepo;
import com.projects.ecommerce.product.service.ProductOwnerService;
import com.projects.ecommerce.utilts.traits.ApiTrait;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductOwnerServiceImpl implements ProductOwnerService {
    private final ProductRepository productRepository;
    private final UserRepo userRepository;

    @Override
    public ResponseEntity<?> removeProductByCreatedBy(String email, Integer productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        boolean isAdmin = userRepository.findAllByRole(Role.ADMIN).stream().anyMatch(user -> user.getEmail().equals(email));
        return getResponseEntity(email, productId, product, isAdmin, productRepository);
    }

    static ResponseEntity<?> getResponseEntity(String email, Integer productId, Product product, boolean isAdmin, ProductRepository productRepository) {
        if (isAdmin || product.getCreatedBy().equals(email)) {
            String productTitleFormatted = product.getProductTitle().replaceAll(" ", "_");
            String productFolderPath = "uploads/products/" + productTitleFormatted;
            try {
                Path productFolder = Paths.get(productFolderPath);
                if (Files.exists(productFolder)) {
                    Files.walk(productFolder).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete product images folder: " + e.getMessage());
            }
            productRepository.deleteById(productId);
            return ApiTrait.successMessage("Product Deleted", HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Override
    public ResponseEntity<?> removeProductsByCreatedBy(String email, List<Integer> productIds) {
        List<Product> products = productRepository.findAllById(productIds);
        if (products.isEmpty())
            throw new ProductNotFoundException("Products not found with the provided ids: " + productIds);
        boolean isAdmin = userRepository.findAllByRole(Role.ADMIN).stream().anyMatch(user -> user.getEmail().equals(email));
        List<Product> productsToDelete = products.stream().filter(p -> isAdmin || p.getCreatedBy().equals(email)).collect(Collectors.toList());
        if (productsToDelete.isEmpty()) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return getResponseEntity(productsToDelete, productRepository);
    }

    static ResponseEntity<?> getResponseEntity(List<Product> productsToDelete, ProductRepository productRepository) {
        for (Product product : productsToDelete) {
            String productTitleFormatted = product.getProductTitle().replaceAll(" ", "_");
            String productFolderPath = "uploads/products/" + productTitleFormatted;
            try {
                Path productFolder = Paths.get(productFolderPath);
                if (Files.exists(productFolder))
                    Files.walk(productFolder).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete product images folder for product ID " + product.getId() + ": " + e.getMessage());
            }
        }
        productRepository.deleteAll(productsToDelete);
        return ApiTrait.successMessage("Products Deleted", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> setDiscount(String email, Integer productId, Double discount) throws AccessDeniedException {
        Map<String, String> response = new HashMap<>();
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("product", "Product not found with id: " + productId));
        boolean isAdmin = userRepository.findAllByRole(Role.ADMIN).stream().anyMatch(user -> user.getEmail().equals(email));
        if (isAdmin || product.getCreatedBy().equals(email)) {
            product.setDiscountPercent(discount);
            Double discountedPrice = product.getPrice() - (product.getPrice() * discount / 100);
            product.setDiscountedPrice(discountedPrice);
            productRepository.save(product);
            response.put("message", "The Discount has been set");
            return ResponseEntity.ok(response);
        } else {
            throw new AccessDeniedException("You do not have permission to access this product.");
        }
    }

    @Override
    public ResponseEntity<?> setDiscounts(String email, List<Integer> productIds, Double discount) throws AccessDeniedException {
        Map<String, String> response = new HashMap<>();
        boolean isAdmin = userRepository.findAllByRole(Role.ADMIN).stream().anyMatch(user -> user.getEmail().equals(email));
        for (Integer productId : productIds) {
            Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
            if (isAdmin || product.getCreatedBy().equals(email)) {
                product.setDiscountPercent(discount);
                Double discountedPrice = product.getPrice() - (product.getPrice() * discount / 100);
                product.setDiscountedPrice(discountedPrice);
                productRepository.save(product);
            } else {
                throw new AccessDeniedException("You do not have permission to access product with id: " + productId);
            }
        }
        response.put("message", "The Discount has been set for the specified products");
        return ResponseEntity.ok(response);
    }
}
