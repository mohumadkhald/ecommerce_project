package com.projects.ecommerce.user.repository;


import com.projects.ecommerce.user.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface AddressRepository extends JpaRepository<Address, Integer> {


    @Query("SELECT a FROM Address a WHERE a.user.id = :userId")
    Set<Address> getUserAddresses(Integer userId);}
