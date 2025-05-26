package com.project.VehicleInsurancePolicyAndClaim.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project.VehicleInsurancePolicyAndClaim.model.Claim;
import com.project.VehicleInsurancePolicyAndClaim.model.Customer;
import com.project.VehicleInsurancePolicyAndClaim.repository.ClaimRepository;
import com.project.VehicleInsurancePolicyAndClaim.repository.PolicyRepository;

@Service
public class ClaimService {
	@Autowired
	private ClaimRepository claimRepository;
	
	@Value("${upload.dir}")
	private String uploadDir;
	
	public void fileClaim(Claim claim,MultipartFile image) throws IOException{
		if(!image.isEmpty()) {
			String fileName = UUID.randomUUID()+"_"+image.getOriginalFilename();
			Path imagePath = Paths.get(uploadDir, fileName);
			Files.copy(image.getInputStream(),imagePath);
			claim.setImagePath(fileName);
		}
		claim.setClaimDate(LocalDate.now());
		claim.setClaimStatus("SUBMITTED");
		claimRepository.save(claim);
	}
	public List<Claim> getClaimsByCustomer(Customer customer){
		return claimRepository.findByPolicy_Vehicle_Customer(customer);
	}
}
