package com.projects.ecommerce.utilts;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class BaseEntityListener {
    @PrePersist
    public void prePersist(Base entity) {
        entity.prePersist();
    }

    @PreUpdate
    public void preUpdate(Base entity) {
        entity.preUpdate();
    }
}
