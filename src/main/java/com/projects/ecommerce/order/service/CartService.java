package com.projects.ecommerce.order.service;


import com.projects.ecommerce.order.domain.Cart;
import com.projects.ecommerce.order.dto.CartDto;
import com.projects.ecommerce.order.dto.CartRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface CartService {
	
	List<CartDto> findAll();
	CartDto findById(final Integer cartId);
	CartRequestDto save(final @NotNull @Valid CartRequestDto cartDto);
	CartDto update(final CartDto cartDto);
	CartDto update(final Integer cartId, final CartDto cartDto);
	void deleteById(final Integer cartId);

	CartRequestDto findByUserId(int userID);


	Cart findCartByUserId(Integer userId);
}
