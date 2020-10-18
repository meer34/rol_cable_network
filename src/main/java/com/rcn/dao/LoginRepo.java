package com.rcn.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rcn.model.LogIn;

public interface LoginRepo extends JpaRepository<LogIn, Integer> {
	
	LogIn findByUsername(String username);
}