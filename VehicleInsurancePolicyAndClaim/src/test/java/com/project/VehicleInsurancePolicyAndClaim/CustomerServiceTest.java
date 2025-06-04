package com.project.VehicleInsurancePolicyAndClaim;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.VehicleInsurancePolicyAndClaim.model.Customer;
import com.project.VehicleInsurancePolicyAndClaim.repository.CustomerRepository;
import com.project.VehicleInsurancePolicyAndClaim.service.CustomerService;

import at.favre.lib.crypto.bcrypt.BCrypt;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private String rawPassword = "testPassword123";
    private String hashedPassword;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setCustomerId(1);
        customer.setEmail("test@example.com");
        customer.setPassword(rawPassword); 
        hashedPassword = BCrypt.withDefaults().hashToString(12, rawPassword.toCharArray());
    }

    @Test
    void testSaveCustomer() {
        Customer customerToSave = new Customer();
        customerToSave.setEmail("new@example.com");
        customerToSave.setPassword("newPassword");

        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        Customer savedCustomer = customerService.saveCustomer(customerToSave);

        assertNotNull(savedCustomer);
        assertEquals("new@example.com", savedCustomer.getEmail());
        assertNotEquals("newPassword", savedCustomer.getPassword());
        assertTrue(BCrypt.verifyer().verify("newPassword".toCharArray(), savedCustomer.getPassword()).verified);

        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testAuthenticate_Success() {
        customer.setPassword(hashedPassword);

        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));

        Optional<Customer> authenticatedCustomer = customerService.authenticate(customer.getEmail(), rawPassword);

        assertTrue(authenticatedCustomer.isPresent());
        assertEquals(customer.getEmail(), authenticatedCustomer.get().getEmail());
        verify(customerRepository, times(1)).findByEmail(customer.getEmail());
    }

    @Test
    void testAuthenticate_Failure_WrongPassword() {
        customer.setPassword(hashedPassword);

        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));

        Optional<Customer> authenticatedCustomer = customerService.authenticate(customer.getEmail(), "wrongPassword");

        assertFalse(authenticatedCustomer.isPresent());
        verify(customerRepository, times(1)).findByEmail(customer.getEmail());
    }

    @Test
    void testAuthenticate_Failure_CustomerNotFound() {
        when(customerRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        Optional<Customer> authenticatedCustomer = customerService.authenticate("nonexistent@example.com", rawPassword);

        assertFalse(authenticatedCustomer.isPresent());
        verify(customerRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    void testFindById() {
        when(customerRepository.findByCustomerId(1L)).thenReturn(customer);

        Customer foundCustomer = customerService.findById(1L);

        assertNotNull(foundCustomer);
        assertEquals(1L, foundCustomer.getCustomerId());
        assertEquals("test@example.com", foundCustomer.getEmail());
        verify(customerRepository, times(1)).findByCustomerId(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(customerRepository.findByCustomerId(2L)).thenReturn(null);

        Customer foundCustomer = customerService.findById(2L);

        assertNull(foundCustomer);
        verify(customerRepository, times(1)).findByCustomerId(2L);
    }
}