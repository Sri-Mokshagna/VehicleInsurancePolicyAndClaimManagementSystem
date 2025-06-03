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
        customer.setPassword(rawPassword); // Set raw password for initial customer object

        // Manually hash the password to simulate what BCrypt would do
        hashedPassword = BCrypt.withDefaults().hashToString(12, rawPassword.toCharArray());
    }

    @Test
    void testSaveCustomer() {
        // Prepare the customer object with the raw password
        Customer customerToSave = new Customer();
        customerToSave.setEmail("new@example.com");
        customerToSave.setPassword("newPassword");

        // When save is called, return the customer object that was passed to the repository.
        // The CustomerService itself will have already hashed the password on this object.
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            // Return the customer object that was passed as an argument to the save method.
            // This object will already contain the password hashed by the service.
            return invocation.getArgument(0);
        });

        Customer savedCustomer = customerService.saveCustomer(customerToSave);

        assertNotNull(savedCustomer);
        assertEquals("new@example.com", savedCustomer.getEmail());
        // Verify that the password in the saved customer is not the raw password,
        // implying it has been hashed.
        assertNotEquals("newPassword", savedCustomer.getPassword());
        // Now, verify that the raw password can be successfully verified against the
        // hashed password stored in the savedCustomer object, which came from the service.
        assertTrue(BCrypt.verifyer().verify("newPassword".toCharArray(), savedCustomer.getPassword()).verified);

        // Verify that the save method on the repository was called exactly once
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