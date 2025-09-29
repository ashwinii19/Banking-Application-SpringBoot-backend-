package com.aurionpro.service;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.aurionpro.dto.AddressRequestDTO;
import com.aurionpro.dto.CustomerRequestDTO;
import com.aurionpro.dto.CustomerResponseDTO;
import com.aurionpro.dto.CustomerUpdateDTO;

public interface CustomerService {

	public CustomerResponseDTO createCustomer(CustomerRequestDTO customerdto);

	public CustomerResponseDTO updateCustomer(Long id, CustomerUpdateDTO customerdto, Authentication authentication);

	public CustomerResponseDTO getCustomerById(Long id, Authentication authentication);

	public List<CustomerResponseDTO> getAllCustomer(Authentication authentication);
	
	CustomerResponseDTO addOrUpdateAddress(Long customerId, AddressRequestDTO dto, Authentication authentication);

	public void deleteCustomer(Long id, Authentication authentication);
}
