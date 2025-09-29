package com.aurionpro.dto;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassbookRequestDTO {


	@Id
	@NotNull
	private String accountNumber;
}
