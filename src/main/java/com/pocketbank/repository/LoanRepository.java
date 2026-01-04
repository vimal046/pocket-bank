package com.pocketbank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pocketbank.entity.Loan;
import com.pocketbank.entity.enums.LoanStatus;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

	// Find all loans for a specific user
	List<Loan> findByUserIdOrderByAppliedAtDesc(Long userId);

	// Find all pending loans (for admin approval)
	List<Loan> findByStatus(LoanStatus status);

}
