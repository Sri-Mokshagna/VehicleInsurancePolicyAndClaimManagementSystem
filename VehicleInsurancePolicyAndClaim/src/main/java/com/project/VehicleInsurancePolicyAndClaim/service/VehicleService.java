package com.project.VehicleInsurancePolicyAndClaim.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project.VehicleInsurancePolicyAndClaim.model.Customer;
import com.project.VehicleInsurancePolicyAndClaim.model.Vehicle;
import com.project.VehicleInsurancePolicyAndClaim.repository.VehicleRepository;

@Service
public class VehicleService {
	@Autowired
	private VehicleRepository vehicleRepository;
	
	@Value("${upload.dir}")
	private String uploadDir;
	
	public Vehicle saveVehicle(Vehicle vehicle, MultipartFile imageFile) throws IOException{
		if(!imageFile.isEmpty()) {
			String filename = UUID.randomUUID()+"_"+imageFile.getOriginalFilename();
			Path filePath = Paths.get(uploadDir,filename);
			Files.copy(imageFile.getInputStream(),filePath);
			vehicle.setImagePath(filename);
		}
		return vehicleRepository.save(vehicle);
	}
	
	public List<Vehicle> getVehiclesByCustomer(Customer customer){
		return vehicleRepository.findByCustomer(customer);
	}
	 
	public List<Vehicle> getVehicleWithoutPolicy(Customer customer){
		return vehicleRepository.findByCustomerAndPolicyIsNull(customer);
	}
	
	public Vehicle getVehicleById(Long id) {
		return vehicleRepository.findById(id).orElse(null);
	}
}
