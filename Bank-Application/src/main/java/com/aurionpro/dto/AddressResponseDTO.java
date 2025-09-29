package com.aurionpro.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressResponseDTO {

	private Long addressId;
	private String city;
	private String state;
	private String pincode;
}
