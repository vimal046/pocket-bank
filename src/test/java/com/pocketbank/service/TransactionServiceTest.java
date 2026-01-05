package com.pocketbank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
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

import com.pocketbank.config.service.TransactionService;
import com.pocketbank.entity.Account;
import com.pocketbank.entity.Transaction;
import com.pocketbank.entity.enums.AccountStatus;
import com.pocketbank.entity.enums.AccountType;
import com.pocketbank.entity.enums.TransactionType;
import com.pocketbank.repository.AccountRepository;
import com.pocketbank.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Transaction ServiceTest")
public class TransactionServiceTest {
	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private AccountRepository accountRepository;

	@InjectMocks
	private TransactionService transactionService;

	private Account testAccount;
	private Transaction testTransaction;

	@BeforeEach
	void setUp() {
		testAccount = Account.builder()
				.id(1L)
				.accountNumber("PB123456789")
				.accountType(AccountType.SAVINGS)
				.balance(new BigDecimal("1000.00"))
				.status(AccountStatus.APPROVED)
				.build();

		testTransaction = Transaction.builder()
				.id(1L)
				.type(TransactionType.DEPOSIT)
				.amount(new BigDecimal("500.00"))
				.balanceAfter(new BigDecimal("1500.00"))
				.description("Test deposit")
				.account(testAccount)
				.build();
	}

	@Test
	@DisplayName("Should deposit money successfully")
	void testDeposit_Success() {
		// Given
		BigDecimal depositAmount = new BigDecimal("500.00");
		when(accountRepository.findByAccountNumber("PB123456789"))
				.thenReturn(Optional.of(testAccount));
		when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
		when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

		// When
		Transaction result = transactionService
				.deposit("PB123456789", depositAmount, "Test deposit");

		// Then
		assertNotNull(result);
		assertEquals(TransactionType.DEPOSIT, result.getType());
		assertEquals(depositAmount, result.getAmount());
		verify(accountRepository).save(any(Account.class));
		verify(transactionRepository).save(any(Transaction.class));
	}

	@Test
	@DisplayName("Should throw exception for negative deposit amount")
	void testDeposit_NegativeAmount() {
		// Given
		BigDecimal negativeAmount = new BigDecimal("-100.00");

		// When & Then
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> transactionService.deposit("PB123456789", negativeAmount, "Test"));
		assertEquals("Amount must be posetive", exception.getMessage());
	}

	@Test
	@DisplayName("Should withdraw money successfully")
	void testWithdraw_Success() {
		// Given
		BigDecimal withdrawAmount = new BigDecimal("300.00");
		when(accountRepository.findByAccountNumber("PB123456789"))
				.thenReturn(Optional.of(testAccount));
		when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
		when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

		// When
		Transaction result = transactionService
				.withdraw("PB123456789", withdrawAmount, "Test withdraw");

		// Then
		assertNotNull(result);
		verify(accountRepository).save(any(Account.class));
		verify(transactionRepository).save(any(Transaction.class));
	}

	@Test
	@DisplayName("Should throw exception for insufficient balance")
	void testWithdraw_InsufficientBalance() {
		// Given
		BigDecimal withdrawAmount = new BigDecimal("2000.00"); // More than balance
		when(accountRepository.findByAccountNumber("PB123456789"))
				.thenReturn(Optional.of(testAccount));

		// When & Then
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> transactionService.withdraw("PB123456789", withdrawAmount, "Test"));
		assertEquals("Insufficient balance", exception.getMessage());
	}

	@Test
	@DisplayName("Should transfer money successfully")
	void testTransfer_Success() {
		// Given
		Account toAccount = Account.builder()
				.id(2L)
				.accountNumber("PB987654321")
				.balance(new BigDecimal("500.00"))
				.status(AccountStatus.APPROVED)
				.build();

		BigDecimal transferAmount = new BigDecimal("200.00");

		when(accountRepository.findByAccountNumber("PB123456789"))
				.thenReturn(Optional.of(testAccount));
		when(accountRepository.findByAccountNumber("PB987654321"))
				.thenReturn(Optional.of(toAccount));
		when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
		when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

		// When
		transactionService.transfer("PB123456789", "PB987654321", transferAmount, "Test transfer");

		// Then
		verify(accountRepository, times(2)).save(any(Account.class));
		verify(transactionRepository, times(2)).save(any(Transaction.class));
	}

	@Test
	@DisplayName("Should throw exception for same account transfer")
	void testTransfer_SameAccount() {
		// Given
		BigDecimal amount = new BigDecimal("100.00");

		// When & Then
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> transactionService.transfer("PB123456789", "PB123456789", amount, "Test"));
		assertEquals("Cannot transfer to the same account.", exception.getMessage());
	}

	@Test
	@DisplayName("Should get account transactions")
	void testGetAccountTransactions_Success() {
		// Given
		List<Transaction> transactions = new ArrayList<>();
		transactions.add(testTransaction);
		when(transactionRepository.findByAccountIdOrderByTransactionDateDesc(1L))
				.thenReturn(transactions);

		// When
		List<Transaction> result = transactionService.getAccountTransactions(1L);

		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
	}

	@Test
	@DisplayName("Should calculate total deposits")
	void testGetTotalDeposits() {
		// Given
		BigDecimal expectedTotal = new BigDecimal("5000.00");
		when(transactionRepository.getTotalDeposits()).thenReturn(expectedTotal);

		// When
		BigDecimal result = transactionService.getTotalDeposits();

		// Then
		assertEquals(expectedTotal, result);
	}
}
