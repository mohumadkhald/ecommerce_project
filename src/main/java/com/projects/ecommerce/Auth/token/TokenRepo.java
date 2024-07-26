package com.projects.ecommerce.Auth.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TokenRepo extends JpaRepository<Token, Integer> {
    @Query("select t from Token t inner join User u on t.user.id = u.id where u.id = :userId and(t.expired = false or t.revoked = false )")
    List<Token> findAllValidTokenByUser(Integer userId);
    @Query("select t from Token t where t.user.id = :userId and (t.expired = true or t.revoked = true or t.expirationDate < CURRENT_TIMESTAMP)")
    List<Token> findAllNotValidTokensByUser(@Param("userId") Integer userId);


    Optional<Token> findByToken(String token);

    void deleteByUserId(Integer id);
}
