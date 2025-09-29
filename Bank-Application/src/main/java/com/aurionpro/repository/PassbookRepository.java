package com.aurionpro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aurionpro.entity.Passbook;

public interface PassbookRepository extends JpaRepository<Passbook, Long>{

}
