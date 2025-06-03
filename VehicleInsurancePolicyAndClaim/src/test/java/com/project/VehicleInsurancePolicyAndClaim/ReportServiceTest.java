package com.project.VehicleInsurancePolicyAndClaim;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.itextpdf.text.DocumentException;
import com.project.VehicleInsurancePolicyAndClaim.model.Claim;
import com.project.VehicleInsurancePolicyAndClaim.model.Customer;
import com.project.VehicleInsurancePolicyAndClaim.model.Policy;
import com.project.VehicleInsurancePolicyAndClaim.model.Vehicle;
import com.project.VehicleInsurancePolicyAndClaim.repository.ClaimRepository;
import com.project.VehicleInsurancePolicyAndClaim.repository.PolicyRepository;
import com.project.VehicleInsurancePolicyAndClaim.service.ClaimExcelGenerator;
import com.project.VehicleInsurancePolicyAndClaim.service.ClaimPdfGenerator;
import com.project.VehicleInsurancePolicyAndClaim.service.PolicyExcelGenerator;
import com.project.VehicleInsurancePolicyAndClaim.service.PolicyPdfGenerator;
import com.project.VehicleInsurancePolicyAndClaim.service.ReportService;

import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private ReportService reportService;

    private Customer customer1;
    private Customer customer2;
    private Vehicle vehicle1;
    private Vehicle vehicle2;
    private Policy policy1;
    private Policy policy2;
    private Claim claim1;
    private Claim claim2;

    @BeforeEach
    void setUp() {
        customer1 = new Customer();
        customer1.setCustomerId(1);
        customer1.setEmail("customer1@example.com");

        customer2 = new Customer();
        customer2.setCustomerId(2);
        customer2.setEmail("customer2@example.com");

        vehicle1 = new Vehicle();
        vehicle1.setVehicleId(101L);
        vehicle1.setCustomer(customer1);

        vehicle2 = new Vehicle();
        vehicle2.setVehicleId(102L);
        vehicle2.setCustomer(customer2);

        policy1 = new Policy();
        policy1.setPolicyId(1L);
        policy1.setPolicyNumber("POL001");
        policy1.setVehicle(vehicle1);

        policy2 = new Policy();
        policy2.setPolicyId(2L);
        policy2.setPolicyNumber("POL002");
        policy2.setVehicle(vehicle2);

        claim1 = new Claim();
        claim1.setClaimId(1L);
        claim1.setPolicy(policy1);

        claim2 = new Claim();
        claim2.setClaimId(2L);
        claim2.setPolicy(policy2);
    }

    @Test
    void testGetPoliciesByCustomer() {
        when(policyRepository.findAll()).thenReturn(Arrays.asList(policy1, policy2));

        List<Policy> policies = reportService.getPoliciesByCustomer(1);

        assertNotNull(policies);
        assertEquals(1, policies.size());
        assertEquals(policy1.getPolicyId(), policies.get(0).getPolicyId());
        verify(policyRepository, times(1)).findAll();
    }

    @Test
    void testGetClaimsByCustomer() {
        when(claimRepository.findAll()).thenReturn(Arrays.asList(claim1, claim2));

        List<Claim> claims = reportService.getClaimsByCustomer(1);

        assertNotNull(claims);
        assertEquals(1, claims.size());
        assertEquals(claim1.getClaimId(), claims.get(0).getClaimId());
        verify(claimRepository, times(1)).findAll();
    }

    @Test
    void testGeneratePolicyPdf() throws IOException, DocumentException {
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy1));

        try (MockedStatic<PolicyPdfGenerator> mockedPdfGenerator = mockStatic(PolicyPdfGenerator.class)) {
            reportService.generatePolicyPdf(1L, response);

            verify(response, times(1)).setContentType("application/pdf");
            verify(response, times(1)).setHeader("Content-Disposition", "attachment; filename=policy_report_1.pdf");
            mockedPdfGenerator.verify(() -> PolicyPdfGenerator.generate(response, policy1), times(1));
            verify(policyRepository, times(1)).findById(1L);
        }
    }

    @Test
    void testGeneratePolicyPdf_PolicyNotFound() {
        when(policyRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reportService.generatePolicyPdf(99L, response));
        verify(policyRepository, times(1)).findById(99L);
        verify(response, never()).setContentType(anyString()); // Ensure no response actions if policy not found
    }

    @Test
    void testGeneratePolicyExcel() throws IOException {
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy1));

        try (MockedStatic<PolicyExcelGenerator> mockedExcelGenerator = mockStatic(PolicyExcelGenerator.class)) {
            reportService.generatePolicyExcel(1L, response);

            verify(response, times(1)).setContentType("application/octet-stream");
            verify(response, times(1)).setHeader("Content-Disposition", "attachment; filename=policy_report_1.xlsx");
            mockedExcelGenerator.verify(() -> PolicyExcelGenerator.generate(response, policy1), times(1));
            verify(policyRepository, times(1)).findById(1L);
        }
    }

    @Test
    void testGeneratePolicyExcel_PolicyNotFound() {
        when(policyRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reportService.generatePolicyExcel(99L, response));
        verify(policyRepository, times(1)).findById(99L);
        verify(response, never()).setContentType(anyString());
    }

    @Test
    void testGenerateClaimPdf() throws IOException, DocumentException {
        when(claimRepository.findById(1L)).thenReturn(Optional.of(claim1));

        try (MockedStatic<ClaimPdfGenerator> mockedPdfGenerator = mockStatic(ClaimPdfGenerator.class)) {
            reportService.generateClaimPdf(1L, response);

            verify(response, times(1)).setContentType("application/pdf");
            verify(response, times(1)).setHeader("Content-Disposition", "attachment; filename=claim_report_1.pdf");
            mockedPdfGenerator.verify(() -> ClaimPdfGenerator.generate(response, claim1), times(1));
            verify(claimRepository, times(1)).findById(1L);
        }
    }

    @Test
    void testGenerateClaimPdf_ClaimNotFound() {
        when(claimRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reportService.generateClaimPdf(99L, response));
        verify(claimRepository, times(1)).findById(99L);
        verify(response, never()).setContentType(anyString());
    }

    @Test
    void testGenerateClaimExcel() throws IOException {
        when(claimRepository.findById(1L)).thenReturn(Optional.of(claim1));

        try (MockedStatic<ClaimExcelGenerator> mockedExcelGenerator = mockStatic(ClaimExcelGenerator.class)) {
            reportService.generateClaimExcel(1L, response);

            verify(response, times(1)).setContentType("application/octet-stream");
            verify(response, times(1)).setHeader("Content-Disposition", "attachment; filename=claim_report_1.xlsx");
            mockedExcelGenerator.verify(() -> ClaimExcelGenerator.generate(response, claim1), times(1));
            verify(claimRepository, times(1)).findById(1L);
        }
    }

    @Test
    void testGenerateClaimExcel_ClaimNotFound() {
        when(claimRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reportService.generateClaimExcel(99L, response));
        verify(claimRepository, times(1)).findById(99L);
        verify(response, never()).setContentType(anyString());
    }
}