package com.project.VehicleInsurancePolicyAndClaim;

import com.project.VehicleInsurancePolicyAndClaim.controller.PolicyController;
import com.project.VehicleInsurancePolicyAndClaim.model.Customer;
import com.project.VehicleInsurancePolicyAndClaim.model.Policy;
import com.project.VehicleInsurancePolicyAndClaim.model.Vehicle;
import com.project.VehicleInsurancePolicyAndClaim.service.PolicyService;
import com.project.VehicleInsurancePolicyAndClaim.service.VehicleService;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@WebMvcTest(PolicyController.class) 
public class PolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PolicyService policyService; 

    @MockBean
    private VehicleService vehicleService;

    @Mock
    private HttpSession session; 

    @InjectMocks
    private PolicyController policyController; 

    private Customer testCustomer; 
    private Customer anotherCustomer;
    private Vehicle testVehicle;
    private Policy testPolicy; 


    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setCustomerId(1);
        testCustomer.setEmail("test@example.com");


        anotherCustomer = new Customer();
        anotherCustomer.setCustomerId(2);
        anotherCustomer.setEmail("another@example.com");

        testVehicle = new Vehicle();
        testVehicle.setVehicleId(101L);
        testVehicle.setRegistrationNumber("ABC-123");
        testVehicle.setCustomer(testCustomer); 
        testPolicy = new Policy();
        testPolicy.setPolicyId(1L);
        testPolicy.setPolicyStatus("ACTIVE");
        testPolicy.setStartDate(LocalDate.now().minusMonths(6));
        testPolicy.setEndDate(LocalDate.now().plusMonths(6));
        testPolicy.setVehicle(testVehicle); 
        when(session.getAttribute("loggedInCustomer")).thenReturn(testCustomer);
    }

    


    

    @Test
    void buyNewPolicyForm_loggedInCustomer() throws Exception {
        Vehicle vehicleWithoutPolicy = new Vehicle();
        vehicleWithoutPolicy.setVehicleId(201L);
        vehicleWithoutPolicy.setRegistrationNumber("XYZ-789");
        List<Vehicle> vehiclesWithoutPolicy = Collections.singletonList(vehicleWithoutPolicy);

        when(vehicleService.getVehicleWithoutPolicy(testCustomer)).thenReturn(vehiclesWithoutPolicy);

        mockMvc.perform(get("/buynewpolicy").sessionAttr("loggedInCustomer", testCustomer))
                .andExpect(status().isOk())
                .andExpect(view().name("policy/buynewpolicy")) 
                .andExpect(model().attributeExists("vehicles"))
                .andExpect(model().attribute("vehicles", vehiclesWithoutPolicy));
        verify(vehicleService, times(1)).getVehicleWithoutPolicy(testCustomer);
    }

    


    @Test
    void previewPolicy_success() throws Exception {
        String coverageType = "Comprehensive";
        Policy calculatedPolicy = new Policy();
        calculatedPolicy.setPolicyId(99L);
        calculatedPolicy.setVehicle(testVehicle); 
        when(vehicleService.getVehicleById(testVehicle.getVehicleId())).thenReturn(testVehicle);
        when(policyService.calculatePolicy(testVehicle, coverageType)).thenReturn(calculatedPolicy);

        mockMvc.perform(post("/buynewpolicy/preview")
                        .param("vehicleId", String.valueOf(testVehicle.getVehicleId()))
                        .param("coverageType", coverageType))
                .andExpect(status().isOk()) 
                .andExpect(view().name("policy/previewpolicy")) 
                .andExpect(model().attributeExists("policy"))
                .andExpect(model().attribute("policy", calculatedPolicy))
                .andExpect(model().attributeExists("vehicle"))
                .andExpect(model().attribute("vehicle", testVehicle));

        verify(vehicleService, times(1)).getVehicleById(testVehicle.getVehicleId());
        verify(policyService, times(1)).calculatePolicy(testVehicle, coverageType);
    }

    @Test
    void previewPolicy_invalidVehicleId() throws Exception {
        String coverageType = "Comprehensive";
        Long invalidVehicleId = 999L;

        when(vehicleService.getVehicleById(invalidVehicleId)).thenReturn(null);

        mockMvc.perform(post("/buynewpolicy/preview")
                        .param("vehicleId", String.valueOf(invalidVehicleId))
                        .param("coverageType", coverageType))
                .andExpect(status().is3xxRedirection()) 
                .andExpect(redirectedUrl("/buynewpolicy")); 
        verify(vehicleService, times(1)).getVehicleById(invalidVehicleId);
        verify(policyService, never()).calculatePolicy(any(Vehicle.class), anyString());
    }


    @Test
    void confirmPolicy_success() throws Exception {
        String coverageType = "Comprehensive";
        Policy calculatedPolicy = new Policy();
        calculatedPolicy.setPolicyId(99L);
        calculatedPolicy.setVehicle(testVehicle);

        when(vehicleService.getVehicleById(testVehicle.getVehicleId())).thenReturn(testVehicle);
        when(policyService.calculatePolicy(testVehicle, coverageType)).thenReturn(calculatedPolicy);
        doNothing().when(policyService).savePolicy(any(Policy.class)); 

        mockMvc.perform(post("/buynewpolicy/confirm")
                        .param("vehicleId", String.valueOf(testVehicle.getVehicleId()))
                        .param("coverageType", coverageType))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/policies"));
        verify(vehicleService, times(1)).getVehicleById(testVehicle.getVehicleId());
        verify(policyService, times(1)).calculatePolicy(testVehicle, coverageType);
        verify(policyService, times(1)).savePolicy(calculatedPolicy);
    }


    @Test
    void confirmPolicy_invalidVehicleId() throws Exception {
        String coverageType = "Comprehensive";
        Long invalidVehicleId = 999L;

        when(vehicleService.getVehicleById(invalidVehicleId)).thenReturn(null);

        mockMvc.perform(post("/buynewpolicy/confirm")
                        .param("vehicleId", String.valueOf(invalidVehicleId))
                        .param("coverageType", coverageType))
                .andExpect(status().is3xxRedirection()) 
                .andExpect(redirectedUrl("/buynewpolicy")); 
        verify(vehicleService, times(1)).getVehicleById(invalidVehicleId);
        verify(policyService, never()).calculatePolicy(any(Vehicle.class), anyString());
        verify(policyService, never()).savePolicy(any(Policy.class));
    }


    @Test
    void viewPolicy_loggedInCustomer_unauthorized() throws Exception {
        testPolicy.getVehicle().setCustomer(anotherCustomer);

        when(policyService.getPolicyById(testPolicy.getPolicyId())).thenReturn(testPolicy);

        mockMvc.perform(get("/viewpolicy/{id}", testPolicy.getPolicyId())
                        .sessionAttr("loggedInCustomer", testCustomer))
                .andExpect(status().is3xxRedirection()) 
                .andExpect(redirectedUrl("/policies"));

        verify(policyService, times(1)).getPolicyById(testPolicy.getPolicyId());
    }


    @Test
    void viewPolicy_notLoggedInCustomer() throws Exception {
        MockHttpSession mockSession = new MockHttpSession();

        mockMvc.perform(get("/viewpolicy/{id}", testPolicy.getPolicyId())
                        .session(mockSession))
                .andExpect(status().is3xxRedirection()) 
                .andExpect(redirectedUrl("/login")); 

        verify(policyService, never()).getPolicyById(anyLong());
    }

    @Test
    void renewPolicy_loggedInCustomer_authorized_expiredPolicy() throws Exception {
        testPolicy.setPolicyStatus("EXPIRED");
        testPolicy.getVehicle().setCustomer(testCustomer);

        when(policyService.getPolicyById(testPolicy.getPolicyId())).thenReturn(testPolicy);
        doNothing().when(policyService).renewPolicy(any(Policy.class));

        mockMvc.perform(post("/renewpolicy/{id}", testPolicy.getPolicyId())
                        .sessionAttr("loggedInCustomer", testCustomer))
                .andExpect(status().is3xxRedirection()) // Expect HTTP 3xx Redirection
                .andExpect(redirectedUrl("/policies")); // Expect redirect to "/policies"

        // Verify service methods were called
        verify(policyService, times(1)).getPolicyById(testPolicy.getPolicyId());
        verify(policyService, times(1)).renewPolicy(testPolicy);
    }


    @Test
    void renewPolicy_loggedInCustomer_authorized_activePolicy() throws Exception {
        // Set policy to active and owned by testCustomer
        testPolicy.setPolicyStatus("ACTIVE");
        testPolicy.getVehicle().setCustomer(testCustomer);

        // Define mock behavior for service calls
        when(policyService.getPolicyById(testPolicy.getPolicyId())).thenReturn(testPolicy);

        // Perform POST request and assert redirect
        mockMvc.perform(post("/renewpolicy/{id}", testPolicy.getPolicyId())
                        .sessionAttr("loggedInCustomer", testCustomer))
                .andExpect(status().is3xxRedirection()) 
                .andExpect(redirectedUrl("/policies"));
        verify(policyService, times(1)).getPolicyById(testPolicy.getPolicyId());
        verify(policyService, never()).renewPolicy(any(Policy.class));
    }

    @Test
    void renewPolicy_loggedInCustomer_unauthorized() throws Exception {
        testPolicy.setPolicyStatus("EXPIRED");
        testPolicy.getVehicle().setCustomer(anotherCustomer);

        when(policyService.getPolicyById(testPolicy.getPolicyId())).thenReturn(testPolicy);

        mockMvc.perform(post("/renewpolicy/{id}", testPolicy.getPolicyId())
                        .sessionAttr("loggedInCustomer", testCustomer))
                .andExpect(status().is3xxRedirection()) 
                .andExpect(redirectedUrl("/policies")); 

        verify(policyService, times(1)).getPolicyById(testPolicy.getPolicyId());
        verify(policyService, never()).renewPolicy(any(Policy.class));
    }

    @Test
    void renewPolicy_notLoggedInCustomer() throws Exception {
        MockHttpSession mockSession = new MockHttpSession();

        mockMvc.perform(post("/renewpolicy/{id}", testPolicy.getPolicyId())
                        .session(mockSession))
                .andExpect(status().is3xxRedirection()) 
                .andExpect(redirectedUrl("/login")); 

        verify(policyService, never()).getPolicyById(anyLong());
        verify(policyService, never()).renewPolicy(any(Policy.class));
    }
}
