package com.project.VehicleInsurancePolicyAndClaim.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.VehicleInsurancePolicyAndClaim.model.Customer;
import com.project.VehicleInsurancePolicyAndClaim.repository.CustomerRepository;

import at.favre.lib.crypto.bcrypt.BCrypt;
@Service
public class CustomerService {
	@Autowired
	private CustomerRepository customerRepository;
	
	public Customer saveCustomer(Customer customer) {
		String bcryptHashString = BCrypt.withDefaults().hashToString(12, customer.getPassword().toCharArray());
		customer.setPassword(bcryptHashString);
		return customerRepository.save(customer);
	}
	
	public Optional<Customer> authenticate(String email,String password){
		Optional<Customer> customerOpt = customerRepository.findByEmail(email);
		if(customerOpt.isPresent()) 
		{
			Customer customer = customerOpt.get();
			if(BCrypt.verifyer().verify(password.toCharArray(), customer.getPassword()).verified) 
			{
				return Optional.of(customer);
			}
		}
		return Optional.empty();
	}
	
	public Customer findById(Long id) {
		return customerRepository.findByCustomerId(id);
	}

}
