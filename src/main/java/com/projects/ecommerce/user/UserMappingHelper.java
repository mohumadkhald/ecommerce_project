package com.projects.ecommerce.user;

import com.projects.ecommerce.user.dto.CredentialDto;
import com.projects.ecommerce.user.dto.UserDto;
import com.projects.ecommerce.user.model.AccountStatus;
import com.projects.ecommerce.user.model.EmailVerification;
import com.projects.ecommerce.user.model.User;

public interface UserMappingHelper {

	public static UserDto map(final User user) {
		if (user == null) {
			return null;
		}

		AccountStatus accountStatus = null;
		if (user.getAccountStatus() != null) {
			accountStatus = AccountStatus.builder()
					.id(user.getAccountStatus().getId())
					.user(user.getAccountStatus().getUser())
					.credentialsNonExpired(user.getAccountStatus().getUser().isCredentialsNonExpired())
					.accountNonLocked(user.getAccountStatus().isAccountNonLocked())
					.accountNonExpired(user.getAccountStatus().getUser().isAccountNonExpired())
					.build();
		}


		return UserDto.builder()
				.userId(user.getId())
				.firstName(user.getFirstname())
				.lastName(user.getLastname())
				.gender(user.getGender())
				.imageUrl(user.getImgUrl())
				.email(user.getEmail())
				.needPassword(user.isNeedsToSetPassword())
				.role(user.getRole().toString())
				.phone(user.getPhone())
				.emailVerified(user.getEmailVerification().isEmailVerified())
				.credentialDto(new CredentialDto(user.getId(), user.isAccountNonExpired(), user.isAccountNonLocked(), user.isCredentialsNonExpired()))
				.build();
	}

	public static User map(final UserDto userDto) {
		if (userDto == null) {
			return null;
		}

		AccountStatus credential = null;
		if (userDto.getCredentialDto() != null) {
			credential = AccountStatus.builder()
					.id(userDto.getCredentialDto().id())
					.accountNonExpired(userDto.getCredentialDto().accountNonExpired())
					.credentialsNonExpired(userDto.getCredentialDto().credentialsNonExpired())
					.accountNonLocked(userDto.getCredentialDto().accountNonLocked())
					.build();
		}

		return User.builder()
				.id(userDto.getUserId())
				.firstname(userDto.getFirstName())
				.lastname(userDto.getLastName())
				.imgUrl(userDto.getImageUrl())
				.email(userDto.getEmail())
				.phone(userDto.getPhone())
				.emailVerification(
						new EmailVerification()
				)
				.accountStatus(credential)
				.build();
	}
}







