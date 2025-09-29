package com.aurionpro.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aurionpro.dto.AddressRequestDTO;
import com.aurionpro.dto.CustomerRequestDTO;
import com.aurionpro.dto.CustomerResponseDTO;
import com.aurionpro.dto.CustomerUpdateDTO;
import com.aurionpro.service.CustomerService;


@RestController
@RequestMapping("/api")
public class CustomerController {

	private CustomerService customerService;

	@Autowired
	public CustomerController(CustomerService customerService) {
		super();
		this.customerService = customerService;
	}
	
	@PostMapping("/customers")
	public ResponseEntity<CustomerResponseDTO> createCustomer(@Validated @RequestBody CustomerRequestDTO customerdto){
		CustomerResponseDTO customerrespdto = customerService.createCustomer(customerdto);
		return ResponseEntity.created(URI.create("/api/customers/"+customerrespdto.getCustomerId())).body(customerrespdto);
	}
	
//	@PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
	@PutMapping("/customers/{id}")
	public ResponseEntity<CustomerResponseDTO> updateCustomer(@PathVariable Long id,
	        @RequestBody CustomerUpdateDTO dto, Authentication authentication) {
	    return ResponseEntity.ok(customerService.updateCustomer(id, dto, authentication));
	}

	@GetMapping("/customers/{id}")
	public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id,
	        Authentication authentication) {
	    return ResponseEntity.ok(customerService.getCustomerById(id, authentication));
	}
	
//	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/customers")
	public ResponseEntity<List<CustomerResponseDTO>> getAllCustomer(Authentication authentication) {
	    return ResponseEntity.ok(customerService.getAllCustomer(authentication));
	}
	
	@PostMapping("/customers/{id}/address")
	public ResponseEntity<CustomerResponseDTO> addOrUpdateAddress(@PathVariable Long id,
	        @RequestBody AddressRequestDTO dto, Authentication authentication) {
	    CustomerResponseDTO updated = customerService.addOrUpdateAddress(id, dto, authentication);
	    return ResponseEntity.ok(updated);
	}

	
//	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/customers/{id}")
	public ResponseEntity<Void> deleteCustomer(@PathVariable Long id,
	        Authentication authentication) {
	    customerService.deleteCustomer(id, authentication);
	    return ResponseEntity.noContent().build();
	}
	
	
	
}
