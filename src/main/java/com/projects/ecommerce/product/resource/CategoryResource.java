package com.projects.ecommerce.product.resource;


import com.projects.ecommerce.product.dto.CategoryDto;
import com.projects.ecommerce.product.dto.response.collection.DtoCollectionResponse;
import com.projects.ecommerce.product.service.CategoryService;
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
@RequestMapping("/api/categories")
@Slf4j
@RequiredArgsConstructor
public class CategoryResource {

	private final CategoryService categoryService;
//	private final FileStorageService fileStorageService;
//	@Autowired
//	private CloudinaryService cloudinaryService;

	private final FileUploadStrategy fileStorageService;





	@GetMapping
	public ResponseEntity<DtoCollectionResponse<CategoryDto>> findAll() {
		log.info("*** CategoryDto List, controller; fetch all categories *");
		return ResponseEntity.ok(new DtoCollectionResponse<>(this.categoryService.findAll()));
	}

	@GetMapping("/{categoryId}")
	public ResponseEntity<CategoryDto> findById(
			@PathVariable("categoryId")
			@NotBlank(message = "Input must not be blank")
			@Valid final String categoryId) {
		log.info("*** CategoryDto, resource; fetch category by id *");
		return ResponseEntity.ok(this.categoryService.findById(Integer.parseInt(categoryId)));
	}

	@PostMapping
	public ResponseEntity<CategoryDto> save(
			@ModelAttribute @Valid final CategoryDto categoryRequestDto,
			@RequestPart(value = "image", required = false) MultipartFile image,
			BindingResult bindingResult
			) throws IOException {
		log.info("*** CategoryDto, resource; save category *");
		// Check if the image is null or empty and add a global error
		if (image == null || image.isEmpty()) {
			throw new IllegalStateException("Image file is required");
		}

		// Check for other validation errors
		if (bindingResult.hasErrors()) {
			return ResponseEntity.badRequest().body(null);
		}

		if (image != null && !image.isEmpty()) {
//			String imageUrl = fileStorageService.storeFile(image, "categories/" + categoryRequestDto.getCategoryTitle());
//			categoryRequestDto.setImg(imageUrl);
//			String imageUrl = cloudinaryService.uploadToFolder(image, "categories/"+ categoryRequestDto.getCategoryTitle());
//			categoryRequestDto.setImg(imageUrl);

			String imageUrl = fileStorageService.storeFile(image, "categories/" + categoryRequestDto.getCategoryTitle());
			categoryRequestDto.setImg(imageUrl);


		}
		return ResponseEntity.ok(this.categoryService.save(categoryRequestDto));
	}

	@PutMapping
	public ResponseEntity<CategoryDto> update(
			@RequestBody
			@NotNull(message = "Input must not be NULL")
			@Valid final CategoryDto categoryDto) {
		log.info("*** CategoryDto, resource; update category *");
		return ResponseEntity.ok(this.categoryService.update(categoryDto));
	}

	@PutMapping("/{categoryId}")
	public ResponseEntity<CategoryDto> update(
			@PathVariable("categoryId")
			@NotBlank(message = "Input must not be blank")
			@Valid final String categoryId,
			@ModelAttribute @Valid final CategoryDto categoryRequestDto,
			@RequestPart(value = "image", required = false) MultipartFile image,
			BindingResult bindingResult
	) throws IOException {
		log.info("*** CategoryDto, resource; save category *");
		// Check if the image is null or empty and add a global error
		if (image == null || image.isEmpty()) {
			throw new IllegalStateException("Image file is required");
		}

		// Check for other validation errors
		if (bindingResult.hasErrors()) {
			return ResponseEntity.badRequest().body(null);
		}

//		if (image != null && !image.isEmpty()) {
//			String imageUrl = fileStorageService.storeFile(image, "products/" + categoryRequestDto.getCategoryTitle());
//			categoryRequestDto.setImg(imageUrl);
//		}
		return ResponseEntity.ok(this.categoryService.update(Integer.parseInt(categoryId), categoryRequestDto));
	}

	@DeleteMapping("/{categoryId}")
	public ResponseEntity<Boolean> deleteById(@PathVariable("categoryId") final String categoryId) {
		log.info("*** Boolean, resource; delete category by id *");
		this.categoryService.deleteById(Integer.parseInt(categoryId));
		return ResponseEntity.ok(true);
	}



}










