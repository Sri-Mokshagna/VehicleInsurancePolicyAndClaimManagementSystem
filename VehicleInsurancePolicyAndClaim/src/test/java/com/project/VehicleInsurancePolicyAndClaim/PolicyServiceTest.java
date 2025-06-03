package com.project.VehicleInsurancePolicyAndClaim;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

import com.project.VehicleInsurancePolicyAndClaim.model.Customer;
import com.project.VehicleInsurancePolicyAndClaim.model.Policy;
import com.project.VehicleInsurancePolicyAndClaim.model.Vehicle;
import com.project.VehicleInsurancePolicyAndClaim.repository.PolicyRepository;
import com.project.VehicleInsurancePolicyAndClaim.service.PolicyService;

@ExtendWith(MockitoExtension.class)
public class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private PolicyService policyService;

    private Customer customer;
    private Vehicle vehicle;
    private Policy policy;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setCustomerId(1);
        customer.setEmail("john.doe@example.com");

        vehicle = new Vehicle();
        vehicle.setVehicleId(101L);
        vehicle.setMake("Toyota");
        vehicle.setModel("Camry");
        vehicle.setYearOfManufacture(2020);
        vehicle.setPrice(25000.0);
        vehicle.setCc(1800);
        vehicle.setCustomer(customer);

        policy = new Policy();
        policy.setPolicyId(1L);
        policy.setPolicyNumber("POL12345");
        policy.setPremiumAmount(500.0);
        policy.setCoverageAmount(20000.0);
        policy.setStartDate(LocalDate.of(2023, 1, 1));
        policy.setEndDate(LocalDate.of(2024, 1, 1));
        policy.setPolicyStatus("ACTIVE");
        policy.setCoverageType("STANDARD");
        policy.setVehicle(vehicle);
        policy.setBalance(20000.0);
    }

    @Test
    void testGetPoliciesForCustomerById() {
        List<Policy> expectedPolicies = Arrays.asList(policy);
        when(policyRepository.findByVehicleCustomerCustomerId(1L)).thenReturn(expectedPolicies);

        List<Policy> actualPolicies = policyService.getPoliciesForCustomer(1L);

        assertNotNull(actualPolicies);
        assertEquals(1, actualPolicies.size());
        assertEquals(policy.getPolicyId(), actualPolicies.get(0).getPolicyId());
        verify(policyRepository, times(1)).findByVehicleCustomerCustomerId(1L);
    }

    @Test
    void testGetPoliciesForCustomerByObject() {
        List<Policy> expectedPolicies = Arrays.asList(policy);
        when(policyRepository.findByVehicle_Customer(customer)).thenReturn(expectedPolicies);

        List<Policy> actualPolicies = policyService.getPoliciesForCustomer(customer);

        assertNotNull(actualPolicies);
        assertEquals(1, actualPolicies.size());
        assertEquals(policy.getPolicyId(), actualPolicies.get(0).getPolicyId());
        verify(policyRepository, times(1)).findByVehicle_Customer(customer);
    }

   

    
    @Test
    void testSavePolicy() {
        when(policyRepository.save(any(Policy.class))).thenReturn(policy);

        policyService.savePolicy(policy);

        verify(policyRepository, times(1)).save(policy);
    }

    @Test
    void testGetPolicyById_Found() {
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        Policy foundPolicy = policyService.getPolicyById(1L);

        assertNotNull(foundPolicy);
        assertEquals(policy.getPolicyId(), foundPolicy.getPolicyId());
        verify(policyRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPolicyById_NotFound() {
        when(policyRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            policyService.getPolicyById(99L);
        });

        assertEquals("Policy not found", exception.getMessage());
        verify(policyRepository, times(1)).findById(99L);
    }

}