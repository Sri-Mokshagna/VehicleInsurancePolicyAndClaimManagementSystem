package com.project.VehicleInsurancePolicyAndClaim.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.DocumentException;
import com.project.VehicleInsurancePolicyAndClaim.model.Claim;
import com.project.VehicleInsurancePolicyAndClaim.model.Policy;
import com.project.VehicleInsurancePolicyAndClaim.repository.ClaimRepository;
import com.project.VehicleInsurancePolicyAndClaim.repository.PolicyRepository;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class ReportService {
 
    @Autowired
    private PolicyRepository policyRepository;
 
    @Autowired
    private ClaimRepository claimRepository;
 
    public List<Policy> getPoliciesByCustomer(int customerId) {
        return policyRepository.findAll()
                .stream()
                .filter(p -> p.getVehicle().getCustomer().getCustomerId() == customerId)
                .collect(Collectors.toList());
    }
 
    public List<Claim> getClaimsByCustomer(int customerId) {
        return claimRepository.findAll()
                .stream()
                .filter(c -> c.getPolicy().getVehicle().getCustomer().getCustomerId() == customerId)
                .collect(Collectors.toList());
    }
 
    public void generatePolicyPdf(Long policyId, HttpServletResponse response) throws IOException, DocumentException {
        Policy policy = policyRepository.findById(policyId).orElseThrow();
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=policy_report_" + policyId + ".pdf");
 
        PolicyPdfGenerator.generate(response, policy);
    }
 
    public void generatePolicyExcel(Long policyId, HttpServletResponse response) throws IOException {
        Policy policy = policyRepository.findById(policyId).orElseThrow();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=policy_report_" + policyId + ".xlsx");
 
        PolicyExcelGenerator.generate(response, policy);
    }
 
    public void generateClaimPdf(Long claimId, HttpServletResponse response) throws IOException, DocumentException {
        Claim claim = claimRepository.findById(claimId).orElseThrow();
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=claim_report_" + claimId + ".pdf");
 
        ClaimPdfGenerator.generate(response, claim);
    }
 
    public void generateClaimExcel(Long claimId, HttpServletResponse response) throws IOException {
        Claim claim = claimRepository.findById(claimId).orElseThrow();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=claim_report_" + claimId + ".xlsx");
 
        ClaimExcelGenerator.generate(response, claim);
    }
}