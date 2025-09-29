package com.aurionpro.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aurionpro.dto.AddressRequestDTO;
import com.aurionpro.dto.AddressResponseDTO;
import com.aurionpro.dto.AddressUpdateDTO;
import com.aurionpro.entity.Address;
import com.aurionpro.exception.ResourceNotFoundException;
import com.aurionpro.mapping.AddressMapper;
import com.aurionpro.repository.AddressRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AddressServiceImpl implements AddressService{

	private final AddressMapper addressMapper;
	private final AddressRepository addressRepository;

	@Autowired
	public AddressServiceImpl(AddressMapper addressMapper, AddressRepository addressRepository) {
		this.addressMapper = addressMapper;
		this.addressRepository = addressRepository;
	}

	@Override
	public AddressResponseDTO createAddress(AddressRequestDTO addreqdto) {
		Address address = addressMapper.toEntity(addreqdto);
		Address saved = addressRepository.save(address);
		return addressMapper.toResponse(saved);
	}

	@Override
	public AddressResponseDTO getAddressById(Long id) {
		Address address = addressRepository.findById(id).
				orElseThrow(() -> new ResourceNotFoundException("Address not Found!"));
		return addressMapper.toResponse(address);
	}

	@Override
	public List<AddressResponseDTO> getAllAddress() {
		List<Address> address = addressRepository.findAll();
		return addressMapper.toResponseList(address);
	}

	@Override
	public AddressResponseDTO updateAddress(Long id, AddressUpdateDTO addupdto) {
		Address address = addressRepository.findById(id).
				orElseThrow(() -> new ResourceNotFoundException("Address not Found!"));
		addressMapper.applyUpdate(addupdto, address);
	    
	    Address updatedAddress = addressRepository.save(address);
	    
	    return addressMapper.toResponse(updatedAddress);
	}

	@Override
	public void deleteAddress(Long id) {
		Address address = addressRepository.findById(id).
				orElseThrow(() -> new ResourceNotFoundException("Address not Found!"));
		addressRepository.delete(address);
	}
	
	
	
	
}
