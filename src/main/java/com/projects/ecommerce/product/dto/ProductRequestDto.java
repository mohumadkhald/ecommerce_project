package com.projects.ecommerce.product.dto;

import com.projects.ecommerce.product.domain.Color;
import com.projects.ecommerce.product.domain.ProductImage;
import com.projects.ecommerce.product.domain.Size;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProductRequestDto implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 1L;
	private Integer productId;

	@NotBlank(message = "Product title cannot be blank")
	private String productTitle;
	private List<String> imageUrls;

	@NotNull(message = "Color must not be null")
	@Enumerated(EnumType.STRING)
	private Color color;

	@NotNull(message = "Size must not be null")
	@Enumerated(EnumType.STRING)
	private Size size;

	@NotNull(message = "Quantity cannot be null")
	@PositiveOrZero(message = "Quantity must be a positive number or zero")
	private Integer quantity;

	@NotNull(message = "Price cannot be null")
	@PositiveOrZero(message = "Price must be a positive number or zero")
	private Double price;

	private Double discountedPrice;

	@PositiveOrZero(message = "Discount percent must be a positive number or zero")
	private Double discountPercent;

	@NotNull(message = "SubCategory cannot be null")
	private Integer subCategoryId;

	private String email;

	public void setDiscountedPrice() {
		this.discountedPrice = this.price - (this.price * this.discountPercent / 100);
	}
}










