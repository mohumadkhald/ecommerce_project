package com.projects.ecommerce.product.helper;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ImageValidatorImpl.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImage {
    String message() default "Image file is required";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
