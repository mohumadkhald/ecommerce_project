package com.projects.ecommerce.order.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInfo {

    @NotBlank(message = "Card holder name is required")
    @Column(name = "card_holder_name")
    private String cardHolderName;

    @NotBlank(message = "Card number is required")
    @Pattern(
            regexp = "^\\d{13,19}|(\\d{4}-){2,4}\\d{1,5}$",
            message = "Card number must be 13 to 19 digits, with optional hyphens every 4 digits"
    )
    @Column(name = "card_number")
    private String cardNumber;

//    @NotBlank(message = "Expiration date is required")
//    @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{4}", message = "Expiration date must be in MM/YY format")
//    @Column(name = "expiration_date")
//    @FutureOrPresent
//    private String expirationDate;

    @NotNull(message = "Expiration date is required")
    @FutureOrPresent(message = "Expiration date must be in the future or present")
    @JsonFormat(pattern = "MM/yy")
    private YearMonth expirationDate;

    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "\\d{3}", message = "CVV must be 3 digits")
    @Column(name = "cvv")
    private String cvv;
}