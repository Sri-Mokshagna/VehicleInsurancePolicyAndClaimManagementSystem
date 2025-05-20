package com.project.VehicleInsurancePolicyAndClaim.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.VehicleInsurancePolicyAndClaim.model.Claim;
import com.project.VehicleInsurancePolicyAndClaim.model.InsuranceAdmin;
import com.project.VehicleInsurancePolicyAndClaim.model.Policy;
import com.project.VehicleInsurancePolicyAndClaim.repository.ClaimRepository;
import com.project.VehicleInsurancePolicyAndClaim.repository.PolicyRepository;
import com.project.VehicleInsurancePolicyAndClaim.service.InsuranceAdminService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/insuranceadmin")
public class InsuranceAdminController {
 
//    @Autowired
//    private InsuranceAdminService adminService;
// 
//    @Autowired
//    private ClaimRepository claimRepository;
// 
//    @Autowired
//    private PolicyRepository policyRepository;
 
    @GetMapping("/login")
    public String showLoginForm() {
        return "insuranceadmin/login";
    }
 
//    @PostMapping("/login")
//    public String processLogin(@RequestParam String username,
//                                @RequestParam String password,
//                                HttpSession session,
//                                Model model) {
//        //Optional<InsuranceAdmin> admin = adminService.authenticate(username, password);
//        if (admin.isPresent()) {
//            session.setAttribute("admin", admin.get());
//            return "redirect:/insuranceadmin/dashboard";
//        } else {
//           model.addAttribute("error", "Invalid Credentials");
//           return "insuranceadmin/login";
//        }
//    }
 
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/insuranceadmin/login";
 
        //List<Claim> pendingClaims = claimRepository.findByStatus("PENDING");
        //model.addAttribute("claims", pendingClaims);
        return "insuranceadmin/dashboard";
    }
 
    @PostMapping("/createPolicy")
    public String createPolicy(@ModelAttribute Policy policy) {
        //policyRepository.save(policy);
        return "redirect:/insuranceadmin/dashboard";
    }
 
    @PostMapping("/updateClaimStatus")
    public String updateClaimStatus(@RequestParam Long claimId, @RequestParam String status) {
//        Claim claim = claimRepository.findById(claimId).orElseThrow();
//        claim.setStatus(status);
//        claimRepository.save(claim);
        return "redirect:/insuranceadmin/dashboard";
    }
 
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/insuranceadmin/login";
    }
}