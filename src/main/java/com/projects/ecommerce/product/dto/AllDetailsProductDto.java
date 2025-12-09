package com.projects.ecommerce.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.projects.ecommerce.product.domain.ProductImage;
import com.projects.ecommerce.product.domain.ProductVariation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AllDetailsProductDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Integer productId;
    private String productTitle;
    private List<String> imageUrls;
    private String sku;
    private Double price;
    private Double discountPercent;
    private Double discountPrice;
    private int allQuantity;
    private String email;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private LocalDateTime deletedOn;

    @JsonProperty("productVariation")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ProductVariationDto> productVariations;

    @JsonProperty("subCategory")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private SubCategoryDto subCategoryDto;

}
