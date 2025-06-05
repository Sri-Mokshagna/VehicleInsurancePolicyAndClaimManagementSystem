package com.project.VehicleInsurancePolicyAndClaim.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.VehicleInsurancePolicyAndClaim.model.Claim;
import com.project.VehicleInsurancePolicyAndClaim.model.Customer;
import com.project.VehicleInsurancePolicyAndClaim.model.InsuranceAdmin;
import com.project.VehicleInsurancePolicyAndClaim.model.Policy;
import com.project.VehicleInsurancePolicyAndClaim.service.ClaimService;
import com.project.VehicleInsurancePolicyAndClaim.service.CustomerService;
import com.project.VehicleInsurancePolicyAndClaim.service.InsuranceAdminService;
import com.project.VehicleInsurancePolicyAndClaim.service.PolicyService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/insuranceadmin")
public class InsuranceAdminController {
	@Autowired
	private InsuranceAdminService insuranceAdminService;
	
	@Autowired
	private PolicyService policyService;
	
	@Autowired
	private ClaimService claimService;
	
	@Autowired
	private CustomerService customerService;
	
	@GetMapping("/login")
	public String loginForm() {
		return "admin/login";
	}
	
	@PostMapping("/login")
	public String login(@RequestParam String username, @RequestParam String password, HttpSession session,Model model) {
		Optional<InsuranceAdmin> admin = insuranceAdminService.authenticate(username, password);
		if(!admin.isEmpty()) {
			session.setAttribute("loggedInAdmin", admin);
			return "redirect:/insuranceadmin/dashboard";
		}
		model.addAttribute("error", "Invalid Username or Password");
		return "admin/login";
	}
	
	@GetMapping("/dashboard")
	public String dashboard(Model model,HttpSession session) {
		if(session.getAttribute("loggedInAdmin")==null) return "redirect:/insuranceadmin/login";
		List<Claim> submittedClaims = insuranceAdminService.getSubmittedClaims();
		model.addAttribute("claims",submittedClaims);
		return "admin/dashboard";
	}
	@GetMapping("/claim/{id}")
	public String viewClaim(@PathVariable Long id,Model model, HttpSession session) {
		if(session.getAttribute("loggedInAdmin")==null)
			return "redirect:/insuranceadmin/login";
		Claim claim = insuranceAdminService.getClaimById(id);
		model.addAttribute("claim", claim);
		model.addAttribute("policy", claim.getPolicy());
		return "admin/viewclaim";
	}
	
	@PostMapping("/claim/{id}/update")
	public String updateClaim(@PathVariable Long id, @RequestParam String status, @RequestParam String reason) {
		insuranceAdminService.updateClaimStatus(id, status, reason);
		return "redirect:/insuranceadmin/dashboard";
	}
	
	@GetMapping("/search")
	public String searchCustomer(@RequestParam String username, Model model, HttpSession session) {
	    if (session.getAttribute("loggedInAdmin") == null)
	        return "redirect:/insuranceadmin/login";
	    
	    Optional<Customer> customerOpt = insuranceAdminService.findCustomerByName(username);
	    if (customerOpt.isPresent()) {
	        Customer customer = customerOpt.get();
	        List<Policy> policies = insuranceAdminService.getPoliciesByCustomer(customer);
	        List<Claim> claimss = insuranceAdminService.getClaimsByCustomer(customer);
	 
	        model.addAttribute("customer", customer);
	        model.addAttribute("policies", policies);
	        model.addAttribute("claimss", claimss);
	    } else {
	        model.addAttribute("error", "Customer not found");
	    }
	 
	    List<Claim> submittedClaims = insuranceAdminService.getSubmittedClaims();
	    model.addAttribute("claims", submittedClaims);
	    return "admin/dashboard";
	}
	 
	@GetMapping("/policy/{policyId}/report")
	public String showReportDownloadPage(@PathVariable Long policyId, Model model, HttpSession session) {
	    if (session.getAttribute("loggedInAdmin") == null)
	        return "redirect:/insuranceadmin/login";
	 
	    model.addAttribute("policyId", policyId);
	    return "admin/downloadreport";
	}
	
	@GetMapping("/customers")
	public String showCustomers(Model model, HttpSession session) {
	    if (session.getAttribute("loggedInAdmin") == null)
	        return "redirect:/insuranceadmin/login";
	 
	    List<Customer> customers = insuranceAdminService.getAllCustomers();
	    model.addAttribute("customers", customers);
	    return "admin/customers";  // create this Thymeleaf page
	}
	
	@GetMapping("/customers/{customerId}")
	public String viewCustomerDetails(@PathVariable Long customerId, Model model, HttpSession session) {
	    if (session.getAttribute("loggedInAdmin") == null)
	        return "redirect:/insuranceadmin/login";
	 
	    List<Policy> policies = policyService.getPoliciesForCustomer(customerId);
	    List<Claim> claims = claimService.getClaimsByCustomer(customerId);
	    Customer customer = customerService.findById(customerId);
	    model.addAttribute("policies", policies);
	    model.addAttribute("claims", claims);
	    model.addAttribute("customer", customer);
	    return "admin/customer-details";  // create this view
	}
	@GetMapping("/claim/{claimId}/report")
	public String showReportDownloadPageClaim(@PathVariable Long claimId, Model model, HttpSession session) 
	{    
		if (session.getAttribute("loggedInAdmin") == null)       
			return "redirect:/insuranceadmin/login";    
		model.addAttribute("claimId", claimId);    
		return "admin/downloadClaim";
	}
	
}
