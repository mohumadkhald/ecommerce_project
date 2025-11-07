package com.projects.ecommerce.product.helper;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ValidationErrorResponse {
    private List<Violation> violations = new ArrayList<>();

    @Setter
    @Getter
    public static class Violation {
        private String fieldName;
        private String message;

        public Violation(String fieldName, String message) {
            this.fieldName = fieldName;
            this.message = message;
        }
    }
}
