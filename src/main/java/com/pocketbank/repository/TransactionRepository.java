package com.pocketbank.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pocketbank.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	// find all transaction for a specific account with limit
	List<Transaction> findTop10ByAccountIdOrderByTransactionDateDesc(Long accountId);

	// find all transaction for a specific account
	List<Transaction> findByAccountIdOrderByTransactionDateDesc(Long accountId);

	// count total transactions
	Long countBy();

	// Get recent transactions across all accounts (for admin)
	List<Transaction> findTop20ByOrderByTransactionDateDesc();

	// calculae total deposits
	@Query("SELECT COALESCE(SUM(t.amount),0) FROM Transaction t WHERE t.type = 'DEPOSIT'")
	BigDecimal getTotalDeposits();

	// calculate total withdrawals
	@Query("SELECT COALESCE(SUM(t.amount),0) FROM Transaction t WHERE t.type = 'WITHDRAWAL'")
	BigDecimal getTotalWithdrawals();

}
