package com.aurionpro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aurionpro.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long>{

}
