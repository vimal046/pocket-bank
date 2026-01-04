package com.pocketbank.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pocketbank.entity.Account;
import com.pocketbank.entity.enums.AccountStatus;

/**
 * Repository for Account entity operations Handles database queries related to
 * accounts
 */

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

	// find account by Account number
	Optional<Account> findByAccountNumber(String accountNumber);

	// find all accounts for a specific user
	List<Account> findByUserId(Long userId);

	// Find all accounts by status
	List<Account> findByStatus(AccountStatus status);

	boolean existsByAccountNumber(String accountNumber);

	// count total accounts
	Long countBy();

	// sum of all balances across all accounts
	@Query("SELECT COALESCE(SUM(a.balance),0) FROM Account a WHERE a.status = 'APPROVED'")
	BigDecimal getTotalBalance();

	// Find all pending accounts (for admin approval)
	List<Account> findByStatusOrderByCreatedAtDesc(AccountStatus pending);

}
