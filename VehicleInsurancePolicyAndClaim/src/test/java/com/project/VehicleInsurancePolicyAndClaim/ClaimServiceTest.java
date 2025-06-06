package com.project.VehicleInsurancePolicyAndClaim;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.InputStream;
import java.time.LocalDate;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.project.VehicleInsurancePolicyAndClaim.model.Claim;
import com.project.VehicleInsurancePolicyAndClaim.model.Customer;
import com.project.VehicleInsurancePolicyAndClaim.model.Policy;
import com.project.VehicleInsurancePolicyAndClaim.model.Vehicle;
import com.project.VehicleInsurancePolicyAndClaim.repository.ClaimRepository;
import com.project.VehicleInsurancePolicyAndClaim.service.ClaimService;

@ExtendWith(MockitoExtension.class)
public class ClaimServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    @InjectMocks
    private ClaimService claimService;

    private Customer customer;
    private Vehicle vehicle;
    private Policy policy;
    private Claim claim;
    private MultipartFile mockImageFile;

    private final String UPLOAD_DIR = "uploads"; 

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(claimService, "uploadDir", UPLOAD_DIR);

        customer = new Customer();
        customer.setCustomerId(1);
        customer.setEmail("customer@example.com");

        vehicle = new Vehicle();
        vehicle.setVehicleId(101L);
        vehicle.setMake("Toyota");
        vehicle.setModel("Camry");
        vehicle.setCustomer(customer);

        policy = new Policy();
        policy.setPolicyId(1L);
        policy.setPolicyNumber("POL12345");
        policy.setVehicle(vehicle);

        claim = new Claim();
        claim.setClaimId(1L);
        claim.setClaimAmount(5000.0);
        claim.setPolicy(policy); 

        mockImageFile = mock(MultipartFile.class);
    }

    @Test
    void testFileClaim_WithImage() throws IOException {
        when(mockImageFile.isEmpty()).thenReturn(false);
        when(mockImageFile.getOriginalFilename()).thenReturn("accident_photo.jpg");
        when(mockImageFile.getInputStream()).thenReturn(new ByteArrayInputStream("image data".getBytes()));

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class);
             MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class)) {

            LocalDate fixedDate = LocalDate.of(2024, 6, 3);
            mockedLocalDate.when(LocalDate::now).thenReturn(fixedDate);

            mockedFiles.when(() -> Files.copy(any(InputStream.class), any(Path.class))).thenReturn(1L);

            when(claimRepository.save(any(Claim.class))).thenAnswer(invocation -> {
                Claim savedClaim = invocation.getArgument(0);
                savedClaim.setClaimId(2L);
                return savedClaim;
            });

            Claim claimToFile = new Claim();
            claimToFile.setClaimAmount(1000.0);
            claimToFile.setPolicy(policy);

            claimService.fileClaim(claimToFile, mockImageFile);

            assertNotNull(claimToFile.getImagePath());
            assertFalse(claimToFile.getImagePath().isEmpty());
            assertTrue(claimToFile.getImagePath().contains("accident_photo.jpg"));
            assertEquals(fixedDate, claimToFile.getClaimDate());
            assertEquals("SUBMITTED", claimToFile.getClaimStatus());

            mockedFiles.verify(() -> Files.copy(any(InputStream.class), argThat(path ->
                    path.toString().startsWith(UPLOAD_DIR) && path.getFileName().toString().contains("accident_photo.jpg")
            )), times(1));

            verify(claimRepository, times(1)).save(claimToFile);
        }
    }

    @Test
    void testFileClaim_WithoutImage() throws IOException {
        when(mockImageFile.isEmpty()).thenReturn(true);

        try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class)) {
            LocalDate fixedDate = LocalDate.of(2024, 6, 3);
            mockedLocalDate.when(LocalDate::now).thenReturn(fixedDate);

            when(claimRepository.save(any(Claim.class))).thenAnswer(invocation -> {
                Claim savedClaim = invocation.getArgument(0);
                savedClaim.setClaimId(3L); 
                return savedClaim;
            });

            Claim claimToFile = new Claim();
            claimToFile.setClaimAmount(200.0);
            claimToFile.setPolicy(policy);

            claimService.fileClaim(claimToFile, mockImageFile);

            assertNull(claimToFile.getImagePath()); 
            assertEquals(fixedDate, claimToFile.getClaimDate());
            assertEquals("SUBMITTED", claimToFile.getClaimStatus());

            try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) { 
                mockedFiles.verify(() -> Files.copy(any(InputStream.class), any(Path.class)), never());
            }

            verify(claimRepository, times(1)).save(claimToFile);
        }
    }

    @Test
    void testGetClaimsByCustomer_CustomerObject() {
        List<Claim> expectedClaims = Arrays.asList(claim);
        when(claimRepository.findByPolicy_Vehicle_Customer(customer)).thenReturn(expectedClaims);

        List<Claim> actualClaims = claimService.getClaimsByCustomer(customer);

        assertNotNull(actualClaims);
        assertEquals(1, actualClaims.size());
        assertEquals(claim.getClaimId(), actualClaims.get(0).getClaimId());
        verify(claimRepository, times(1)).findByPolicy_Vehicle_Customer(customer);
    }

    @Test
    void testGetClaimsByCustomer_CustomerId() {
        List<Claim> expectedClaims = Arrays.asList(claim);
        when(claimRepository.findByPolicy_Vehicle_CustomerCustomerId(1L)).thenReturn(expectedClaims);

        List<Claim> actualClaims = claimService.getClaimsByCustomer(1L);

        assertNotNull(actualClaims);
        assertEquals(1, actualClaims.size());
        assertEquals(claim.getClaimId(), actualClaims.get(0).getClaimId());
        verify(claimRepository, times(1)).findByPolicy_Vehicle_CustomerCustomerId(1L);
    }

    @Test
    void testGetClaimsByVehicle() {
        List<Claim> expectedClaims = Arrays.asList(claim);
        when(claimRepository.findByPolicy_Vehicle(vehicle)).thenReturn(expectedClaims);

        List<Claim> actualClaims = claimService.getClaimsByVehicle(vehicle);

        assertNotNull(actualClaims);
        assertEquals(1, actualClaims.size());
        assertEquals(claim.getClaimId(), actualClaims.get(0).getClaimId());
        verify(claimRepository, times(1)).findByPolicy_Vehicle(vehicle);
    }

    @Test
    void testGetClaimByStatus() {
        List<Claim> expectedClaims = Arrays.asList(claim);
        when(claimRepository.findByClaimStatus("SUBMITTED")).thenReturn(expectedClaims);

        List<Claim> actualClaims = claimService.getClaimByStatus("SUBMITTED");

        assertNotNull(actualClaims);
        assertEquals(1, actualClaims.size());
        assertEquals(claim.getClaimId(), actualClaims.get(0).getClaimId());
        verify(claimRepository, times(1)).findByClaimStatus("SUBMITTED");
    }
}