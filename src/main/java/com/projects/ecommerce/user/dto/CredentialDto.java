package com.projects.ecommerce.user.dto;

public record CredentialDto(Integer id,
                            boolean accountNonExpired,
                            boolean accountNonLocked,
                            boolean credentialsNonExpired) {
}
