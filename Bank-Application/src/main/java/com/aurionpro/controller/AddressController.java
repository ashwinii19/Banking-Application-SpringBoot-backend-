package com.aurionpro.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import com.aurionpro.dto.AddressResponseDTO;
import com.aurionpro.dto.AddressUpdateDTO;
import com.aurionpro.service.AddressService;

@RestController
@RequestMapping("/api")
public class AddressController {

	private AddressService addressService;

	@Autowired
	public AddressController(AddressService addressService) {
		super();
		this.addressService = addressService;
	}
	
	@PostMapping("/addresses")
	public ResponseEntity<AddressResponseDTO> createAddress(@Validated @RequestBody AddressRequestDTO addreqdto){
		AddressResponseDTO addressResponseDTO = addressService.createAddress(addreqdto);
		return ResponseEntity.created(URI.create("/api/addresses/"+addressResponseDTO.getAddressId())).body(addressResponseDTO);
	}
	
	@GetMapping("/addresses/{id}")
	public ResponseEntity<AddressResponseDTO> getAddressById(@PathVariable Long id){
		return ResponseEntity.ok(addressService.getAddressById(id));
	}
	
	@GetMapping("/addresses")
	public ResponseEntity<List<AddressResponseDTO>> getAllAddress(){
		return ResponseEntity.ok(addressService.getAllAddress());
	}
	
	@PutMapping("/addresses/{id}")
	public ResponseEntity<AddressResponseDTO> updateAddress(@Validated @PathVariable Long id, @RequestBody AddressUpdateDTO addupdto){
		return ResponseEntity.ok(addressService.updateAddress(id, addupdto));
	}
	
	@DeleteMapping("/addresses/{id}")
	public ResponseEntity<Void> deleteAddress(@PathVariable Long id){
		addressService.deleteAddress(id);
		return ResponseEntity.noContent().build();
	}
	
}
