package com.aurionpro.service;

import java.util.List;

import com.aurionpro.dto.AddressRequestDTO;
import com.aurionpro.dto.AddressResponseDTO;
import com.aurionpro.dto.AddressUpdateDTO;

public interface AddressService {

	public AddressResponseDTO createAddress(AddressRequestDTO addreqdto);
	public AddressResponseDTO getAddressById(Long id);
	public List<AddressResponseDTO> getAllAddress();
	public AddressResponseDTO updateAddress(Long id, AddressUpdateDTO addupdto);
	public void deleteAddress(Long id);
}
