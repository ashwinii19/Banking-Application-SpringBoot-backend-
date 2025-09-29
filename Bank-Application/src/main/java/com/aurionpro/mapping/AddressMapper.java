package com.aurionpro.mapping;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.aurionpro.dto.AddressRequestDTO;
import com.aurionpro.dto.AddressResponseDTO;
import com.aurionpro.dto.AddressUpdateDTO;
import com.aurionpro.entity.Address;

@Component
public class AddressMapper {
	
	private final ModelMapper modelMapper;

	public AddressMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}
	
	public Address toEntity(AddressRequestDTO addreqdto) {
		Address address = modelMapper.map(addreqdto, Address.class);
		return address;
	}
	
	public AddressResponseDTO toResponse(Address address) {
	    AddressResponseDTO addrespdto = modelMapper.map(address, AddressResponseDTO.class);
	    return addrespdto;
	}
	
	public List<AddressResponseDTO> toResponseList(List<Address> address){
		return address.stream().map(this::toResponse).toList();
	}
	
	public void applyUpdate(AddressUpdateDTO dto, Address address) {
	    if (dto == null || address == null) {
	        throw new IllegalArgumentException("DTO or Address cannot be null");
	    }
	    if (dto.getCity() != null) {
	        address.setCity(dto.getCity());
	    }
	    if (dto.getState() != null) {
	        address.setState(dto.getState());
	    }
	    if (dto.getPincode() != null) {
	        address.setPincode(dto.getPincode());
	    }
	}

	
}
