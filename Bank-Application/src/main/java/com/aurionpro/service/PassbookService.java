package com.aurionpro.service;

import com.aurionpro.dto.PassbookRequestDTO;
import com.aurionpro.dto.PassbookResponseDTO;

public interface PassbookService {

	PassbookResponseDTO generateAndSendPassbook(PassbookRequestDTO requestDTO);
}
