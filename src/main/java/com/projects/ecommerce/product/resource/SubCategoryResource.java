package com.projects.ecommerce.product.resource;


import com.projects.ecommerce.product.dto.SubCategoryDto;
import com.projects.ecommerce.product.dto.response.collection.DtoCollectionResponse;
import com.projects.ecommerce.product.service.SubCategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sub-categories")
@Slf4j
@RequiredArgsConstructor
public class SubCategoryResource {
	
	private final SubCategoryService subCategoryService;
	
	@GetMapping
	public ResponseEntity<DtoCollectionResponse<SubCategoryDto>> findAll() {
		log.info("*** CategoryDto List, controller; fetch all categories *");
		return ResponseEntity.ok(new DtoCollectionResponse<>(this.subCategoryService.findAll()));
	}
	
	@GetMapping("/{subCategoryId}")
	public ResponseEntity<SubCategoryDto> findById(
			@PathVariable("subCategoryId")
			@NotBlank(message = "Input must not be blank")
			@Valid final String subCategoryId) {
		log.info("*** CategoryDto, resource; fetch category by id *");
		return ResponseEntity.ok(this.subCategoryService.findById(Integer.parseInt(subCategoryId)));
	}
	
	@PostMapping
	public ResponseEntity<SubCategoryDto> save(
			@RequestBody 
			@Valid final SubCategoryDto subCategoryDto) {
		log.info("*** CategoryDto, resource; save category *");
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
			@RequestBody 
			@NotNull(message = "Input must not be NULL") 
			@Valid final SubCategoryDto subCategoryDto) {
		log.info("*** CategoryDto, resource; update category with categoryId *");
		return ResponseEntity.ok(this.subCategoryService.update(Integer.parseInt(subCategoryId), subCategoryDto));
	}
	
	@DeleteMapping("/{subCategoryId}")
	public ResponseEntity<Boolean> deleteById(@PathVariable("subCategoryId") final String subCategoryId) {
		log.info("*** Boolean, resource; delete category by id *");
		this.subCategoryService.deleteById(Integer.parseInt(subCategoryId));
		return ResponseEntity.ok(true);
	}
	
	
	
}










