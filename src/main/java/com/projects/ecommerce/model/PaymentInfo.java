package com.projects.ecommerce.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PaymentInfo {
    private String paymentMethod;
    private String status;
    private String paymentId;
    private String RazPaymentLinkId;
    private String RazPaymentLinkRefId;
    private String RazPaymentLinkStatus;
    private String RazPaymentId;

}
