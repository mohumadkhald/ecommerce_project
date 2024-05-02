package com.projects.ecommerce.user.repository;


import com.projects.ecommerce.user.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Integer> {
	
	
	
}
