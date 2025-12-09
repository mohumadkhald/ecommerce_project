package com.projects.ecommerce.product.resource;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projects.ecommerce.product.dto.*;
import com.projects.ecommerce.product.service.ProductCommandService;
import com.projects.ecommerce.product.service.ProductOwnerService;
import com.projects.ecommerce.product.service.ProductQueryService;
import com.projects.ecommerce.product.service.ProductVariationService;
import com.projects.ecommerce.user.service.UserService;
import com.projects.ecommerce.utilts.file.FileUploadStrategy;
import com.projects.ecommerce.utilts.traits.ApiTrait;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@Slf4j
@RequiredArgsConstructor
public class ProductResource {

    // ✅ New Service Structure
    private final ProductQueryService productQueryService;
    private final ProductCommandService productCommandService;
    private final ProductOwnerService productOwnerService;
    private final ProductVariationService productVariationService;

    private final UserService userService;
//    private final FileStorageService fileStorageService;
    private final FileUploadStrategy fileStorageService;

    private final Validator validator;

    // ✅ Fetch All Products With Filters
    @GetMapping
    public ResponseEntity<Page<AllDetailsProductDto>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) List<String> size,
            @RequestParam(required = false) List<String> color,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String subCategory,
            @RequestParam(required = false) String productTitle) {

        List<String> uppercaseSizes = size != null ? size.stream().map(String::toUpperCase).toList() : null;
        List<String> colors = color != null ? color.stream().map(String::toLowerCase).toList() : null;

        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC, sortBy);

        Pageable pageable = PageRequest.of(page, pageSize, sort);

        Page<AllDetailsProductDto> productPage =
                productQueryService.findAll(pageable, minPrice, maxPrice, colors, uppercaseSizes,
                        available, email, subCategory, productTitle);

        return ResponseEntity.ok(productPage);
    }

    // ✅ Category + Filters
    @GetMapping("/product-category/{subCategoryName}")
    public ResponseEntity<Page<ProductDto>> getProductsByCategoryNameAndFilters(
            @PathVariable String subCategoryName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) List<String> color,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) List<String> size,
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        List<String> uppercaseSizes = size != null ? size.stream().map(String::toUpperCase).toList() : null;
        List<String> colors = color != null ? color.stream().map(String::toLowerCase).toList() : null;

        Sort sort = Sort.by(sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);

        return ResponseEntity.ok(
                productQueryService.getProductsByCategoryNameAndFilters(
                        subCategoryName, email, colors, minPrice, maxPrice,
                        uppercaseSizes, available, page, pageSize, sort)
        );
    }

    // ✅ Category + Product Name + Filters
    @GetMapping("/{category}/{productName}")
    public ResponseEntity<Page<ProductDto>> getProductsByCategoryNameAndProductNameAndFilters(
            @PathVariable String category,
            @PathVariable String productName,
            @RequestParam(required = false) List<String> color,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) List<String> size,
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        List<String> uppercaseSizes = size != null ? size.stream().map(String::toUpperCase).toList() : null;
        List<String> colors = color != null ? color.stream().map(String::toLowerCase).toList() : null;

        Sort sort = Sort.by(sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);

        return ResponseEntity.ok(
                productQueryService.getProductsByCategoryNameAndProdcutNameAndFilters(
                        category, productName, colors, minPrice, maxPrice,
                        uppercaseSizes, available, page, pageSize, sort)
        );
    }

    // ✅ Get Product By ID
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> findById(
            @PathVariable("productId")
            @NotBlank(message = "Input must not be blank!")
            @Valid String productId) {

        return ResponseEntity.ok(
                productQueryService.findById(Integer.parseInt(productId))
        );
    }

    // ✅ Create Product
    @PostMapping
    public ResponseEntity<Map<String, String>> save(
            @ModelAttribute @Valid ProductRequestDto productDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestHeader("Authorization") String jwtToken
    ) throws IOException {

        Integer userId = userService.findUserIdByJwt(jwtToken);
        String email = userService.findById(userId).getEmail();
        productDto.setEmail(email);

// Validate image count 5–10
        if (images == null ) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "You must upload between 5 and 10 images"));
        }

