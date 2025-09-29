package com.aurionpro.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.aurionpro.dto.AddressRequestDTO;
import com.aurionpro.dto.AddressUpdateDTO;
import com.aurionpro.dto.CustomerRequestDTO;
import com.aurionpro.dto.CustomerResponseDTO;
import com.aurionpro.dto.CustomerUpdateDTO;
import com.aurionpro.entity.Customer;
import com.aurionpro.entity.User;
import com.aurionpro.exception.ResourceNotFoundException;
import com.aurionpro.mapping.AddressMapper;
import com.aurionpro.mapping.CustomerMapper;
import com.aurionpro.repository.CustomerRepository;
import com.aurionpro.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

	private final CustomerMapper customerMapper;
	private final AddressMapper addressMapper;
	private final CustomerRepository customerrepository;
	private final UserRepository userRepository;

	public CustomerServiceImpl(CustomerMapper customerMapper, CustomerRepository customerrepository,
			AddressMapper addressMapper, UserRepository userRepository) {
		this.customerMapper = customerMapper;
		this.customerrepository = customerrepository;
		this.addressMapper = addressMapper;
		this.userRepository = userRepository;
	}

	@Override
	public CustomerResponseDTO createCustomer(CustomerRequestDTO customerdto) {
		Customer customer = customerMapper.toEntity(customerdto);
		Customer saved = customerrepository.save(customer);
		return customerMapper.toResponse(saved);
	}

	 @Override
	    public CustomerResponseDTO updateCustomer(Long id, CustomerUpdateDTO customerdto, Authentication authentication) {
	        User caller = userRepository.findByUserName(authentication.getName())
	                .orElseThrow(() -> new ResourceNotFoundException("Caller user not found"));

	        boolean isAdmin = caller.getRoles().stream()
	                .anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getRoleName()));

	        if (!isAdmin) {
	            Customer own = caller.getCustomer();
	            if (own == null || !own.getCustomerId().equals(id)) {
	                throw new AccessDeniedException("You can only update your own customer details");
	            }
	        }

	        Customer customer = customerrepository.findById(id)
	                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
	        customerMapper.applyUpdate(customerdto, customer);
	        Customer updated = customerrepository.save(customer);
	        return customerMapper.toResponse(updated);
	    }


	@Override
	public CustomerResponseDTO getCustomerById(Long id, Authentication authentication) {
		User user = userRepository.findByUserName(authentication.getName()).
				orElseThrow(() -> new ResourceNotFoundException("user not found"));
		
		boolean isAdmin = user.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getRoleName()));
		
		if(!isAdmin) {
			Customer own = user.getCustomer();
            if (own == null || !own.getCustomerId().equals(id)) {
                throw new AccessDeniedException("You can only view your own customer details");
            }
		}
		
		Customer customer = customerrepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not Found!"));
		return customerMapper.toResponse(customer);
	}

	@Override
    public List<CustomerResponseDTO> getAllCustomer(Authentication authentication) {
        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Caller user not found"));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getRoleName().equalsIgnoreCase("ADMIN") || r.getRoleName().equalsIgnoreCase("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new AccessDeniedException("Only admins can view all customers");
        }

        List<Customer> all = customerrepository.findAll();
        return customerMapper.toResponseList(all);
    }

	@Override
    public void deleteCustomer(Long id, Authentication authentication) {
        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Caller user not found"));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getRoleName()));
        if (!isAdmin) {
            throw new AccessDeniedException("Only admins can delete customers");
        }
        Customer customer = customerrepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        List<User> users = userRepository.findAllByCustomer(customer);
        for (User usr : users) {
            usr.setCustomer(null);
            userRepository.save(usr);
        }
        customerrepository.delete(customer);
    }

	 @Override
	    public CustomerResponseDTO addOrUpdateAddress(Long customerId, AddressRequestDTO dto, Authentication authentication) {
	        User user = userRepository.findByUserName(authentication.getName())
	                .orElseThrow(() -> new ResourceNotFoundException("Caller user not found"));

	        boolean isAdmin = user.getRoles().stream()
	                .anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getRoleName()));

	        if (!isAdmin) {
	            Customer own = user.getCustomer();
	            if (own == null || !own.getCustomerId().equals(customerId)) {
	                throw new AccessDeniedException("You can only update your own address");
	            }
	        }

	        Customer customer = customerrepository.findById(customerId)
	                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

	        if (customer.getAddress() == null) {
	            customer.setAddress(addressMapper.toEntity(dto));
	        } else {
	            AddressUpdateDTO updateDTO = convertRequestToUpdateDTO(dto);
	            addressMapper.applyUpdate(updateDTO, customer.getAddress());
	        }

	        Customer updatedCustomer = customerrepository.save(customer);
	        return customerMapper.toResponse(updatedCustomer);
	    }
	
	 private AddressUpdateDTO convertRequestToUpdateDTO(AddressRequestDTO dto) {
		    AddressUpdateDTO updateDTO = new AddressUpdateDTO();
		    updateDTO.setCity(dto.getCity());
		    updateDTO.setState(dto.getState());
		    updateDTO.setPincode(dto.getPincode());
		    return updateDTO;
		}


}
