package com.pocketbank.config.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pocketbank.entity.Loan;
import com.pocketbank.entity.User;
import com.pocketbank.entity.enums.LoanStatus;
import com.pocketbank.repository.LoanRepository;
import com.pocketbank.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoanService {

	private final LoanRepository loanRepository;
	private final UserRepository userRepository;
	private final TransactionService transactionService;
	private final AccountService accountService;

	// Get user loans
	public List<Loan> getUserLoans(Long userId) {
		return loanRepository.findByUserIdOrderByAppliedAtDesc(userId);
	}

	// Apply for loan
	@Transactional
	public Loan
			applyForLoan(Long userId, BigDecimal loanAmount, Integer tenureMonths, String purpose) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("user not found"));

		// calculate interest rate based on tenure
		double interestRate = calculateIntrestRate(tenureMonths);

		// calculate monthly emi
		BigDecimal monthlyEmi = calculateEmi(loanAmount, interestRate, tenureMonths);

		Loan loan = Loan.builder()
				.loanAmount(loanAmount)
				.tenureMonths(tenureMonths)
				.interestRate(interestRate)
				.monthlyEmi(monthlyEmi)
				.status(LoanStatus.PENDING)
				.purpose(purpose)
				.user(user)
				.build();

		return loanRepository.save(loan);
	}

	// calculate EMI using formula: P*r* (1+r)^n/((1+r)^n-1)
	private BigDecimal calculateEmi(BigDecimal principal, double annualRate, Integer months) {

		double monthlyRate = annualRate / 12 / 100;
		double emi = principal.doubleValue() * monthlyRate * Math.pow(1 + monthlyRate, months)
				/ (Math.pow(1 + monthlyRate, months) - 1);
		return BigDecimal.valueOf(emi)
				.setScale(2, RoundingMode.HALF_UP);
	}

	// calculate interest rate
	private double calculateIntrestRate(Integer tenureMonths) {
		if (tenureMonths <= 12) {
			return 8.5;
		} else if (tenureMonths <= 24) {
			return 9.0;
		} else if (tenureMonths <= 36) {
			return 9.5;
		} else {
			return 10.0;
		}
	}

	// Get pending loans (admin)
	public List<Loan> getPendingLoans() {
		return loanRepository.findByStatus(LoanStatus.PENDING);
	}

	// Get All loans (admin)
	public List<Loan> getAllLoans() {
		return loanRepository.findAll();
	}

	// Approve loan and disburse
	@Transactional
	public Loan approveLoan(Long loanId, String accountNumber) {

		Loan loan = loanRepository.findById(loanId)
				.orElseThrow(() -> new RuntimeException("Loan not found"));

		loan.setStatus(LoanStatus.APPROVED);
		loan.setApprovedAt(LocalDateTime.now());
		loanRepository.save(loan);

		// Disburse loan amount to account
		transactionService.deposit(accountNumber,
				loan.getLoanAmount(),
				"Loan disbursment - Loan ID: " + loanId);
		loan.setStatus(LoanStatus.DISBURSED);
		return loanRepository.save(loan);
	}

	@Transactional
	public Loan rejectLoan(Long loanId) {
		Loan loan = loanRepository.findById(loanId)
				.orElseThrow(() -> new RuntimeException("Loan not found"));
		loan.setStatus(LoanStatus.REJECTED);
		return loanRepository.save(loan);
	}

}
