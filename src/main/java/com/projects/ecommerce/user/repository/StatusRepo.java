package com.projects.ecommerce.user.repository;

import com.projects.ecommerce.user.model.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepo extends JpaRepository<AccountStatus, Long> {

    // Define methods to interact with the status table
}
