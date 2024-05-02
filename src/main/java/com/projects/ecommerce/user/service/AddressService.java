package com.projects.ecommerce.user.service;


import com.projects.ecommerce.user.dto.AddressDto;
import com.projects.ecommerce.user.dto.AddressRequestDto;

import java.util.List;

public interface AddressService {
	
	List<AddressDto> findAll();
	AddressDto findById(final Integer addressId);
	AddressDto save(final AddressRequestDto addressRequestDto);
	AddressDto update(final AddressRequestDto addressRequestDto);
	AddressDto update(final Integer addressId, final AddressDto addressDto);
	void deleteById(final Integer addressId);
	
}
