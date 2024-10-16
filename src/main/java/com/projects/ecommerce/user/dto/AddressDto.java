package com.projects.ecommerce.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AddressDto {
	

	private Integer addressId;
	
	private String fullAddress;
	
	private String postalCode;

	private String state;

	private String city;

	private String country;
	
	@JsonProperty("user")
	@JsonInclude(value = Include.NON_NULL)
	private UserDto userDto;
	
}










