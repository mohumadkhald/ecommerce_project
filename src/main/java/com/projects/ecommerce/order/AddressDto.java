package com.projects.ecommerce.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class AddressDto {
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
}