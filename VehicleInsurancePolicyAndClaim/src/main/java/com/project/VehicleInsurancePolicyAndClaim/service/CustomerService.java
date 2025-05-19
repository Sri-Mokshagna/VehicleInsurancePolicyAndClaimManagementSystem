package com.project.VehicleInsurancePolicyAndClaim.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.VehicleInsurancePolicyAndClaim.model.Customer;
import com.project.VehicleInsurancePolicyAndClaim.repository.CustomerRepository;
@Service
public class CustomerService {
	@Autowired
	private CustomerRepository customerRepository;
	
	public Customer saveCustomer(Customer customer) {
		return customerRepository.save(customer);
	}
	
	public Optional<Customer> authenticate(String email,String password){
		return customerRepository.findByEmailAndPassword(email,password);
	}
}
