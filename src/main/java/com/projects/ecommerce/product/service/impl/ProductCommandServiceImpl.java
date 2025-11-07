package com.projects.ecommerce.product.service.impl;

import com.projects.ecommerce.product.domain.*;
import com.projects.ecommerce.product.dto.ProductDto;
import com.projects.ecommerce.product.dto.ProductEditDto;
import com.projects.ecommerce.product.dto.ProductRequestDto;
import com.projects.ecommerce.product.dto.Spec;
import com.projects.ecommerce.product.exception.wrapper.ProductNotFoundException;
import com.projects.ecommerce.product.helper.ProductMappingHelper;
import com.projects.ecommerce.product.repository.ProductRepository;
import com.projects.ecommerce.product.service.CategoryService;
import com.projects.ecommerce.product.service.SubCategoryService;
import com.projects.ecommerce.user.expetion.AlreadyExistsException;
import com.projects.ecommerce.user.expetion.NotFoundException;
import com.projects.ecommerce.user.repository.UserRepo;
import com.projects.ecommerce.product.service.ProductCommandService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductCommandServiceImpl implements ProductCommandService {
    private final ProductRepository productRepository;
    private final UserRepo userRepository;
    private final CategoryService categoryService;
    private final SubCategoryService subCategoryService;

    @Override
    public ProductDto create(ProductRequestDto productDto) {
        if (productDto.getDiscountPercent() != null) productDto.setDiscountedPrice();
        List<Product> existingProducts = productRepository.findAll();
        Product mappedProduct = ProductMappingHelper.map(productDto, existingProducts);
        return ProductMappingHelper.map(productRepository.save(mappedProduct));
    }

    @Override
    public List<ProductDto> saveAll(List<ProductRequestDto> productDtos, String email) {
        List<Product> existingProducts = productRepository.findAll();
        Map<String, Product> productMap = new HashMap<>();
        for (Product p : existingProducts) productMap.put(p.getProductTitle(), p);

        for (ProductRequestDto productDto : productDtos) {
            if (productDto.getDiscountPercent() != null) productDto.setDiscountedPrice();
            String productName = productDto.getProductTitle();
            Product existingProduct = productMap.get(productName);
            if (existingProduct != null && existingProduct.getSubCategory().getSubId().equals(productDto.getSubCategoryId())) {
                if (existingProduct.getCreatedBy() != null && existingProduct.getCreatedBy().equals(email)) {
                    ProductCommandServiceImpl.getExistingVariation(existingProduct, productDto);
                }
            } else {
                Product mappedProduct = ProductMappingHelper.map(productDto, existingProducts);
                productMap.put(productName, mappedProduct);
            }
        }

        List<ProductDto> saved = new ArrayList<>();
        for (Product p : productMap.values()) {
            Product savedEntity = productRepository.save(p);
            saved.add(ProductMappingHelper.map(savedEntity));
        }
        return saved;
    }

    @Override
    public ProductDto update(ProductDto productDto) {
        return ProductMappingHelper.map(productRepository.save(ProductMappingHelper.map(productDto)));
    }

    @Override
    public ProductDto update(Integer productId, @Valid ProductEditDto productDto) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product", "not found with id: " + productId));
        if (!product.getProductTitle().equals(productDto.getProductTitle())) {
            Product existing = productRepository.findByProductTitle(productDto.getProductTitle());
            if (existing != null && !existing.getId().equals(productId))
                throw new AlreadyExistsException("Product", "Already Exists: " + productDto.getProductTitle());
        }
        product.setProductTitle(productDto.getProductTitle());
        product.setPrice(productDto.getPrice());
        product.setImageUrl(productDto.getImageUrl());
        product.setDiscountPercent(productDto.getDiscountPercent());
        product.setSubCategory(SubCategory.builder().subId(productDto.getSubCategoryId()).category(Category.builder().build()).build());
        productRepository.save(product);
        return ProductMappingHelper.map(product);
    }

    @Override
    public void deleteById(Integer productId) {
        ProductDto dto = ProductMappingHelper.map(this.productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product", "not found")));
        this.productRepository.delete(ProductMappingHelper.map(dto));
    }

    @Override
    public void updateProductStocks(Integer productId, List<Spec> specs, boolean increaseQuantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        for (Spec spec : specs) {
            Optional<ProductVariation> existingVariation = product.getVariations().stream()
                    .filter(v -> v.getSize().equals(Size.valueOf(spec.getSize())) && v.getColor().equals(Color.valueOf(spec.getColor())))
                    .findFirst();
            if (existingVariation.isPresent()) {
                ProductVariation variationToUpdate = existingVariation.get();
                int currentQuantity = variationToUpdate.getQuantity();
                variationToUpdate.setQuantity(increaseQuantity ? currentQuantity + spec.getQuantity() : spec.getQuantity());
            } else {
                newProductVariation(product, spec, spec.getQuantity(), null);
            }
        }
        productRepository.save(product);
        int totalQuantity = product.getVariations().stream().mapToInt(ProductVariation::getQuantity).sum();
        product.setAllQuantity(totalQuantity);
    }

    @Override
    public Product findProductById(Integer productId) {
        return productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
    }

    @Override
    public void updateProductStock(Integer productId, List<Spec> specs, Integer quantityToSubtract) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        for (Spec spec : specs) {
            Optional<ProductVariation> existingVariation = product.getVariations().stream()
                    .filter(v -> v.getSize().equals(Size.valueOf(spec.getSize())) && v.getColor().equals(Color.valueOf(spec.getColor())))
                    .findFirst();
            if (existingVariation.isPresent()) {
                ProductVariation variationToUpdate = existingVariation.get();
                int currentQuantity = variationToUpdate.getQuantity();
                int newQuantity = currentQuantity - quantityToSubtract;
                variationToUpdate.setQuantity(Math.max(newQuantity, 0));
            } else {
                newProductVariation(product, spec, quantityToSubtract, null);
            }
        }
        productRepository.save(product);
        int totalQuantity = product.getVariations().stream().mapToInt(ProductVariation::getQuantity).sum();
        product.setAllQuantity(totalQuantity);
    }

    static void newProductVariation(Product product, Spec spec, Integer increaseQuantity, String img) {
        ProductVariation newVariation = new ProductVariation();
        newVariation.setSize(Size.valueOf(spec.getSize()));
        newVariation.setColor(Color.valueOf(spec.getColor()));
        newVariation.setQuantity(spec.getQuantity() + increaseQuantity);
        newVariation.setImg(img);
        newVariation.setProduct(product);
        product.getVariations().add(newVariation);
    }

    public static void getExistingVariation(Product product, ProductRequestDto productDto) {
        Optional<ProductVariation> existingVariation = product.getVariations().stream()
                .filter(v -> v.getSize().equals(productDto.getSize()) && v.getColor().equals(productDto.getColor()))
                .findFirst();
        if (existingVariation.isPresent()) {
            ProductVariation variation = existingVariation.get();
            variation.setQuantity(variation.getQuantity() + productDto.getQuantity());
        } else {
            ProductVariation newVariation = ProductVariation.builder()
                    .color(productDto.getColor())
                    .size(productDto.getSize())
                    .quantity(productDto.getQuantity())
                    .product(product)
                    .build();
            product.getVariations().add(newVariation);
        }
        int totalQuantity = product.getVariations().stream().mapToInt(ProductVariation::getQuantity).sum();
        product.setAllQuantity(totalQuantity);
    }
}
