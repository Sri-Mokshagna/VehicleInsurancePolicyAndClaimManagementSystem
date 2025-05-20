package com.project.VehicleInsurancePolicyAndClaim.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.VehicleInsurancePolicyAndClaim.model.InsuranceAdmin;
import com.project.VehicleInsurancePolicyAndClaim.repository.InsuranceAdminRepository;

@Service
public class InsuranceAdminService {
	@Autowired
	private InsuranceAdminRepository adminRepo;
	
	public Optional<InsuranceAdmin> authenticate(String username,String password){
		return adminRepo.findByUsernameAndPassword(username, password);
	}
}
