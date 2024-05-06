package com.projects.ecommerce.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class UserDto implements Serializable {
	

	private Integer userId;
	
	private String firstName;
	
	private String lastName;
	
	private String imageUrl;
	
	private String email;
	
	private String phone;

	private boolean emailVerified;

	@JsonProperty("address")
	@JsonInclude(value = Include.NON_NULL)
	private Set<AddressDto> addressDtos;

	@JsonProperty("credential")
	@JsonInclude(value = Include.NON_NULL)
	private CredentialDto credentialDto;
	
}










