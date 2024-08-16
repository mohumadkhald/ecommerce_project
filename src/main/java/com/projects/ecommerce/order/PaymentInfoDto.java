package com.projects.ecommerce.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class PaymentInfoDto {
    private String cardHolderName;
    private String cardNumber; // Consider using masking or only storing last 4 digits for security
    private String expirationDate;
    private String cvv;
}