// Save image URLs
        List<String> storedImageUrls = new ArrayList<>();
        for (MultipartFile img : images) {
            String url = fileStorageService.storeFile(
                    img, "products/" + productDto.getProductTitle()
            );
            storedImageUrls.add(url);
        }

// Save URLs into DTO only
        productDto.setImageUrls(storedImageUrls);

        productCommandService.create(productDto);

        return ResponseEntity.ok(Map.of("message", "Product created successfully"));
    }


    // ✅ Batch Create
    @PostMapping("/batch")
    public ResponseEntity<Map<String, String>> saveBatch(
            @RequestBody @NotNull @Valid List<ProductRequestDto> productDtos,
            @RequestHeader("Authorization") String jwtToken
    ) {
        Integer userId = userService.findUserIdByJwt(jwtToken);
        String email = userService.findById(userId).getEmail();

        productCommandService.saveAll(productDtos, email);

        return ResponseEntity.ok(Map.of("message", "Products created successfully"));
    }

    // ✅ Update Product
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> update(
            @PathVariable("productId") String productId,
            @ModelAttribute @Valid ProductEditDto productDto,
//            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestHeader("Authorization") String jwtToken
    ) throws IOException {

        Integer userId = userService.findUserIdByJwt(jwtToken);
        productDto.setEmail(userService.findById(userId).getEmail());
        return ResponseEntity.ok(
                productCommandService.update(Integer.parseInt(productId), productDto)
        );
    }

    // ✅ Get Variation Stock
    @GetMapping("/stock")
    public ResponseEntity<Map<String, Map<String, Map<String, Integer>>>> getProductVariations(
            @RequestParam String productName) {

        return ResponseEntity.ok(
                productVariationService.getProductVariations(productName)
        );
    }

    // ✅ Update Variations + Images
    @PutMapping("/{productId}/stock")
    public ResponseEntity<Map<String, String>> updateProductVariations(
            @PathVariable Integer productId,
            @RequestParam("specs") String specsJson
    ) throws IOException {

        List<Spec> specs = parseSpecs(specsJson);

        Map<String, String> errors = validateSpecs(specs);
        if (!errors.isEmpty()) return ResponseEntity.badRequest().body(errors);



        productVariationService.updateProductVariation(productId, specs);

        return ResponseEntity.ok(Map.of("message", "Product variations updated successfully"));
    }

    private Map<String, String> validateSpecs(List<Spec> specs) {
        Map<String, String> errors = new HashMap<>();
        for (int i = 0; i < specs.size(); i++) {
            for (ConstraintViolation<Spec> v : validator.validate(specs.get(i))) {
                errors.put("spec[" + i + "]." + v.getPropertyPath(), v.getMessage());
            }
        }
        return errors;
    }

    private List<Spec> parseSpecs(String specsJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, Spec.class);
            return mapper.readValue(specsJson, type);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse specs JSON", e);
        }
    }

    // ✅ Increase/Decrease Stock
    @PostMapping("/{productId}/stock")
    public ResponseEntity<String> updateProductVariations(
            @PathVariable Integer productId,
            @RequestParam(required = false) boolean increase,
            @Valid @RequestBody List<@Valid Spec> specs) {

        productCommandService.updateProductStocks(productId, specs, increase);

        return ResponseEntity.ok("Product variations updated successfully.");
    }

    // ✅ Increase or Decrease Stock (Plural)
    @PostMapping("/{productId}/stocks")
    public ResponseEntity<String> updateProductStock(
            @PathVariable Integer productId,
            @RequestParam(required = false) boolean decrease,
            @RequestBody List<Spec> specs) {

        productCommandService.updateProductStocks(productId, specs, !decrease);

        return ResponseEntity.ok(decrease ?
                "Product variations decreased successfully." :
                "Product variations increased successfully.");
    }

    // ✅ Products Created By User
    @GetMapping("/created-by")
    public ResponseEntity<List<ProductDto>> getProductsCreatedByUser(@RequestParam("email") String email) {
        return ResponseEntity.ok(productQueryService.findAllByCreatedBy(email));
    }

    @GetMapping("/find/created-by")
    public ResponseEntity<List<ProductDto>> getAllProductsCreatedByUser(
            @RequestHeader("Authorization") String jwtToken) {

        Integer userId = userService.findUserIdByJwt(jwtToken);
        return ResponseEntity.ok(
                productQueryService.findAllByCreatedBy(userService.findById(userId).getEmail())
        );
    }

    // ✅ Delete Product
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeProductFromCart(
            @PathVariable Integer productId,
            @RequestHeader("Authorization") String jwtToken) {

        Integer userId = userService.findUserIdByJwt(jwtToken);
        String email = userService.findById(userId).getEmail();

        return productOwnerService.removeProductByCreatedBy(email, productId);
    }

    // ✅ Bulk Delete
    @DeleteMapping("/bulk-delete")
    public ResponseEntity<?> removeProductsFromCart(
            @RequestParam List<Integer> productIds,
            @RequestHeader("Authorization") String jwtToken) {

        Integer userId = userService.findUserIdByJwt(jwtToken);
        String email = userService.findById(userId).getEmail();

        return productOwnerService.removeProductsByCreatedBy(email, productIds);
    }

    // ✅ Product Full Details
    @GetMapping("/allDetails/{productId}")
    public ResponseEntity<AllDetailsProductDto> findByProductId(
            @RequestHeader("Authorization") String jwtToken,
            @PathVariable("productId") @NotBlank @Valid String productId) throws AccessDeniedException {

        Integer userId = userService.findUserIdByJwt(jwtToken);
        String email = userService.findById(userId).getEmail();

        return ResponseEntity.ok(
                productQueryService.findByProductId(email, Integer.parseInt(productId))
        );
    }

    // ✅ Set Single Discount
    @PatchMapping("setDiscount/{productId}")
    public ResponseEntity<?> setDiscount(
            @RequestHeader("Authorization") String jwtToken,
            @PathVariable Integer productId,
            @RequestParam Double discount) throws AccessDeniedException {

        Integer userId = userService.findUserIdByJwt(jwtToken);
        String email = userService.findByUserId(userId).getEmail();

        return productOwnerService.setDiscount(email, productId, discount);
    }

    // ✅ Set Multiple Discounts
    @PatchMapping("setDiscount")
    public ResponseEntity<?> setDiscounts(
            @RequestHeader("Authorization") String jwtToken,
            @RequestParam List<Integer> productIds,
            @RequestParam Double discount) throws AccessDeniedException {

        Integer userId = userService.findUserIdByJwt(jwtToken);
        String email = userService.findByUserId(userId).getEmail();

        return productOwnerService.setDiscounts(email, productIds, discount);
    }

    // ✅ Get All Email Sellers
    @GetMapping("/{subId}/emails")
    public List<String> getAllEmailSellers(@PathVariable Integer subId) {
        return productQueryService.getAllEmailSellers(subId);
    }

    // ✅ Suggestions
    @GetMapping("/{subId}/suggestion")
    public List<ProductDto> getSuggestionProductsBySubCategory(@PathVariable Integer subId) {
        return productQueryService.getSuggestionProductsBySubCategory(subId);
    }

    @PatchMapping("photo")
    public ResponseEntity<?> changePhoto(
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestParam("url" ) String url,
            @RequestParam("title")  String name,
            @RequestHeader("Authorization") String jwtToken)
            throws IOException {

        log.info("*** ProductDto, resource; save product ***");

        // Check if the image is null or empty and add a global error
        if (image == null || image.isEmpty()) {
            throw new IllegalStateException("Image file is required");
        }


        String imageUrl = fileStorageService.storeFile(image, "products/" + name);
        productCommandService.updateProductPhoto(name, url, imageUrl);
        log.info("the url is  {}", url);
//        fileStorageService.removeFile(url);

        return ApiTrait.successMessage(imageUrl, HttpStatus.OK);
    }
}