package com.project.VehicleInsurancePolicyAndClaim.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.VehicleInsurancePolicyAndClaim.model.Customer;
import com.project.VehicleInsurancePolicyAndClaim.model.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle,Long> {
	List<Vehicle> findByCustomer(Customer customer);
	List<Vehicle> findByCustomerAndPolicyIsNull(Customer customer);
}
