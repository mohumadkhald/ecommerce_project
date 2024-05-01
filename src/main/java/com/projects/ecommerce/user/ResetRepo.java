package com.projects.ecommerce.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetRepo extends JpaRepository<PasswordReset, Integer> {
    @Modifying
    @Query("DELETE FROM PasswordReset pr WHERE pr.user.id = :userId")
    void deleteByUserId(@Param("userId") Integer userId);
    // Define methods to interact with the reset table
}
