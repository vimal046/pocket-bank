package com.pocketbank.config.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pocketbank.entity.Account;
import com.pocketbank.entity.Transaction;
import com.pocketbank.entity.enums.AccountStatus;
import com.pocketbank.entity.enums.TransactionType;
import com.pocketbank.repository.AccountRepository;
import com.pocketbank.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

	private final TransactionRepository transactionRepository;
	private final AccountRepository accountRepository;

	// Get recent transactions for an account
	public List<Transaction> getRecentTransactions(Long accountId) {
		return transactionRepository.findTop10ByAccountIdOrderByTransactionDateDesc(accountId);
	}

	// Get transaction history for an account
	public List<Transaction> getAccountTransactions(Long accountId) {
		return transactionRepository.findByAccountIdOrderByTransactionDateDesc(accountId);
	}

	// Deposit money
	@Transactional
	public Transaction deposit(String accountNumber, BigDecimal amount, String description) {
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new RuntimeException("Amount must be posetive");
		}

		Account account = accountRepository.findByAccountNumber(accountNumber)
				.orElseThrow(() -> new RuntimeException("Account not found"));

		if (account.getStatus() != AccountStatus.APPROVED) {
			throw new RuntimeException("Account is not active");
		}

		// update balance
		account.setBalance(account.getBalance()
				.add(amount));
		accountRepository.save(account);

		// create transaction record
		Transaction transaction = Transaction.builder()
				.type(TransactionType.DEPOSIT)
				.amount(amount)
				.balanceAfter(account.getBalance())
				.description(description != null ? description : "Deposit")
				.account(account)
				.build();

		return transactionRepository.save(transaction);
	}

	// withdraw money
	@Transactional
	public Transaction withdraw(String accountNumber, BigDecimal amount, String description) {

		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new RuntimeException("Amount must be posetive");
		}

		Account account = accountRepository.findByAccountNumber(accountNumber)
				.orElseThrow(() -> new RuntimeException("Account not found"));

		if (account.getStatus() != AccountStatus.APPROVED) {
			throw new RuntimeException("Account is not active.");
		}

		if (account.getBalance()
				.compareTo(amount) < 0) {
			throw new RuntimeException("Insufficient balance");
		}

		// update balance
		account.setBalance(account.getBalance()
				.subtract(amount));
		accountRepository.save(account);

		// create transaction record
		Transaction transaction = Transaction.builder()
				.type(TransactionType.WITHDRAWAL)
				.amount(amount)
				.balanceAfter(account.getBalance())
				.description(description != null ? description : "Withdrawal")
				.account(account)
				.build();

		return transactionRepository.save(transaction);
	}

	// Transfer money between accounts
	@Transactional
	public void transfer(String fromAccountNumber,
			String toAccountNumber,
			BigDecimal amount,
			String description) {

		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new RuntimeException("Amount must be posetive");
		}

		if (fromAccountNumber.equals(toAccountNumber)) {
			throw new RuntimeException("Cannot transfer to the same account.");
		}

		Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
				.orElseThrow(() -> new RuntimeException("Source account not found."));

		Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
				.orElseThrow(() -> new RuntimeException("Destination account not found."));

		if (fromAccount.getStatus() != AccountStatus.APPROVED
				|| toAccount.getStatus() != AccountStatus.APPROVED) {

			throw new RuntimeException("Account is not active");
		}

		if (fromAccount.getBalance()
				.compareTo(amount) < 0) {
			throw new RuntimeException("Insufficient balance");
		}

		// Debit from source account
		fromAccount.setBalance(fromAccount.getBalance()
				.subtract(amount));
		accountRepository.save(fromAccount);

		Transaction debitTxn = Transaction.builder()
				.type(TransactionType.TRANSFER_OUT)
				.amount(amount)
				.balanceAfter(fromAccount.getBalance())
				.recipientAccountNumber(toAccountNumber)
				.description(description != null ? description : "Transfer to " + toAccountNumber)
				.account(fromAccount)
				.build();
		transactionRepository.save(debitTxn);

		// credit to destination account
		toAccount.setBalance(toAccount.getBalance()
				.add(amount));
		accountRepository.save(toAccount);

		Transaction creditTxn = Transaction.builder()
				.type(TransactionType.TRANSFER_IN)
				.amount(amount)
				.balanceAfter(toAccount.getBalance())
				.recipientAccountNumber(fromAccountNumber)
				.description(
						description != null ? description : "Transfer from " + fromAccountNumber)
				.account(toAccount)
				.build();
		transactionRepository.save(creditTxn);
	}

	public Long countTransactions() {
		return transactionRepository.countBy();
	}

	// Get all recent transactions (admin)
	public List<Transaction> getAllRecentTransactions() {
		return transactionRepository.findTop20ByOrderByTransactionDateDesc();
	}

	// Get transaction statistics
	public BigDecimal getTotalDeposits() {
		return transactionRepository.getTotalDeposits();
	}

	public BigDecimal getTotalWithdrawals() {
		return transactionRepository.getTotalWithdrawals();
	}

}
