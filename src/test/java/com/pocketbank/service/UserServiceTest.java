package com.pocketbank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pocketbank.config.service.UserService;
import com.pocketbank.entity.User;
import com.pocketbank.entity.enums.Role;
import com.pocketbank.repository.UserRepository;

/*
 * Unit Tests for UserService
 * Testing user registeration,authentication , and profile management
 * */
@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Tests")
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	private User testUser;

	@BeforeEach
	void setUp() {
		testUser = User.builder()
				.id(1L)
				.username("testuser")
				.password("password123")
				.fullName("Test User")
				.email("test@example.com")
				.phoneNumber("1234567890")
				.address("Test Address")
				.role(Role.CUSTOMER)
				.enabled(true)
				.build();
	}

	@Test
	@DisplayName("Should register new user successfully")
	void testRegisterUser_Success() {

		// Given
		when(userRepository.existsByUsername(anyString())).thenReturn(false);
		when(userRepository.existsByEmail(anyString())).thenReturn(false);
		when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenReturn(testUser);

		// When
		User result = userService.registerUser(testUser);

		// Then
		assertNotNull(result);
		assertEquals("testuser", result.getUsername());
		verify(userRepository).save(any(User.class));
		verify(passwordEncoder).encode(anyString());
	}

	@Test
	@DisplayName("Should throw exception when username exists.")
	void testRegisterUser_UsernameExists() {

		// Given
		when(userRepository.existsByUsername(anyString())).thenReturn(true);

		// When & Then
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> userService.registerUser(testUser));

		assertEquals("Username already exists", exception.getMessage());

		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	@DisplayName("Should throw exception when email already exists")
	void testRegisterUser_EmailExists() {
		// Given
		when(userRepository.existsByUsername(anyString())).thenReturn(false);
		when(userRepository.existsByEmail(anyString())).thenReturn(true);

		// When & Then
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> userService.registerUser(testUser));

		assertEquals("Email already exists", exception.getMessage());
	}

	@Test
	@DisplayName("Should find user by username")
	void testFindByUsername_Success() {
		// Given
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

		// When
		User result = userService.findByUsername("testuser");

		// Then
		assertNotNull(result);
		assertEquals("testuser", result.getUsername());
	}

	@Test
	@DisplayName("Should throw exception when user not found")
	void testFindByUsername_NotFound() {
		// Given
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

		// When & Then
		assertThrows(RuntimeException.class, () -> userService.findByUsername("nonexistent"));

	}

	@Test
	@DisplayName("Should update user profile successfully")
	void testUpdateProfile_Success() {
		// Given
		User updatedUser = User.builder()
				.fullName("Updated Name")
				.email("updated@example.com")
				.phoneNumber("9876543210")
				.address("New Address")
				.build();

		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		when(userRepository.save(any(User.class))).thenReturn(testUser);

		// When
		User result = userService.updateProfile(1L, updatedUser);

		// Then
		assertNotNull(result);
		verify(userRepository).save(any(User.class));
	}
}
