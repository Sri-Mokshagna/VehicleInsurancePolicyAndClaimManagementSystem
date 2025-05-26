package com.project.VehicleInsurancePolicyAndClaim.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.VehicleInsurancePolicyAndClaim.model.Customer;
import com.project.VehicleInsurancePolicyAndClaim.model.Policy;
import com.project.VehicleInsurancePolicyAndClaim.model.Vehicle;
import com.project.VehicleInsurancePolicyAndClaim.repository.PolicyRepository;

@Service
public class PolicyService {
 
    @Autowired
    private PolicyRepository policyRepository;
 
    public List<Policy> getPoliciesForCustomer(Long customerId) {
        return policyRepository.findByVehicleCustomerCustomerId(customerId);
    }
    public List<Policy> getPoliciesForCustomer(Customer customer) {
        return policyRepository.findByVehicle_Customer(customer);
    }
 
    public Policy calculatePolicy(Vehicle vehicle, String coverageType) {
        int cy = LocalDate.now().getYear();
        int age= cy-vehicle.getYearOfManufacture();
        double basePremium = vehicle.getPrice() * 0.015 + vehicle.getCc() * 0.5 + age * 100;
        double coverageAmount = vehicle.getPrice() * (coverageType.equalsIgnoreCase("PREMIUM") ? 1.0 : 0.8);
 
        if (coverageType.equalsIgnoreCase("PREMIUM")) {
            basePremium *= 1.25; 
        }
 
        Policy policy = new Policy();
        policy.setPremiumAmount(basePremium);
        policy.setCoverageAmount(coverageAmount);
        policy.setStartDate(LocalDate.now());
        policy.setEndDate(LocalDate.now().plusYears(1));
        policy.setPolicyStatus("ACTIVE");
        policy.setPolicyNumber(generateRandomPolicyNumber());
        policy.setCoverageType(coverageType);
        policy.setVehicle(vehicle);
        return policy;
    }
 
    public void savePolicy(Policy policy) {
        policyRepository.save(policy);
    }
 
    private String generateRandomPolicyNumber() {
        return "POL" + new Random().nextInt(100000);
    }
    
    public Policy getPolicyById(Long id) {
    	return policyRepository.findById(id).orElseThrow(()-> new RuntimeException("Policy not found"));
    }

}