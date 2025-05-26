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
import com.project.VehicleInsurancePolicyAndClaim.model.InsuranceAdmin;
import com.project.VehicleInsurancePolicyAndClaim.service.InsuranceAdminService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/insuranceadmin")
public class InsuranceAdminController {
	@Autowired
	private InsuranceAdminService insuranceAdminService;
	
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
}
