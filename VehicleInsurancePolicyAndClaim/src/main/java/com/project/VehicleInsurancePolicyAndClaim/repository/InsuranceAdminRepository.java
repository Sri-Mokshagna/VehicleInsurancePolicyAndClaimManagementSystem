package com.project.VehicleInsurancePolicyAndClaim.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.VehicleInsurancePolicyAndClaim.model.InsuranceAdmin;

@Repository
public interface InsuranceAdminRepository extends JpaRepository<InsuranceAdmin,Long>{
	Optional<InsuranceAdmin> findByUsernameAndPassword(String username,String password);
}
