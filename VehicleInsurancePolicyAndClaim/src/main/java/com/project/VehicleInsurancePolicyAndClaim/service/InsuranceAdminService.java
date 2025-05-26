package com.project.VehicleInsurancePolicyAndClaim.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.VehicleInsurancePolicyAndClaim.model.Claim;
import com.project.VehicleInsurancePolicyAndClaim.model.InsuranceAdmin;
import com.project.VehicleInsurancePolicyAndClaim.repository.ClaimRepository;
import com.project.VehicleInsurancePolicyAndClaim.repository.InsuranceAdminRepository;

@Service
public class InsuranceAdminService {
	@Autowired
	private InsuranceAdminRepository adminRepo;
	
	@Autowired
	private ClaimRepository claimRepo;
	
	public List<Claim> getSubmittedClaims(){
		return claimRepo.findByClaimStatus("SUBMITTED");
	}
	public Claim getClaimById(Long claimId) {
		return claimRepo.findById(claimId).orElse(null);
	}
	public Optional<InsuranceAdmin> authenticate(String username,String password){
		return adminRepo.findByUsernameAndPassword(username, password);
	}
	public void updateClaimStatus(Long claimId,String status,String reason) {
		Claim claim = getClaimById(claimId);
		if(claim!=null) {
			claim.setClaimStatus(status);
			claim.setClaimReason(reason);
			claimRepo.save(claim);
		}
	}
}
