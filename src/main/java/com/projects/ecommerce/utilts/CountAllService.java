package com.projects.ecommerce.utilts;

import com.projects.ecommerce.order.repostiory.OrderRepository;
import com.projects.ecommerce.product.repository.CategoryRepository;
import com.projects.ecommerce.product.repository.ProductRepository;
import com.projects.ecommerce.product.repository.SubCategoryRepository;
import com.projects.ecommerce.user.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CountAllService {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ProductRepository productRepository;
    private final UserRepo userRepository;
    private final OrderRepository orderRepository;

    public CountAllService(CategoryRepository categoryRepository,
                           SubCategoryRepository subCategoryRepository,
                           ProductRepository productRepository,
                           UserRepo userRepository, OrderRepository orderRepository) {
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    public Map<String, Long> getCounts() {
        Map<String, Long> counts = new HashMap<>();
        counts.put("categories", categoryRepository.count());
        counts.put("subCategories", subCategoryRepository.count());
        counts.put("products", productRepository.count());
        counts.put("users", userRepository.count());
        counts.put("orders", orderRepository.count());
        return counts;
    }
}