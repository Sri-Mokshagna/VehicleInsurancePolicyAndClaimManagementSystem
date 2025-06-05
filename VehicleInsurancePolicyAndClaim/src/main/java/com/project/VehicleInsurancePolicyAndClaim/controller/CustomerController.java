package com.project.VehicleInsurancePolicyAndClaim.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.VehicleInsurancePolicyAndClaim.model.Claim;
import com.project.VehicleInsurancePolicyAndClaim.model.Customer;
import com.project.VehicleInsurancePolicyAndClaim.model.Policy;
import com.project.VehicleInsurancePolicyAndClaim.model.Vehicle;
import com.project.VehicleInsurancePolicyAndClaim.service.ClaimService;
import com.project.VehicleInsurancePolicyAndClaim.service.CustomerService;
import com.project.VehicleInsurancePolicyAndClaim.service.PolicyService;
import com.project.VehicleInsurancePolicyAndClaim.service.VehicleService;

import jakarta.servlet.http.HttpSession;

@Controller
public class CustomerController {
	@Autowired 
	private CustomerService customerService;
	@Autowired
	private VehicleService vehicleService;
	@Autowired
	private PolicyService policyService;
	@Autowired
	private ClaimService claimService;
	@GetMapping("/")
	public String home() {
		return "home";
	}
	@GetMapping("/about")
	public String aboutUs()
	{
		return "about";
	}

	@GetMapping("/privacy")
	public String privacy(){
		return "privacy";
	}

	@GetMapping("/terms")
	public String termsAndConditions(){
		return "termsAndConditions";
	}

	@GetMapping("/policy/list")
	public String dashboardPolicy(){
		return "dashboardPolicy";
	}
	
	@GetMapping("/contact")
	public String contactUs() {
		return "contactUs";
	}
	
	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("customer",new Customer());
		return "customer/register";
	}
	
	
	
	@PostMapping("/register")
	public String registerCustomer(@ModelAttribute("customer") Customer customer,Model model) {
		customerService.saveCustomer(customer);
		model.addAttribute("successMessage","Customer registered successfully!");
		model.addAttribute("customer", new Customer());
		System.out.println("Register method hit");
		return "customer/register";
	}
	
	@GetMapping("/login")
	public String loginForm() {
		return "customer/login";
	}
	
	@PostMapping("/login")
	public String processLogin(@RequestParam String email,@RequestParam String password, HttpSession session,Model model) {
		Optional<Customer> customer = customerService.authenticate(email, password);
		if(customer.isPresent())
		{
			Customer cust = customer.get();
			session.setAttribute("loggedInCustomer",cust);
			return "redirect:/dashboard";
		}
		else {
			model.addAttribute("error","Invalid Email or Password");
			return "customer/login";
		}
	}
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login?logout=true";
	}
	@GetMapping("/dashboard")
	public String dashboard(HttpSession session,Model model) {
		Customer customer = (Customer) session.getAttribute("loggedInCustomer");
		if(customer==null) return "redirect:/login";
		List<Vehicle> vehicles = vehicleService.getVehiclesByCustomer(customer);
		List<Policy> policies = policyService.getPoliciesForCustomer(customer);
		List<Claim> claims = claimService.getClaimsByCustomer(customer);
		List<Claim> pendingClaims = claimService.getClaimByStatus("SUBMITTED");
		model.addAttribute("policies", policies);
		model.addAttribute("claims", claims);
		model.addAttribute("customer",customer);
		model.addAttribute("vehicles", vehicles);
		model.addAttribute("pendingClaims", pendingClaims);
		return "customer/dashboard";
	}
}
