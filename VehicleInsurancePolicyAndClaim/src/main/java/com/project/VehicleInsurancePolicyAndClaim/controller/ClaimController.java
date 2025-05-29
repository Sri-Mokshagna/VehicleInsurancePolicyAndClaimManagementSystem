package com.project.VehicleInsurancePolicyAndClaim.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.project.VehicleInsurancePolicyAndClaim.model.Claim;
import com.project.VehicleInsurancePolicyAndClaim.model.Customer;
import com.project.VehicleInsurancePolicyAndClaim.model.Policy;
import com.project.VehicleInsurancePolicyAndClaim.model.Vehicle;
import com.project.VehicleInsurancePolicyAndClaim.service.ClaimService;
import com.project.VehicleInsurancePolicyAndClaim.service.PolicyService;
import com.project.VehicleInsurancePolicyAndClaim.service.VehicleService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ClaimController {
 
    @Autowired
    private ClaimService claimService;
 
    @Autowired
    private PolicyService policyService;
    
    @Autowired
    private VehicleService vehicleService;
 
    @GetMapping("/claims")
    public String viewClaims(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) return "redirect:/login";
        List<Claim> claims = claimService.getClaimsByCustomer(customer);
        List<Vehicle> vehicles = vehicleService.getVehiclesByCustomer(customer);
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("claims", claims);
        return "claim/claims";
    }
 
    @GetMapping("/createclaim")
    public String showClaimForm(Model model, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        List<Policy> policies = policyService.getPoliciesForCustomer(customer);
        model.addAttribute("policies", policies);
        model.addAttribute("claim", new Claim());
        return "claim/createclaim";
    }
 
    @PostMapping("/createclaim")
    public String fileClaim(@ModelAttribute Claim claim,
                            @RequestParam("image") MultipartFile image,
                            HttpSession session) throws IOException {
        claimService.fileClaim(claim, image);
        return "redirect:/claims";
    }
}