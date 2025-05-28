package com.project.VehicleInsurancePolicyAndClaim.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.VehicleInsurancePolicyAndClaim.model.Customer;
import com.project.VehicleInsurancePolicyAndClaim.model.Policy;
import com.project.VehicleInsurancePolicyAndClaim.model.Vehicle;
import com.project.VehicleInsurancePolicyAndClaim.service.PolicyService;
import com.project.VehicleInsurancePolicyAndClaim.service.VehicleService;

import jakarta.servlet.http.HttpSession;

@Controller
public class PolicyController {
 
	@Autowired
	private PolicyService policyService;
	@Autowired
	private VehicleService vehicleService;
 
	@GetMapping("/policies")
	public String viewPolicies(HttpSession session, Model model) {
		Customer customer = (Customer) session.getAttribute("loggedInCustomer");
		List<Policy> policies = policyService.getPoliciesForCustomer((long) customer.getCustomerId());
		model.addAttribute("policies", policies);
		return "policy/policies";
	}
 
	@GetMapping("/buynewpolicy")
	public String buyNewPolicyForm(HttpSession session, Model model) {
	Customer customer = (Customer) session.getAttribute("loggedInCustomer");
	List<Vehicle> vehiclesWithoutPolicy = vehicleService.getVehicleWithoutPolicy(customer);
	model.addAttribute("vehicles", vehiclesWithoutPolicy);
	return "policy/buynewpolicy";
	}
 
	@PostMapping("/buynewpolicy/preview")
	public String previewPolicy(@RequestParam("vehicleId") Long vehicleId,@RequestParam("coverageType") String coverageType,Model model) {
		Optional<Vehicle> vehicleOpt = Optional.ofNullable(vehicleService.getVehicleById(vehicleId));
		if (vehicleOpt.isPresent()) {
			Vehicle vehicle = vehicleOpt.get();
			Policy policy = policyService.calculatePolicy(vehicle, coverageType);
			model.addAttribute("policy", policy);
			model.addAttribute("vehicle", vehicle);
			return "policy/previewpolicy";
		}
		return "redirect:/buynewpolicy";
	}
 
	@PostMapping("/buynewpolicy/confirm")
	public String confirmPolicy(@RequestParam("vehicleId") Long vehicleId,@RequestParam("coverageType") String coverageType,Model model) {
		Optional<Vehicle> vehicleOpt = Optional.ofNullable(vehicleService.getVehicleById(vehicleId));
		if (vehicleOpt.isPresent()) {
			Vehicle vehicle = vehicleOpt.get();
			Policy policy = policyService.calculatePolicy(vehicle, coverageType);
			policyService.savePolicy(policy);
			return "redirect:/policies";
		}
		return "redirect:/buynewpolicy";
	}
	
	@GetMapping("/viewpolicy/{id}")
	public String viewPolicy(@PathVariable Long id, Model model, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return "redirect:/login";
        }
 
        Policy policy = policyService.getPolicyById(id);
 
        if (!(policy.getVehicle().getCustomer().getCustomerId()==customer.getCustomerId())) {
            return "redirect:/policies";
        }
 
        model.addAttribute("policy", policy);
        return "policy/viewpolicy";
    }
	
	@PostMapping("/renewpolicy/{id}")
	public String renewPolicy(@PathVariable Long id,HttpSession session) {
		Customer customer = (Customer) session.getAttribute("loggedInCustomer");
		if(customer==null) return "redirect:/login";
		Policy policy = policyService.getPolicyById(id);
		
		if(policy.getVehicle().getCustomer().getCustomerId() != (customer.getCustomerId())){
			return "redirect:/policies";
		}
		
		if(!policy.getPolicyStatus().equalsIgnoreCase("EXPIRED")) {
			return "redirect:/policies";
		}
		
		policyService.renewPolicy(policy);
		return "redirect:/policies";
	}
	
}