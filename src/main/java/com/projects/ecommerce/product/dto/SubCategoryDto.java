package com.projects.ecommerce.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SubCategoryDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	@NotBlank(message = "name cannot be empty or start space")
	private String name;
	private String img;

	private Integer categoryId;

	@JsonInclude(Include.NON_NULL)
	private Set<ProductDto> productDtos;

}










