package com.projects.ecommerce.user.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "role" })
public enum Role {
    USER,ADMIN
}
