package com.pocketbank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pocketbank.config.service.AccountService;
import com.pocketbank.entity.Account;
import com.pocketbank.entity.User;
import com.pocketbank.entity.enums.AccountStatus;
import com.pocketbank.entity.enums.AccountType;
import com.pocketbank.repository.AccountRepository;
import com.pocketbank.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Account Service Tests")
public class AccountServiceTest {

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private AccountService accountService;

	private User testUser;
	private Account testAccount;

	@BeforeEach
	void setUp() {
		testUser = User.builder()
				.id(1L)
				.username("testuser")
				.fullName("Test User")
				.build();

		testAccount = Account.builder()
				.accountNumber("PB123456789")
				.accountType(AccountType.SAVINGS)
				.balance(BigDecimal.ZERO)
				.status(AccountStatus.PENDING)
				.user(testUser)
				.build();
	}

	@Test
	@DisplayName("Should create new account successfully")
	void testCreateAccount_Success() {

		// Given
		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
		when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

		// When
		Account result = accountService.createAccount(1L, AccountType.SAVINGS);

		// Then
		assertNotNull(result);
		assertEquals(AccountType.SAVINGS, result.getAccountType());
		assertEquals(AccountStatus.PENDING, result.getStatus());
		verify(accountRepository).save(any(Account.class));
	}

	@Test
	@DisplayName("Should approve account successfully")
	void testApproveAccount_Success() {
		// Given
		when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
		when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

		// when
		Account result = accountService.approveAccount(1L);

		// Then
		assertNotNull(result);
		assertEquals(AccountStatus.APPROVED, result.getStatus());
		verify(accountRepository).save(any(Account.class));
	}

	@Test
	@DisplayName("Should get all user accounts")
	void testGetUserAccounts_Success() {

		// Given
		List<Account> accounts = new ArrayList<>();
		accounts.add(testAccount);
		when(accountRepository.findByUserId(1L)).thenReturn(accounts);

		// When
		List<Account> result = accountService.getUserAccounts(1L);

		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("PB123456789",
				result.get(0)
						.getAccountNumber());
	}

	@Test
	@DisplayName("Should suspend account successfully")
	void testSuspendAccount_Success() {

		// Given
		testAccount.setStatus(AccountStatus.APPROVED);
		when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
		when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

		// when
		Account result = accountService.suspendAccount(1L);

		// Then
		assertNotNull(result);
		assertEquals(AccountStatus.SUSPENDED, result.getStatus());
	}

}
