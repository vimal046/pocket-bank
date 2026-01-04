package com.pocketbank.config.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pocketbank.entity.FixedDeposit;
import com.pocketbank.entity.User;
import com.pocketbank.entity.enums.FdStatus;
import com.pocketbank.repository.FixedDepositRepository;
import com.pocketbank.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FixedDepositService {

	private final FixedDepositRepository fdRepository;
	private final UserRepository userRepository;
	private final TransactionService transactionService;

	public List<FixedDeposit> getUserFixedDeposits(Long userId) {
		return fdRepository.findByUserIdOrderByStartDateDesc(userId);
	}

	public List<FixedDeposit> getAllFixedDeposits() {
		return fdRepository.findAll();
	}

	// Create fixed deposit
	@Transactional
	public FixedDeposit createFixedDeposit(Long userId,
			String accountNumber,
			BigDecimal principalAmount,
			Integer tenureMonths) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found."));

		// Deduct amount from account
		transactionService.withdraw(accountNumber,
				principalAmount,
				"Fixed Deposit creation for " + tenureMonths + " months");

		// calculate intrest rate
		double interestRate = calculateFdInterestRate(tenureMonths);

		// calculate maturity amount: A = P(1 + r/100)^t
		double maturityAmount = principalAmount.doubleValue()
				* Math.pow(1 + interestRate / 100, tenureMonths / 12.0);

		LocalDateTime maturityDate = LocalDateTime.now()
				.plusMonths(tenureMonths);

		FixedDeposit fd = FixedDeposit.builder()
				.principalAmount(principalAmount)
				.tenureMonths(tenureMonths)
				.interestRate(interestRate)
				.maturityAmount(BigDecimal.valueOf(maturityAmount)
						.setScale(2, RoundingMode.HALF_UP))
				.maturityDate(maturityDate)
				.status(FdStatus.ACTIVE)
				.user(user)
				.build();

		return fdRepository.save(fd);

	}

	// Calculate FD interest rate
	private double calculateFdInterestRate(Integer tenureMonths) {

		if (tenureMonths <= 6) {
			return 5.5;
		} else if (tenureMonths <= 12) {
			return 6.0;
		} else if (tenureMonths <= 24) {
			return 6.5;
		} else {
			return 7.0;
		}
	}

}
