package com.pocketbank.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.pocketbank.config.service.UserService;
import com.pocketbank.entity.User;

/*
 * Controller test for AuthController
 * Testing authentication endpoints
 * */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Auth Controller Tests")
public class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	@Test
	@DisplayName("Should display login page")
	void testLoginPage() throws Exception {

		mockMvc.perform(get("/login"))
				.andExpect(status().isOk())
				.andExpectAll(view().name("login"));
	}

	@Test
	@DisplayName("Should display register page")
	void testRegisterPage() throws Exception {
		mockMvc.perform(get("/register"))
				.andExpect(status().isOk())
				.andExpect(view().name("register"))
				.andExpect(model().attributeExists("user"));
	}

	@Test
	@DisplayName("Should register user successfully")
	void testRegisterUser_Success() throws Exception {
		User user = User.builder()
				.username("testuser")
				.password("password123")
				.fullName("Test User")
				.email("test@example.com")
				.phoneNumber("1234567890")
				.address("Test Address")
				.build();

		when(userService.registerUser(any(User.class))).thenReturn(user);

		mockMvc.perform(post("/register").with(csrf())
				.param("username", "testuser")
				.param("password", "password123")
				.param("fullName", "Test User")
				.param("email", "test@example.com")
				.param("phoneNumber", "1234567890")
				.param("address", "Test Address"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));

		verify(userService).registerUser(any(User.class));

	}
}
