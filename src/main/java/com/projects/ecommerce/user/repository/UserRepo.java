package com.projects.ecommerce.user.repository;


import com.projects.ecommerce.user.model.Role;
import com.projects.ecommerce.user.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    User findByEmail(String email);
    User findUserById(Integer userId1);

    @Query("select u from User u where u.firstname like %:query% or u.lastname like %:query% or u.email like %:query% or u.phone like %:query%")
    List<User> searchUser(String query);

    boolean findUserByEmail(@NotBlank(message = "Email cannot be empty or start space") @Pattern(regexp = "^(.+)@(.+)$", message = "Email should be valid") String user);

    boolean existsByEmail(String email);


    User findByPasswordReset_ResetCode(String code);

    List<User> findAllByRole(Role role);
}
