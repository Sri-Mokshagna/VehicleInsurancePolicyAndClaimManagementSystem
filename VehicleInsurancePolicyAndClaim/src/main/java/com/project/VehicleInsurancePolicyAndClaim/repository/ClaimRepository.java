package com.project.VehicleInsurancePolicyAndClaim.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.VehicleInsurancePolicyAndClaim.model.Claim;
import com.project.VehicleInsurancePolicyAndClaim.model.Customer;

@Repository
public interface ClaimRepository extends JpaRepository<Claim,Long>{
	List<Claim> findByPolicy_Vehicle_Customer(Customer customer);
	List<Claim> findByClaimStatus(String status);
}
