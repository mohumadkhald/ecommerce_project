package com.projects.ecommerce.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {
    @Valid
    @NotNull
    private PaymentInfo paymentInfo;

    @Valid
    @NotNull
    private Address address;
}

