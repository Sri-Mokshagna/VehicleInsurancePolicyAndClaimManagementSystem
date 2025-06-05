package com.project.VehicleInsurancePolicyAndClaim.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.DocumentException;
import com.project.VehicleInsurancePolicyAndClaim.model.Claim;
import com.project.VehicleInsurancePolicyAndClaim.model.Customer;
import com.project.VehicleInsurancePolicyAndClaim.model.Policy;
import com.project.VehicleInsurancePolicyAndClaim.service.CustomerService;
import com.project.VehicleInsurancePolicyAndClaim.service.ReportService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/reports")
public class ReportController {
 
    @Autowired
    private ReportService reportService;
 
    @Autowired
    private CustomerService customerService;
 
    @GetMapping
    public String selectReportType() {
        return "report/select-report";
    }
 
    @GetMapping("/policy")
    public String listPolicies(Model model, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        List<Policy> policies = reportService.getPoliciesByCustomer(customer.getCustomerId());
        model.addAttribute("policies", policies);
        return "report/policy-list";
    }
 
    @GetMapping("/claim")
    public String listClaims(Model model, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        List<Claim> claims = reportService.getClaimsByCustomer(customer.getCustomerId());
        List<Policy> policies = reportService.getPoliciesByCustomer(customer.getCustomerId());
        model.addAttribute("policies", policies);
        model.addAttribute("claims", claims);
        return "report/claim-list";
    }
 
    @GetMapping("/policy/{id}")
    public String showPolicyReport(@PathVariable Long id, Model model) {
        model.addAttribute("policyId", id);
        return "report/policy-report";
    }
 
    @GetMapping("/claim/{id}")
    public String showClaimReport(@PathVariable Long id, Model model) {
        model.addAttribute("claimId", id);
        return "report/claim-report";
    }
 
    @GetMapping("/policy/{id}/pdf")
    public void downloadPolicyPdf(@PathVariable Long id, HttpServletResponse response) throws IOException, DocumentException {
        reportService.generatePolicyPdf(id, response);
    }
 
    @GetMapping("/policy/{id}/excel")
    public void downloadPolicyExcel(@PathVariable Long id, HttpServletResponse response) throws IOException {
        reportService.generatePolicyExcel(id, response);
    }
 
    @GetMapping("/claim/{id}/pdf")
    public void downloadClaimPdf(@PathVariable Long id, HttpServletResponse response) throws IOException, DocumentException {
        reportService.generateClaimPdf(id, response);
    }
 
    @GetMapping("/claim/{id}/excel")
    public void downloadClaimExcel(@PathVariable Long id, HttpServletResponse response) throws IOException {
        reportService.generateClaimExcel(id, response);
    }
}
 