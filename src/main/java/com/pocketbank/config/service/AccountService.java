package com.pocketbank.config.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pocketbank.entity.Account;
import com.pocketbank.entity.User;
import com.pocketbank.entity.enums.AccountStatus;
import com.pocketbank.entity.enums.AccountType;
import com.pocketbank.repository.AccountRepository;
import com.pocketbank.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;
	private final UserRepository userRepository;

	// get all accounts for a user
	public List<Account> getUserAccounts(Long userId) {
		return accountRepository.findByUserId(userId);
	}

	// create new account
	@Transactional
	public Account createAccount(Long userId, AccountType accountType) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		Account account = Account.builder()
				.accountNumber(generateAccountNumber())
				.accountType(accountType)
				.balance(BigDecimal.ZERO)
				.status(AccountStatus.PENDING)
				.user(user)
				.build();

		return accountRepository.save(account);
	}

	// Generate Unique account number
	private String generateAccountNumber() {

		String accountNumber;
		do {
			accountNumber = "PB" + System.currentTimeMillis() + new Random().nextInt(1000);
		} while (accountRepository.existsByAccountNumber(accountNumber));

		return accountNumber;
	}

	// Get Account by Id
	public Account findById(Long id) {
		return accountRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Account not found"));
	}

	// Get total accounts count
	public Long countAccounts() {
		return accountRepository.countBy();
	}

	// Get total balance across all accounts
	public BigDecimal getTotalBalance() {
		return accountRepository.getTotalBalance();
	}

	// Get pending accounts (for admin approval)
	public List<Account> getPendingAccounts() {
		return accountRepository.findByStatusOrderByCreatedAtDesc(AccountStatus.PENDING);
	}

	// Get All accounts
	public List<Account> getAllAccounts() {
		return accountRepository.findAll();
	}

	// Approve account
	@Transactional
	public Account approveAccount(Long accountId) {
		Account account = findById(accountId);
		account.setStatus(AccountStatus.APPROVED);
		return accountRepository.save(account);
	}

	// Reject/Suspend account
	@Transactional
	public Account suspendAccount(Long accountId) {

		Account account = findById(accountId);
		account.setStatus(AccountStatus.SUSPENDED);
		return accountRepository.save(account);
	}

}
