package com.projects.ecommerce.user;


import com.projects.ecommerce.user.dto.AddressDto;
import com.projects.ecommerce.user.dto.AddressRequestDto;
import com.projects.ecommerce.user.dto.UserDto;
import com.projects.ecommerce.user.model.Address;
import com.projects.ecommerce.user.model.User;

public interface AddressMappingHelper {



	public static AddressDto map(final Address address) {
		return AddressDto.builder()
				.addressId(address.getAddressId())
				.fullAddress(address.getFullAddress())
				.postalCode(address.getPostalCode())
				.city(address.getCity())
				.userDto(
					UserDto.builder()
						.userId(address.getUser().getId())
						.firstName(address.getUser().getFirstname())
						.lastName(address.getUser().getLastname())
						.imageUrl(address.getUser().getImgUrl())
						.email(address.getUser().getEmail())
						.phone(address.getUser().getPhone())
						.build())
				.build();
	}
	
	public static Address map(final AddressRequestDto addressDto) {
		return Address.builder()
				.addressId(addressDto.getAddressId())
				.fullAddress(addressDto.getFullAddress())
				.postalCode(addressDto.getPostalCode())
				.city(addressDto.getCity())
				.user(
					User.builder()
						.id(addressDto.getUserId())
						.build())
				.build();
	}
	public static Address map(final AddressDto addressDto) {
		return Address.builder()
				.addressId(addressDto.getAddressId())
				.fullAddress(addressDto.getFullAddress())
				.postalCode(addressDto.getPostalCode())
				.city(addressDto.getCity())
				.user(
						User.builder()
								.id(addressDto.getUserDto().getUserId())
								.build())
				.build();
	}
	
	
}










