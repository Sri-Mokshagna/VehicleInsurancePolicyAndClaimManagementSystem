package com.project.VehicleInsurancePolicyAndClaim.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.VehicleInsurancePolicyAndClaim.model.Customer;
import com.project.VehicleInsurancePolicyAndClaim.model.Policy;
import com.project.VehicleInsurancePolicyAndClaim.model.Vehicle;

@Repository
public interface PolicyRepository extends JpaRepository<Policy,Long>{
	List<Policy> findByVehicleCustomerCustomerId(Long customerId);
	Optional<Policy> findByVehicle(Vehicle vehicle); 
	List<Policy> findByVehicle_Customer(Customer customer);
}

