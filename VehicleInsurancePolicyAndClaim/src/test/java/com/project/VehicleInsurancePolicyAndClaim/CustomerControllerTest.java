package com.project.VehicleInsurancePolicyAndClaim;

import com.project.VehicleInsurancePolicyAndClaim.controller.CustomerController;
import com.project.VehicleInsurancePolicyAndClaim.model.Claim;
import com.project.VehicleInsurancePolicyAndClaim.model.Customer;
import com.project.VehicleInsurancePolicyAndClaim.model.Policy;
import com.project.VehicleInsurancePolicyAndClaim.model.Vehicle;
import com.project.VehicleInsurancePolicyAndClaim.service.ClaimService;
import com.project.VehicleInsurancePolicyAndClaim.service.CustomerService;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc; 

    @MockBean
    private CustomerService customerService; 

    @MockBean
    private VehicleService vehicleService; 

    @MockBean
    private PolicyService policyService; 

    @MockBean
    private ClaimService claimService; 
    @Mock
    private HttpSession session;

    @InjectMocks
    private CustomerController customerController; 

    private Customer testCustomer; 
    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setCustomerId(1);
        testCustomer.setEmail("test@example.com");
        testCustomer.setPassword("password123");

        when(session.getAttribute("loggedInCustomer")).thenReturn(testCustomer);
    }

  
    @Test
    void home() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk()) 
                .andExpect(view().name("home")); 
    }

    @Test
    void showRegistrationForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk()) 
                .andExpect(view().name("customer/register"))
                .andExpect(model().attributeExists("customer")) 
                .andDo(result -> {
                    Object customerAttribute = result.getModelAndView().getModel().get("customer");
                    assertNotNull(customerAttribute); 
                    assertTrue(customerAttribute instanceof Customer); 
                });
    }

    @Test
    void registerCustomer_success() throws Exception {
        Customer newCustomer = new Customer();
        newCustomer.setEmail("jane@example.com");
        newCustomer.setPassword("newpass");
        newCustomer.setCustomerId(2);
        when(customerService.saveCustomer(any(Customer.class))).thenReturn(newCustomer);

        mockMvc.perform(post("/register")
                        .param("phone", newCustomer.getPhone())
                        .param("address", newCustomer.getAddress())
                        .param("email", newCustomer.getEmail())
                        .param("password", newCustomer.getPassword()))
                .andExpect(status().isOk()) // Expect HTTP 200 OK (as it returns the same view)
                .andExpect(view().name("customer/register")) // Expect "customer/register" view name
                .andExpect(model().attributeExists("successMessage")) // Expect success message
                .andExpect(model().attribute("successMessage", "Customer registered successfully!"))
                .andExpect(model().attributeExists("customer")); // Expect a new customer object for the form

        verify(customerService, times(1)).saveCustomer(any(Customer.class));
    }

   
    @Test
    void loginForm() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk()) 
                .andExpect(view().name("customer/login")); 
    }

    @Test
    void processLogin_success() throws Exception {
        when(customerService.authenticate(eq(testCustomer.getEmail()), eq(testCustomer.getPassword())))
                .thenReturn(Optional.of(testCustomer));

        mockMvc.perform(post("/login")
                        .param("email", testCustomer.getEmail())
                        .param("password", testCustomer.getPassword()))
                .andExpect(status().is3xxRedirection()) 
                .andExpect(redirectedUrl("/dashboard")) 
                .andExpect(request().sessionAttribute("loggedInCustomer", testCustomer)); 
        verify(customerService, times(1)).authenticate(eq(testCustomer.getEmail()), eq(testCustomer.getPassword()));
    }

    @Test
    void processLogin_failure() throws Exception {
        when(customerService.authenticate(anyString(), anyString()))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/login")
                        .param("email", "wrong@example.com")
                        .param("password", "wrongpass"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/login"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Invalid Email or Password"));

        verify(customerService, times(1)).authenticate(anyString(), anyString());
    }

    @Test
    void logout() throws Exception {
        MockHttpSession mockSession = new MockHttpSession(); 

        mockMvc.perform(get("/logout").session(mockSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout=true")); 
    }
    @Test
    void dashboard_loggedInCustomer() throws Exception {
        // Prepare mock data for services
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setVehicleId(1L);
        List<Vehicle> vehicles = Collections.singletonList(vehicle1);

        Policy policy1 = new Policy();
        policy1.setPolicyId(1L);
        List<Policy> policies = Collections.singletonList(policy1);

        Claim claim1 = new Claim();
        claim1.setClaimId(1L);
        List<Claim> claims = Collections.singletonList(claim1);

        Claim pendingClaim1 = new Claim();
        pendingClaim1.setClaimId(2L);
        List<Claim> pendingClaims = Collections.singletonList(pendingClaim1);

        when(vehicleService.getVehiclesByCustomer(testCustomer)).thenReturn(vehicles);
        when(policyService.getPoliciesForCustomer(testCustomer)).thenReturn(policies);
        when(claimService.getClaimsByCustomer(testCustomer)).thenReturn(claims);
        when(claimService.getClaimByStatus("SUBMITTED")).thenReturn(pendingClaims);

        mockMvc.perform(get("/dashboard").sessionAttr("loggedInCustomer", testCustomer))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/dashboard"))
                .andExpect(model().attributeExists("policies"))
                .andExpect(model().attribute("policies", policies))
                .andExpect(model().attributeExists("claims"))
                .andExpect(model().attribute("claims", claims))
                .andExpect(model().attributeExists("customer"))
                .andExpect(model().attribute("customer", testCustomer))
                .andExpect(model().attributeExists("vehicles"))
                .andExpect(model().attribute("vehicles", vehicles))
                .andExpect(model().attributeExists("pendingClaims"))
                .andExpect(model().attribute("pendingClaims", pendingClaims));

        verify(vehicleService, times(1)).getVehiclesByCustomer(testCustomer);
        verify(policyService, times(1)).getPoliciesForCustomer(testCustomer);
        verify(claimService, times(1)).getClaimsByCustomer(testCustomer);
        verify(claimService, times(1)).getClaimByStatus("SUBMITTED");
    }


    @Test
    void dashboard_notLoggedInCustomer() throws Exception {
        MockHttpSession mockSession = new MockHttpSession();

        mockMvc.perform(get("/dashboard").session(mockSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(vehicleService, never()).getVehiclesByCustomer(any(Customer.class));
        verify(policyService, never()).getPoliciesForCustomer(any(Customer.class));
        verify(claimService, never()).getClaimsByCustomer(any(Customer.class));
        verify(claimService, never()).getClaimByStatus(anyString());
    }
}
