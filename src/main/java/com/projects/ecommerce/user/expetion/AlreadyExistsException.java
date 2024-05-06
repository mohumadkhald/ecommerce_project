package com.projects.ecommerce.user.expetion;

import lombok.Getter;

@Getter
public class AlreadyExistsException extends RuntimeException {
    private final String field;

    public AlreadyExistsException(String field, String message) {
        super(message);
        this.field = field;
    }

}
