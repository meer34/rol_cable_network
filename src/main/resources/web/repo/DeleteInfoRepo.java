package com.hunter.web.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hunter.web.model.DeleteInfo;

public interface DeleteInfoRepo extends JpaRepository<DeleteInfo, Long>{

	Long deleteByClassName(String className);
	
}
