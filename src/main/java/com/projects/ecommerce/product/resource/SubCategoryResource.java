package com.projects.ecommerce.product.resource;


import com.projects.ecommerce.product.dto.CategoryDto;
import com.projects.ecommerce.product.dto.SubCategoryDto;
import com.projects.ecommerce.product.dto.response.collection.DtoCollectionResponse;
import com.projects.ecommerce.product.service.SubCategoryService;
import com.projects.ecommerce.utilts.file.FileUploadStrategy;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/sub-categories")
@Slf4j
@RequiredArgsConstructor
public class SubCategoryResource {

	private final SubCategoryService subCategoryService;
//	private final FileStorageService fileStorageService;
	private final FileUploadStrategy fileStorageService;


	@GetMapping
	public ResponseEntity<DtoCollectionResponse<SubCategoryDto>> findAll() {
		log.info("*** CategoryDto List, controller; fetch all categories *");
		return ResponseEntity.ok(new DtoCollectionResponse<>(this.subCategoryService.findAll()));
	}

	@GetMapping("/count")
	public ResponseEntity<Long> getSubCategoriesCount() {
		log.info("*** CategoryDto List, controller; fetch count *");
		return ResponseEntity.ok(subCategoryService.getSubCategoriesCount());
	}

	@GetMapping("/find/{categoryTitle}")
	public ResponseEntity<CategoryDto> findSubByCategoryTitle(@PathVariable ("categoryTitle") String categoryTitle) {
		log.info("*** CategoryDto List, controller; fetch all categories by category title *");
		return ResponseEntity.ok(this.subCategoryService.findAllByCategoryTitle(categoryTitle));
	}

	@GetMapping("/{subCategoryId}")
	public ResponseEntity<SubCategoryDto> findById(
			@PathVariable("subCategoryId")
			@NotBlank(message = "Input must not be blank")
			@Valid final Integer subCategoryId) {
		log.info("*** CategoryDto, resource; fetch category by id *");
		return ResponseEntity.ok(this.subCategoryService.findById(subCategoryId));
	}

	@PostMapping
	public ResponseEntity<SubCategoryDto> save(
			@ModelAttribute @Valid final SubCategoryDto subCategoryDto,
			@RequestPart(value = "image", required = false) MultipartFile image,
			BindingResult bindingResult
	) throws IOException {

		log.info("*** CategoryDto, resource; save category ***");

		// Check if the image is null or empty and add a global error
		if (image == null || image.isEmpty()) {
			throw new IllegalStateException("Image file is required");
		}

		// Check for other validation errors
		if (bindingResult.hasErrors()) {
			return ResponseEntity.badRequest().body(null);
		}

		if (image != null && !image.isEmpty()) {
			String imageUrl = fileStorageService.storeFile(image, "subcategories/" + subCategoryDto.getName());
			subCategoryDto.setImg(imageUrl);
		}

		return ResponseEntity.ok(this.subCategoryService.save(subCategoryDto));
	}



	@PutMapping
	public ResponseEntity<SubCategoryDto> update(
			@RequestBody
			@NotNull(message = "Input must not be NULL")
			@Valid final SubCategoryDto subCategoryDto) {
		log.info("*** CategoryDto, resource; update category *");
		return ResponseEntity.ok(this.subCategoryService.update(subCategoryDto));
	}

	@PutMapping("/{subCategoryId}")
	public ResponseEntity<SubCategoryDto> update(
			@PathVariable("subCategoryId")
			@NotBlank(message = "Input must not be blank")
			@Valid final String subCategoryId,
			@ModelAttribute @Valid final SubCategoryDto subCategoryDto,
			@RequestPart(value = "image", required = false) MultipartFile image,
			BindingResult bindingResult
	) throws IOException {

		log.info("*** CategoryDto, resource; put category ***");

		// Check if the image is null or empty and add a global error
		if (image == null || image.isEmpty()) {
			throw new IllegalStateException("Image file is required");
		}

		// Check for other validation errors
		if (bindingResult.hasErrors()) {
			return ResponseEntity.badRequest().body(null);
		}

		if (image != null && !image.isEmpty()) {
			String imageUrl = fileStorageService.storeFile(image, "products/" + subCategoryDto.getName());
			subCategoryDto.setImg(imageUrl);
		}

		return ResponseEntity.ok(this.subCategoryService.update(Integer.parseInt(subCategoryId), subCategoryDto));
	}

	@DeleteMapping("/{subCategoryId}")
	public ResponseEntity<Boolean> deleteById(@PathVariable("subCategoryId") final String subCategoryId) {
		log.info("*** Boolean, resource; delete category by id *");
		this.subCategoryService.deleteById(Integer.parseInt(subCategoryId));
		return ResponseEntity.ok(true);
	}



}










