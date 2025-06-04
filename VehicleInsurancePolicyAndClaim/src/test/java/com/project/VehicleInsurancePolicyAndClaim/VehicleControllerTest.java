package com.project.VehicleInsurancePolicyAndClaim;

import com.project.VehicleInsurancePolicyAndClaim.controller.VehicleController;
import com.project.VehicleInsurancePolicyAndClaim.model.Claim;
import com.project.VehicleInsurancePolicyAndClaim.model.Customer;
import com.project.VehicleInsurancePolicyAndClaim.model.Vehicle;
import com.project.VehicleInsurancePolicyAndClaim.model.VehicleType;
import com.project.VehicleInsurancePolicyAndClaim.service.ClaimService;
import com.project.VehicleInsurancePolicyAndClaim.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

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

@WebMvcTest(VehicleController.class)
public class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehicleService vehicleService;

    @MockBean
    private ClaimService claimService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private VehicleController vehicleController;

    private Customer testCustomer;
    private Vehicle testVehicle;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setCustomerId(1);
        testCustomer.setEmail("test@example.com");
        testVehicle = new Vehicle();
        testVehicle.setVehicleId(101L);
        testVehicle.setRegistrationNumber("VEH-123");
        testVehicle.setMake("Toyota");
        testVehicle.setModel("Camry");
        testVehicle.setVehicleType(VehicleType.CAR);
        testVehicle.setCustomer(testCustomer);

        when(session.getAttribute("loggedInCustomer")).thenReturn(testCustomer);
    }

    @Test
    void listVehicles_loggedInCustomer() throws Exception {
        List<Vehicle> vehicles = Collections.singletonList(testVehicle);

        when(vehicleService.getVehiclesByCustomer(testCustomer)).thenReturn(vehicles);

        mockMvc.perform(get("/vehicles").sessionAttr("loggedInCustomer", testCustomer))
                .andExpect(status().isOk())
                .andExpect(view().name("vehicle/vehicles"))
                .andExpect(model().attributeExists("vehicles"))
                .andExpect(model().attribute("vehicles", vehicles));

        verify(vehicleService, times(1)).getVehiclesByCustomer(testCustomer);
    }

    @Test
    void listVehicles_notLoggedInCustomer() throws Exception {
        MockHttpSession mockSession = new MockHttpSession();

        mockMvc.perform(get("/vehicles").session(mockSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(vehicleService, never()).getVehiclesByCustomer(any(Customer.class));
    }

    @Test
    void showAddForm() throws Exception {
        mockMvc.perform(get("/addnewvehicle"))
                .andExpect(status().isOk())
                .andExpect(view().name("vehicle/addVehicle"))
                .andExpect(model().attributeExists("vehicle"))
                .andDo(result -> {
                    Object vehicleAttribute = result.getModelAndView().getModel().get("vehicle");
                    assertNotNull(vehicleAttribute);
                    assertTrue(vehicleAttribute instanceof Vehicle);
                })
                .andExpect(model().attributeExists("vehicleTypes"))
                .andExpect(model().attribute("vehicleTypes", VehicleType.values()));
    }

    @Test
    void viewVehicle_notLoggedInCustomer() throws Exception {
        MockHttpSession mockSession = new MockHttpSession();

        mockMvc.perform(get("/vehicle/{id}", testVehicle.getVehicleId())
                        .session(mockSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(vehicleService, never()).getVehicleById(anyLong());
        verify(claimService, never()).getClaimsByVehicle(any(Vehicle.class));
    }
    @Test
    void addVehicle_loggedInCustomer_success() throws Exception {
        MockMultipartFile mockImage = new MockMultipartFile(
                "image", "test-image.jpg", "image/jpeg", "some image content".getBytes());

        Vehicle savedVehicle = new Vehicle();
        savedVehicle.setVehicleId(200L); // Assign a dummy ID
        savedVehicle.setRegistrationNumber("NEW-VEH-456");
        savedVehicle.setMake("Honda");
        savedVehicle.setModel("Civic");
        savedVehicle.setVehicleType(VehicleType.CAR);
        savedVehicle.setCustomer(testCustomer); 
        when(vehicleService.saveVehicle(any(Vehicle.class), any(MultipartFile.class))).thenReturn(savedVehicle);

        mockMvc.perform(multipart("/addnewvehicle")
                        .file(mockImage)
                        .param("registrationNumber", "NEW-VEH-456")
                        .param("make", "Honda")
                        .param("model", "Civic")
                        .param("vehicleType", VehicleType.CAR.name())
                        .sessionAttr("loggedInCustomer", testCustomer))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vehicles"));

        verify(vehicleService, times(1)).saveVehicle(any(Vehicle.class), eq(mockImage));
    }
    
}
