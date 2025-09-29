package com.aurionpro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aurionpro.entity.Customer;
import com.aurionpro.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUserName(String userName);

	List<User> findByIsUserDeleted(String isUserDeleted);
	
	List<User> findAllByCustomer(Customer customer);
	
	 Optional<User> findByCustomerEmailid(String emailid);
}
