package com.pocketbank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pocketbank.entity.User;
import com.pocketbank.entity.enums.Role;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	//Find user by username for authentication
	Optional<User> findByUsername(String username);
	
	//Find user by email
	Optional<User> findByEmail(String email);
	
	//Check if username exists
	boolean existsByUsername(String username);
	
	//Check if email exists
	boolean existsByEmail(String email);
	
	//Find all users by role
	List<User> findByRole(Role role);
	
    // Count total customers
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'CUSTOMER'")
    Long countCustomers();
}
