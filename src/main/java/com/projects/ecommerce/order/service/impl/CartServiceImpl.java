package com.projects.ecommerce.order.service.impl;


import com.projects.ecommerce.order.domain.Cart;
import com.projects.ecommerce.order.dto.CartDto;
import com.projects.ecommerce.order.dto.CartRequestDto;
import com.projects.ecommerce.order.exception.wrapper.CartNotFoundException;
import com.projects.ecommerce.order.helper.CartMappingHelper;
import com.projects.ecommerce.order.repository.CartRepository;
import com.projects.ecommerce.order.service.CartService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
	
	private final CartRepository cartRepository;

	@Override
	public List<CartDto> findAll() {
		log.info("*** CartDto List, service; fetch all carts *");
		return this.cartRepository.findAll()
				.stream()
					.map(CartMappingHelper::map)
					.distinct()
					.toList();
	}
	
	@Override
	public CartDto findById(final Integer cartId) {
		log.info("*** CartDto, service; fetch cart by id *");
		return this.cartRepository.findById(cartId)
				.map(CartMappingHelper::map)
				.orElseThrow(() -> new CartNotFoundException(String
						.format("Cart with id: %d not found", cartId)));
	}
	
	@Override
	public CartRequestDto save(final @NotNull @Valid CartRequestDto cartDto) {
		log.info("*** CartDto, service; save cart *");
		Cart cart = cartRepository.findByUserId(cartDto.getUserId());
		if (cart != null && Objects.equals(cart.getUserId(), cartDto.getUserId()))
		{
			return CartMappingHelper.map1(cart);
		}
		return CartMappingHelper.map1(this.cartRepository
				.save(CartMappingHelper.map(cartDto)));
	}
	
	@Override
	public CartDto update(final CartDto cartDto) {

		log.info("*** CartDto, service; update cart *");
		return CartMappingHelper.map(this.cartRepository
				.save(CartMappingHelper.map(cartDto)));
	}
	
	@Override
	public CartDto update(final Integer cartId, final CartDto cartDto) {
		log.info("*** CartDto, service; update cart with cartId *");
		return CartMappingHelper.map(this.cartRepository
				.save(CartMappingHelper.map(this.findById(cartId))));
	}
	
	@Override
	public void deleteById(final Integer cartId) {
		log.info("*** Void, service; delete cart by id *");
		this.cartRepository.deleteById(cartId);
	}

	@Override
	public CartRequestDto findByUserId(int userID) {
		Cart cart = cartRepository.findByUserId(userID);
		return CartMappingHelper.map1(cart);

	}

	@Override
	public Cart findCartByUserId(Integer userId) {
		return cartRepository.findCartByUserId(userId);
	}


}










