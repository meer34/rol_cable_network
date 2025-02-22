package com.hunter.web.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.hunter.web.model.User;

public interface UserRepo extends CrudRepository<User, Long>{
	
	@Query("SELECT u from User u where u.phone = :phone")
	public User getUserByPhoneNumber(@Param("phone") String phone);
	
	@Query("SELECT u from User u where u.username = :username")
	public User getUserByUsername(@Param("username") String username);

}
