package com.project.VehicleInsurancePolicyAndClaim.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.VehicleInsurancePolicyAndClaim.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer>{
	Optional<Customer> findByEmailAndPassword(String email,String password);

	Optional<Customer> findByName(String username);

	Customer findByCustomerId(Long id);

	Optional<Customer> findByEmail(String email);
}
