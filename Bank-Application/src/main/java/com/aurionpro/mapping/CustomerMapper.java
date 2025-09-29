package com.aurionpro.mapping;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.aurionpro.dto.AccountResponseDTO;
import com.aurionpro.dto.AddressResponseDTO;
import com.aurionpro.dto.CustomerRequestDTO;
import com.aurionpro.dto.CustomerResponseDTO;
import com.aurionpro.dto.CustomerUpdateDTO;
import com.aurionpro.entity.Account;
import com.aurionpro.entity.Address;
import com.aurionpro.entity.Customer;

@Component
public class CustomerMapper {
	
	private final ModelMapper modelMapper;

	public CustomerMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}
	
	public Customer toEntity(CustomerRequestDTO dto) {
        Customer customer = modelMapper.map(dto, Customer.class);
        
        if (dto.getAddress() != null) {
            Address address = modelMapper.map(dto.getAddress(), Address.class); 
            customer.setAddress(address);
        }
        
        if (dto.getAccount() != null) {
            Account account = modelMapper.map(dto.getAccount(), Account.class); 
            account.setCustomer(customer); 
            customer.getAccounts().add(account);
        }

        return customer;
    }

    public CustomerResponseDTO toResponse(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerResponseDTO dto = modelMapper.map(customer, CustomerResponseDTO.class);

        if (customer.getAddress() != null) {
        	AddressResponseDTO addressDTO = modelMapper.map(customer.getAddress(), AddressResponseDTO.class);
        	dto.setAddress(addressDTO);
        }
        
        if(customer.getAccounts() != null && !customer.getAccounts().isEmpty()) {
        	Account firstAccount = customer.getAccounts().get(0);
        	List<AccountResponseDTO> accountDTOs = customer.getAccounts().stream()
                    .map(acc -> modelMapper.map(acc, AccountResponseDTO.class))
                    .toList();
        	dto.setAccounts(accountDTOs);
        }

        return dto;
    }

    public List<CustomerResponseDTO> toResponseList(List<Customer> customers) {
        return customers.stream()
                .map(this::toResponse)
                .toList();
    }

    
    public static void applyUpdate(CustomerUpdateDTO dto, Customer customer) {
        if (dto.getEmailid() != null) {
            customer.setEmailid(dto.getEmailid());
        }
        if (dto.getContactNo() != null) {
            customer.setContactNo(dto.getContactNo());
        }

        if (customer.getAddress() == null) {
            customer.setAddress(new Address());
        }
        if (dto.getCity() != null) {
            customer.getAddress().setCity(dto.getCity());
        }
        if (dto.getState() != null) {
            customer.getAddress().setState(dto.getState());
        }
        if (dto.getPincode() != null) {
            customer.getAddress().setPincode(dto.getPincode());
        }

        if (!customer.getAccounts().isEmpty()) {
            Account account = customer.getAccounts().get(0);

            if (dto.getAccountType() != null) {
                account.setAccountType(dto.getAccountType());
            }
            if (dto.getBalance() != null) {
                account.setBalance(dto.getBalance());
            }
        } else {
            if (dto.getAccountType() != null && dto.getBalance() != null) {
                Account newAccount = new Account();
                newAccount.setAccountType(dto.getAccountType());
                newAccount.setBalance(dto.getBalance());
                newAccount.setCustomer(customer);
                customer.getAccounts().add(newAccount);
            }
        }
    }

}
