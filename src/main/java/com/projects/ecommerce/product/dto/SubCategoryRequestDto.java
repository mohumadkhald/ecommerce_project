package com.projects.ecommerce.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SubCategoryRequestDto implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 1L;


	private Integer subCategoryId;
	@NotBlank(message = "categoryTitle cannot be empty or start space")
	private String subCategoryTitle;
	private String imageUrl;

	@JsonInclude(Include.NON_NULL)
	private Set<SubCategoryRequestDto> subCategoriesDtos;

	@NotNull(message = "parentCategoryId cannot be empty or start space")
	private Integer parentCategoryId;

	@JsonInclude(Include.NON_NULL)
	private Set<ProductDto> productDtos;
	
}










