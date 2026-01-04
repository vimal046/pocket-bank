package com.pocketbank.config.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pocketbank.entity.User;
import com.pocketbank.entity.enums.Role;
import com.pocketbank.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	// Register new user
	@Transactional
	public User registerUser(User user) {
		if (userRepository.existsByUsername(user.getUsername())) {
			throw new RuntimeException("Username already exists");
		}
		if (userRepository.existsByEmail(user.getEmail())) {
			throw new RuntimeException("Email already exists");
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole(Role.CUSTOMER);
		user.setEnabled(true);
		return userRepository.save(user);
	}

	// Find user by username
	public User findByUsername(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("User not found"));
	}

	// Find user by ID
	public User findById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("User not found"));
	}

	// Get all users
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	// Get all customers
	public List<User> getAllCustomers() {
		return userRepository.findByRole(Role.CUSTOMER);
	}

	// Update user profile
	@Transactional
	public User updateProfile(Long userId, User updatedUser) {
		User user = findById(userId);
		user.setFullName(updatedUser.getFullName());
		user.setEmail(updatedUser.getEmail());
		user.setPhoneNumber(updatedUser.getPhoneNumber());
		user.setAddress(updatedUser.getAddress());
		return userRepository.save(user);
	}

	// Count total customers
	public Long countCustomers() {
		return userRepository.countCustomers();
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		var user = findByUsername(username);

		if (user == null) {
			throw new UsernameNotFoundException("User not found: " + username);
		}

		return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
				.password(user.getPassword())
				.disabled(!user.getEnabled())
				.authorities(user.getRole()
						.name())
				.build();
	}
}
