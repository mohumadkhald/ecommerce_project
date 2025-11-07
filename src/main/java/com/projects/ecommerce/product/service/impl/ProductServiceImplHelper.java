package com.projects.ecommerce.product.service.impl;

import com.projects.ecommerce.product.domain.*;
import com.projects.ecommerce.product.repository.ProductRepository;
import com.projects.ecommerce.product.service.CategoryService;
import com.projects.ecommerce.product.service.SubCategoryService;
import com.projects.ecommerce.user.expetion.NotFoundException;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/*
 * Helper: ProductServiceImplHelper
 * - This small helper re-uses the Specification based filtering logic. It's packaged here so the query implementation can call it
 * - Move it to its own file in your project (e.g. ProductQueryHelper.java) for cleanliness.
 */
class ProductServiceImplHelper {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final SubCategoryService subCategoryService;

    public ProductServiceImplHelper(ProductRepository productRepository, CategoryService categoryService, SubCategoryService subCategoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.subCategoryService = subCategoryService;
    }

    public Page<Product> getFilteredProducts(String categoryName, String email, String productName, List<String> colors, Double minPrice, Double maxPrice, List<String> sizes, Boolean available, int page, int pageSize, Sort sort) {
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        boolean opSub = subCategoryService.findByName(categoryName);
        if (!opSub) throw new NotFoundException("Category", "Category " + categoryName + " Not found");
        Specification<Product> spec = buildSpecForFilters(categoryName, email, productName, colors, minPrice, maxPrice, sizes, available);
        return productRepository.findAll(spec, pageable);
    }

    public Page<Product> getFilteredProductsByName(String categoryName, String email, String productName, List<String> colors, Double minPrice, Double maxPrice, List<String> sizes, Boolean available, int page, int pageSize, Sort sort) {
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Integer categoryId;
        if (!"all".equalsIgnoreCase(categoryName)) {
            Category opCat = categoryService.findByCategoryName(categoryName);
            if (opCat == null) throw new NotFoundException("Category", "Category " + categoryName + " Not found");
            categoryId = opCat.getCategoryId();
        } else {
            categoryId = null;
        }
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (categoryId != null)
                predicates.add(cb.equal(root.get("subCategory").get("category").get("categoryId"), categoryId));
            checkNull(email, productName, minPrice, maxPrice, available, root, cb, predicates);
            filterSizeAndColor(null, sizes, root, query, cb, predicates); // colors handled above if needed in callers
            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return productRepository.findAll(spec, pageable);
    }

    public Page<Product> getFilteredProductsAll(String categoryName, String email, String productName, List<String> colors, Double minPrice, Double maxPrice, List<String> sizes, Boolean available, int page, int pageSize, Sort sort) {
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Specification<Product> spec = buildSpecForFilters(categoryName, email, productName, colors, minPrice, maxPrice, sizes, available);
        return productRepository.findAll(spec, pageable);
    }

    private Specification<Product> buildSpecForFilters(String categoryName, String email, String productName, List<String> colors, Double minPrice, Double maxPrice, List<String> sizes, Boolean available) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (categoryName != null && !categoryName.isEmpty())
                predicates.add(cb.equal(root.get("subCategory").get("name"), categoryName));
            checkNull(email, productName, minPrice, maxPrice, available, root, cb, predicates);
            filterSizeAndColor(colors, sizes, root, query, cb, predicates);
            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void checkNull(String email, String productName, Double minPrice, Double maxPrice, Boolean available, Root<Product> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (email != null && !email.isEmpty()) predicates.add(cb.equal(root.get("createdBy"), email));
        if (productName != null && !productName.isEmpty())
            predicates.add(cb.like(root.get("productTitle"), "%" + productName + "%"));
        if (minPrice != null) predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        if (maxPrice != null) predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        if (available != null) {
            if (available) predicates.add(cb.greaterThan(root.get("allQuantity"), 0));
            else predicates.add(cb.lessThanOrEqualTo(root.get("allQuantity"), 0));
        }
    }

	static void filterSizeAndColor(
			List<String> colors,
			List<String> sizes,
			Root<Product> root,
			CriteriaQuery<?> query,
			CriteriaBuilder cb,
			List<Predicate> predicates
	) {

		// Convert input strings to enums safely
		List<Color> colorEnums = (colors != null && !colors.isEmpty())
				? colors.stream().map(Color::valueOf).toList()
				: List.of();

		List<Size> sizeEnums = (sizes != null && !sizes.isEmpty())
				? sizes.stream().map(Size::valueOf).toList()
				: List.of();


		// ✅ 1. Filter for colors
		if (!colorEnums.isEmpty()) {
			predicates.add(existsForField(query, root, cb,
					"color", colorEnums, colorEnums.size()));
		}

		// ✅ 2. Filter for sizes
		if (!sizeEnums.isEmpty()) {
			predicates.add(existsForField(query, root, cb,
					"size", sizeEnums, sizeEnums.size()));
		}

		// ✅ 3. Filter combinations (only when both lists provided)
		if (!colorEnums.isEmpty() && !sizeEnums.isEmpty()) {
			predicates.add(existsForColorSizeCombination(
					query, root, cb, colorEnums, sizeEnums
			));
		}
	}
	private static Predicate existsForField(
			CriteriaQuery<?> query,
			Root<Product> root,
			CriteriaBuilder cb,
			String fieldName,
			List<?> values,
			long expectedCount
	) {
		Subquery<Long> sub = query.subquery(Long.class);
		Root<ProductVariation> pv = sub.from(ProductVariation.class);

		sub.select(pv.get("product").get("id"))
				.where(cb.and(
						pv.get(fieldName).in(values),
						cb.equal(pv.get("product").get("id"), root.get("id")),
						cb.greaterThan(pv.get("quantity"), 0)
				))
				.groupBy(pv.get("product").get("id"))
				.having(cb.equal(cb.countDistinct(pv.get(fieldName)), expectedCount));

		return cb.exists(sub);
	}

	private static Predicate existsForColorSizeCombination(
			CriteriaQuery<?> query,
			Root<Product> root,
			CriteriaBuilder cb,
			List<Color> colors,
			List<Size> sizes
	) {
		Subquery<Long> sub = query.subquery(Long.class);
		Root<ProductVariation> pv = sub.from(ProductVariation.class);

		sub.select(pv.get("product").get("id"))
				.where(cb.and(
						cb.equal(pv.get("product").get("id"), root.get("id")),
						pv.get("color").in(colors),
						pv.get("size").in(sizes),
						cb.greaterThan(pv.get("quantity"), 0)
				))
				.groupBy(pv.get("product").get("id"))
				.having(cb.equal(
						cb.countDistinct(cb.concat(
								pv.get("color").as(String.class),
								pv.get("size").as(String.class)
						)),
						(long) colors.size() * sizes.size()
				));

		return cb.exists(sub);
	}

}
