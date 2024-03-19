package com.projects.ecommerce.repo;

import com.projects.ecommerce.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderRepo extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findByUserId(Long userId);
}
