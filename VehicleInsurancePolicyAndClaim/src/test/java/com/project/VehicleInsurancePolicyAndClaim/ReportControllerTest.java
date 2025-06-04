package com.project.VehicleInsurancePolicyAndClaim;

import com.project.VehicleInsurancePolicyAndClaim.controller.ReportController;
import com.project.VehicleInsurancePolicyAndClaim.model.Claim;
import com.project.VehicleInsurancePolicyAndClaim.model.Customer;
import com.project.VehicleInsurancePolicyAndClaim.model.Policy;
import com.project.VehicleInsurancePolicyAndClaim.service.CustomerService;
import com.project.VehicleInsurancePolicyAndClaim.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult; // Import MvcResult

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals; // Import assertEquals

@WebMvcTest(ReportController.class)
public class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @MockBean
    private CustomerService customerService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private ReportController reportController;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setCustomerId(1);
        testCustomer.setEmail("test@example.com");

        when(session.getAttribute("loggedInCustomer")).thenReturn(testCustomer);
    }

    @Test
    void selectReportType() throws Exception {
        mockMvc.perform(get("/reports"))
                .andExpect(status().isOk())
                .andExpect(view().name("report/select-report"));
    }

    @Test
    void listPolicies_loggedInCustomer() throws Exception {
        Policy policy1 = new Policy();
        policy1.setPolicyId(1L);
        Policy policy2 = new Policy();
        policy2.setPolicyId(2L);
        List<Policy> policies = Arrays.asList(policy1, policy2);

        when(reportService.getPoliciesByCustomer(testCustomer.getCustomerId())).thenReturn(policies);

        mockMvc.perform(get("/reports/policy").sessionAttr("loggedInCustomer", testCustomer))
                .andExpect(status().isOk())
                .andExpect(view().name("report/policy-list"))
                .andExpect(model().attributeExists("policies"))
                .andExpect(model().attribute("policies", policies));

        verify(reportService, times(1)).getPoliciesByCustomer(testCustomer.getCustomerId());
    }

    
    @Test
    void listClaims_loggedInCustomer() throws Exception {
        Claim claim1 = new Claim();
        claim1.setClaimId(1L);
        Claim claim2 = new Claim();
        claim2.setClaimId(2L);
        List<Claim> claims = Arrays.asList(claim1, claim2);

        when(reportService.getClaimsByCustomer(testCustomer.getCustomerId())).thenReturn(claims);

        mockMvc.perform(get("/reports/claim").sessionAttr("loggedInCustomer", testCustomer))
                .andExpect(status().isOk())
                .andExpect(view().name("report/claim-list"))
                .andExpect(model().attributeExists("claims"))
                .andExpect(model().attribute("claims", claims));

        verify(reportService, times(1)).getClaimsByCustomer(testCustomer.getCustomerId());
    }

    
    @Test
    void showPolicyReport() throws Exception {
        Long policyId = 123L;
        mockMvc.perform(get("/reports/policy/{id}", policyId))
                .andExpect(status().isOk())
                .andExpect(view().name("report/policy-report"))
                .andExpect(model().attributeExists("policyId"))
                .andExpect(model().attribute("policyId", policyId));
    }

    @Test
    void showClaimReport() throws Exception {
        Long claimId = 456L;
        mockMvc.perform(get("/reports/claim/{id}", claimId))
                .andExpect(status().isOk())
                .andExpect(view().name("report/claim-report"))
                .andExpect(model().attributeExists("claimId"))
                .andExpect(model().attribute("claimId", claimId));
    }

    @Test
    void downloadPolicyPdf() throws Exception {
        Long policyId = 123L;

        // Use doAnswer to simulate the service setting headers on the HttpServletResponse
        doAnswer(invocation -> {
            HttpServletResponse response = invocation.getArgument(1);
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"policy_" + policyId + ".pdf\"");
            return null; // Void method, so return null
        }).when(reportService).generatePolicyPdf(eq(policyId), any(HttpServletResponse.class));

        MvcResult result = mockMvc.perform(get("/reports/policy/{id}/pdf", policyId))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("application/pdf", result.getResponse().getContentType());
        assertEquals("attachment; filename=\"policy_" + policyId + ".pdf\"", result.getResponse().getHeader("Content-Disposition"));

        verify(reportService, times(1)).generatePolicyPdf(eq(policyId), any(HttpServletResponse.class));
    }

    
    @Test
    void downloadClaimPdf() throws Exception {
        Long claimId = 456L;

        // Use doAnswer to simulate the service setting headers on the HttpServletResponse
        doAnswer(invocation -> {
            HttpServletResponse response = invocation.getArgument(1);
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"claim_" + claimId + ".pdf\"");
            return null; // Void method, so return null
        }).when(reportService).generateClaimPdf(eq(claimId), any(HttpServletResponse.class));

        MvcResult result = mockMvc.perform(get("/reports/claim/{id}/pdf", claimId))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("application/pdf", result.getResponse().getContentType());
        assertEquals("attachment; filename=\"claim_" + claimId + ".pdf\"", result.getResponse().getHeader("Content-Disposition"));

        verify(reportService, times(1)).generateClaimPdf(eq(claimId), any(HttpServletResponse.class));
    }

    @Test
    void downloadClaimExcel() throws Exception {
        Long claimId = 456L;

        doAnswer(invocation -> {
            HttpServletResponse response = invocation.getArgument(1);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"claim_" + claimId + ".xlsx\"");
            return null; // Void method, so return null
        }).when(reportService).generateClaimExcel(eq(claimId), any(HttpServletResponse.class));

        MvcResult result = mockMvc.perform(get("/reports/claim/{id}/excel", claimId))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", result.getResponse().getContentType());
        assertEquals("attachment; filename=\"claim_" + claimId + ".xlsx\"", result.getResponse().getHeader("Content-Disposition"));

        verify(reportService, times(1)).generateClaimExcel(eq(claimId), any(HttpServletResponse.class));
    }
    @Test
    void downloadPolicyExcel() throws Exception {
        Long policyId = 123L;

        // Use doAnswer to simulate the service setting headers on the HttpServletResponse
        doAnswer(invocation -> {
            HttpServletResponse response = invocation.getArgument(1);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"policy_" + policyId + ".xlsx\"");
            return null; // Void method, so return null
        }).when(reportService).generatePolicyExcel(eq(policyId), any(HttpServletResponse.class));

        MvcResult result = mockMvc.perform(get("/reports/policy/{id}/excel", policyId))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", result.getResponse().getContentType());
        assertEquals("attachment; filename=\"policy_" + policyId + ".xlsx\"", result.getResponse().getHeader("Content-Disposition"));

        verify(reportService, times(1)).generatePolicyExcel(eq(policyId), any(HttpServletResponse.class));
    }
}
