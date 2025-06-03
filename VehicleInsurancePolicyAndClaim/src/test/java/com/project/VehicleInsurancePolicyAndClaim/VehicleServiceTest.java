package com.project.VehicleInsurancePolicyAndClaim;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.springframework.test.util.ReflectionTestUtils; // For setting @Value fields
import org.springframework.web.multipart.MultipartFile;

import com.project.VehicleInsurancePolicyAndClaim.model.Customer;
import com.project.VehicleInsurancePolicyAndClaim.model.Vehicle;
import com.project.VehicleInsurancePolicyAndClaim.repository.VehicleRepository;
import com.project.VehicleInsurancePolicyAndClaim.service.VehicleService;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Customer customer;
    private Vehicle vehicle1;
    private Vehicle vehicle2;
    private MultipartFile mockImageFile;

    private final String UPLOAD_DIR = "uploads"; // A dummy upload directory for testing

    @BeforeEach
    void setUp() {
        // Use ReflectionTestUtils to inject the @Value field
        ReflectionTestUtils.setField(vehicleService, "uploadDir", UPLOAD_DIR);

        customer = new Customer();
        customer.setCustomerId(1);
        customer.setEmail("customer@example.com");

        vehicle1 = new Vehicle();
        vehicle1.setVehicleId(101L);
        vehicle1.setMake("Toyota");
        vehicle1.setModel("Camry");
        vehicle1.setCustomer(customer);
        vehicle1.setImagePath("image1.jpg"); 

        vehicle2 = new Vehicle();
        vehicle2.setVehicleId(102L);
        vehicle2.setMake("Honda");
        vehicle2.setModel("Civic");
        vehicle2.setCustomer(customer);
        vehicle2.setImagePath(null); // Pre-set for tests where image is not present

        // Mock a MultipartFile for testing file uploads
        mockImageFile = mock(MultipartFile.class);
    }

    
    @Test
    void testSaveVehicle_WithoutImage() throws IOException {
        // Simulate an empty image file
        when(mockImageFile.isEmpty()).thenReturn(true);

        // Mock the repository save method
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> {
            Vehicle v = invocation.getArgument(0);
            v.setVehicleId(104L); // Simulate ID being set by repository
            return v;
        });

        // Ensure Files.copy is NOT called when image is empty
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            Vehicle savedVehicle = vehicleService.saveVehicle(vehicle2, mockImageFile);

            assertNotNull(savedVehicle);
            assertNull(savedVehicle.getImagePath()); // Image path should remain null
            assertEquals(104L, savedVehicle.getVehicleId());

            // Verify Files.copy was never called
            mockedFiles.verify(() -> Files.copy(any(InputStream.class), any(Path.class)), never()); // Also specify InputStream.class here

            // Verify vehicleRepository.save was called once
            verify(vehicleRepository, times(1)).save(any(Vehicle.class));
        }
    }

    @Test
    void testGetVehiclesByCustomer() {
        List<Vehicle> expectedVehicles = Arrays.asList(vehicle1, vehicle2);
        when(vehicleRepository.findByCustomer(customer)).thenReturn(expectedVehicles);

        List<Vehicle> actualVehicles = vehicleService.getVehiclesByCustomer(customer);

        assertNotNull(actualVehicles);
        assertEquals(2, actualVehicles.size());
        assertTrue(actualVehicles.contains(vehicle1));
        assertTrue(actualVehicles.contains(vehicle2));
        verify(vehicleRepository, times(1)).findByCustomer(customer);
    }

    @Test
    void testGetVehicleWithoutPolicy() {
        List<Vehicle> expectedVehicles = Arrays.asList(vehicle2); // Assuming vehicle2 has no policy
        when(vehicleRepository.findByCustomerAndPolicyIsNull(customer)).thenReturn(expectedVehicles);

        List<Vehicle> actualVehicles = vehicleService.getVehicleWithoutPolicy(customer);

        assertNotNull(actualVehicles);
        assertEquals(1, actualVehicles.size());
        assertTrue(actualVehicles.contains(vehicle2));
        assertFalse(actualVehicles.contains(vehicle1)); // vehicle1 has imagePath, might imply policy or just for test setup
        verify(vehicleRepository, times(1)).findByCustomerAndPolicyIsNull(customer);
    }

    @Test
    void testGetVehicleById_Found() {
        when(vehicleRepository.findById(101L)).thenReturn(Optional.of(vehicle1));

        Vehicle foundVehicle = vehicleService.getVehicleById(101L);

        assertNotNull(foundVehicle);
        assertEquals(101L, foundVehicle.getVehicleId());
        assertEquals("Toyota", foundVehicle.getMake());
        verify(vehicleRepository, times(1)).findById(101L);
    }

    @Test
    void testGetVehicleById_NotFound() {
        when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());

        Vehicle foundVehicle = vehicleService.getVehicleById(999L);

        assertNull(foundVehicle);
        verify(vehicleRepository, times(1)).findById(999L);
    }
}
