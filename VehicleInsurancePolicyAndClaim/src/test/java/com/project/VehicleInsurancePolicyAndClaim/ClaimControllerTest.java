package com.project.VehicleInsurancePolicyAndClaim;

import com.project.VehicleInsurancePolicyAndClaim.controller.ClaimController;
import com.project.VehicleInsurancePolicyAndClaim.model.Claim;
import com.project.VehicleInsurancePolicyAndClaim.model.Customer;
import com.project.VehicleInsurancePolicyAndClaim.model.Policy;
import com.project.VehicleInsurancePolicyAndClaim.model.Vehicle;
import com.project.VehicleInsurancePolicyAndClaim.service.ClaimService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ClaimController.class) 
public class ClaimControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClaimService claimService; 

    @MockBean
    private PolicyService policyService;

    @MockBean
    private VehicleService vehicleService; 

    @Mock
    private HttpSession session;

    @InjectMocks
    private ClaimController claimController; 

    private Customer testCustomer;


    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setCustomerId(1);
        testCustomer.setEmail("test@example.com");
        when(session.getAttribute("loggedInCustomer")).thenReturn(testCustomer);
    }

    @Test
    void viewClaims_loggedInCustomer() throws Exception {
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setVehicleId(101L);
        vehicle1.setRegistrationNumber("VEH123");

        Policy policy1 = new Policy();
        policy1.setPolicyId(1L);
        policy1.setVehicle(vehicle1);

        Claim claim1 = new Claim();
        claim1.setClaimId(1L);
        claim1.setPolicy(policy1); 

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setVehicleId(102L);
        vehicle2.setRegistrationNumber("VEH456");

        Policy policy2 = new Policy();
        policy2.setPolicyId(2L);
        policy2.setVehicle(vehicle2);

        Claim claim2 = new Claim();
        claim2.setClaimId(2L);
        claim2.setPolicy(policy2);

        List<Claim> claims = Arrays.asList(claim1, claim2);
        List<Vehicle> vehicles = Arrays.asList(vehicle1, vehicle2); // Ensure vehicles are also passed to the model

        when(claimService.getClaimsByCustomer(testCustomer)).thenReturn(claims);
        when(vehicleService.getVehiclesByCustomer(testCustomer)).thenReturn(vehicles);

        mockMvc.perform(get("/claims").sessionAttr("loggedInCustomer", testCustomer))
                .andExpect(status().isOk()) 
                .andExpect(view().name("claim/claims")) 
                .andExpect(model().attributeExists("claims"))
                .andExpect(model().attribute("claims", claims))
                .andExpect(model().attributeExists("vehicles"))
                .andExpect(model().attribute("vehicles", vehicles)); 

        verify(claimService, times(1)).getClaimsByCustomer(testCustomer);
        verify(vehicleService, times(1)).getVehiclesByCustomer(testCustomer);
    }

    @Test
    void viewClaims_notLoggedInCustomer() throws Exception {
        when(session.getAttribute("loggedInCustomer")).thenReturn(null);

        mockMvc.perform(get("/claims"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login")); 

        verify(claimService, never()).getClaimsByCustomer(any(Customer.class));
        verify(vehicleService, never()).getVehiclesByCustomer(any(Customer.class));
    }


    @Test
    void fileClaim_success() throws Exception {
        MockMultipartFile mockImage = new MockMultipartFile(
                "image", "test-image.jpg", "image/jpeg", "some image content".getBytes());

        Claim claimToFile = new Claim();
        claimToFile.setPolicy(new Policy()); 
        doNothing().when(claimService).fileClaim(any(Claim.class), any(MockMultipartFile.class));

        mockMvc.perform(multipart("/createclaim")
                        .file(mockImage) 
                        .param("claimDetails", "Accident on highway")
                        .sessionAttr("loggedInCustomer", testCustomer))
                .andExpect(status().is3xxRedirection()) 
                .andExpect(redirectedUrl("/claims"));

        verify(claimService, times(1)).fileClaim(any(Claim.class), any(MockMultipartFile.class));
    }

}
