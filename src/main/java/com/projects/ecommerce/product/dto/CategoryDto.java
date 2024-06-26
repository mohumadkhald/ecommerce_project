package com.projects.ecommerce.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class CategoryDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private Integer categoryId;

    @NotBlank(message = "categoryTitle cannot be empty or start space")
    private String categoryTitle;

	private String description;

	private String img;

	@JsonInclude(Include.NON_NULL)
	private Set<SubCategoryDto> subCategoryDtos;


}










