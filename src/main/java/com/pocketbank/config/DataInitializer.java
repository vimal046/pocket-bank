package com.pocketbank.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.pocketbank.entity.Account;
import com.pocketbank.entity.FixedDeposit;
import com.pocketbank.entity.Loan;
import com.pocketbank.entity.Transaction;
import com.pocketbank.entity.User;
import com.pocketbank.entity.enums.AccountStatus;
import com.pocketbank.entity.enums.AccountType;
import com.pocketbank.entity.enums.FdStatus;
import com.pocketbank.entity.enums.LoanStatus;
import com.pocketbank.entity.enums.Role;
import com.pocketbank.entity.enums.TransactionType;
import com.pocketbank.repository.AccountRepository;
import com.pocketbank.repository.FixedDepositRepository;
import com.pocketbank.repository.LoanRepository;
import com.pocketbank.repository.TransactionRepository;
import com.pocketbank.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

	private final UserRepository userRepository;
	private final AccountRepository accountRepository;
	private final TransactionRepository transactionRepository;
	private final LoanRepository loanRepository;
	private final FixedDepositRepository fdRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {

		// Check if data already exists

		if (userRepository.count() > 0) {
			System.out.println("Database already initialised. Skipping data seeding.");
			return;
		}

		System.out.println("===========================================");
		System.out.println("   Initializing Sample Data...");
		System.out.println("===========================================");

		// create admin user
		User admin = User.builder()
				.username("admin")
				.password(passwordEncoder.encode("admin123"))
				.fullName("Admin User")
				.email("admin@pocketbank.com")
				.phoneNumber("9876543210")
				.address("PocketBank HQ, Chennai, India")
				.role(Role.ADMIN)
				.enabled(true)
				.createdAt(LocalDateTime.now())
				.build();
		userRepository.save(admin);
		System.out.println("✅ Created admin user (username: admin, password : admin123)");

		// Create Customer Users
		User john = User.builder()
				.username("john")
				.password(passwordEncoder.encode("password"))
				.fullName("John S")
				.email("johns123@gmail.com")
				.phoneNumber("9876543211")
				.address("123 Main Street, Chennai, Tamil Nadu")
				.role(Role.CUSTOMER)
				.enabled(true)
				.createdAt(LocalDateTime.now()
						.minusDays(30))
				.build();
		userRepository.save(john);

		User sara = User.builder()
				.username("sara")
				.password(passwordEncoder.encode("password"))
				.fullName("Sara George")
				.email("sarageorge254@gmail.com")
				.phoneNumber("9876543212")
				.address("456 Park Avenue, Chennai, Tamil Nadu")
				.role(Role.CUSTOMER)
				.enabled(true)
				.createdAt(LocalDateTime.now()
						.minusDays(20))
				.build();
		userRepository.save(sara);

		User mike = User.builder()
				.username("mike")
				.password(passwordEncoder.encode("password"))
				.fullName("Mike Johnson")
				.email("mikejohnson352@gmail.com")
				.phoneNumber("9876543213")
				.address("789 Lake Road, Chennai, Tamil Nadu")
				.role(Role.CUSTOMER)
				.enabled(true)
				.createdAt(LocalDateTime.now()
						.minusDays(15))
				.build();
		userRepository.save(mike);

		System.out.println(
				"✅ Created 3 Customer users (username: john/sara/mike, password: password)");

		// Created Accounts for john
		Account johnSavings = Account.builder()
				.accountNumber("PB" + System.currentTimeMillis() + "001")
				.accountType(AccountType.SAVINGS)
				.balance(new BigDecimal("50000.00"))
				.status(AccountStatus.APPROVED)
				.user(john)
				.createdAt(LocalDateTime.now()
						.minusDays(28))
				.build();
		accountRepository.save(johnSavings);

		Account johnChecking = Account.builder()
				.accountNumber("PB" + System.currentTimeMillis() + "002")
				.accountType(AccountType.CHECKING)
				.balance(new BigDecimal("25000.00"))
				.status(AccountStatus.APPROVED)
				.user(john)
				.createdAt(LocalDateTime.now()
						.minusDays(25))
				.build();
		accountRepository.save(johnChecking);

		// Create Accounts for Sarah
		Account sarahSavings = Account.builder()
				.accountNumber("PB" + System.currentTimeMillis() + "003")
				.accountType(AccountType.SAVINGS)
				.balance(new BigDecimal("75000.00"))
				.status(AccountStatus.APPROVED)
				.user(sara)
				.createdAt(LocalDateTime.now()
						.minusDays(18))
				.build();
		accountRepository.save(sarahSavings);

		// Create Pending Account for Mike
		Account mikePending = Account.builder()
				.accountNumber("PB" + System.currentTimeMillis() + "004")
				.accountType(AccountType.SAVINGS)
				.balance(BigDecimal.ZERO)
				.status(AccountStatus.PENDING)
				.user(mike)
				.createdAt(LocalDateTime.now()
						.minusDays(2))
				.build();
		accountRepository.save(mikePending);

		System.out.println("✓ Created 4 Accounts (3 approved, 1 pending)");

		// Create Sample Transactions for John's Savings Account
		Transaction tx1 = Transaction.builder()
				.type(TransactionType.DEPOSIT)
				.amount(new BigDecimal("30000.00"))
				.balanceAfter(new BigDecimal("30000.00"))
				.description("Initial Deposit")
				.account(johnSavings)
				.transactionDate(LocalDateTime.now()
						.minusDays(27))
				.build();
		transactionRepository.save(tx1);

		Transaction tx2 = Transaction.builder()
				.type(TransactionType.DEPOSIT)
				.amount(new BigDecimal("15000.00"))
				.balanceAfter(new BigDecimal("45000.00"))
				.description("Salary Credit")
				.account(johnSavings)
				.transactionDate(LocalDateTime.now()
						.minusDays(15))
				.build();
		transactionRepository.save(tx2);

		Transaction tx3 = Transaction.builder()
				.type(TransactionType.WITHDRAWAL)
				.amount(new BigDecimal("5000.00"))
				.balanceAfter(new BigDecimal("40000.00"))
				.description("ATM Withdrawal")
				.account(johnSavings)
				.transactionDate(LocalDateTime.now()
						.minusDays(10))
				.build();
		transactionRepository.save(tx3);

		Transaction tx4 = Transaction.builder()
				.type(TransactionType.DEPOSIT)
				.amount(new BigDecimal("10000.00"))
				.balanceAfter(new BigDecimal("50000.00"))
				.description("Cash Deposit")
				.account(johnSavings)
				.transactionDate(LocalDateTime.now()
						.minusDays(5))
				.build();
		transactionRepository.save(tx4);

		// Transactions for John's Checking Account
		Transaction tx5 = Transaction.builder()
				.type(TransactionType.DEPOSIT)
				.amount(new BigDecimal("25000.00"))
				.balanceAfter(new BigDecimal("25000.00"))
				.description("Initial Deposit")
				.account(johnChecking)
				.transactionDate(LocalDateTime.now()
						.minusDays(24))
				.build();
		transactionRepository.save(tx5);

		// Transactions for Sarah's Account
		Transaction tx6 = Transaction.builder()
				.type(TransactionType.DEPOSIT)
				.amount(new BigDecimal("50000.00"))
				.balanceAfter(new BigDecimal("50000.00"))
				.description("Initial Deposit")
				.account(sarahSavings)
				.transactionDate(LocalDateTime.now()
						.minusDays(17))
				.build();
		transactionRepository.save(tx6);

		Transaction tx7 = Transaction.builder()
				.type(TransactionType.DEPOSIT)
				.amount(new BigDecimal("25000.00"))
				.balanceAfter(new BigDecimal("75000.00"))
				.description("Bonus Credit")
				.account(sarahSavings)
				.transactionDate(LocalDateTime.now()
						.minusDays(8))
				.build();
		transactionRepository.save(tx7);

		System.out.println("✓ Created 7 Sample Transactions");

		// Create Sample Loans
		Loan johnLoan = Loan.builder()
				.loanAmount(new BigDecimal("100000.00"))
				.tenureMonths(24)
				.interestRate(9.0)
				.monthlyEmi(new BigDecimal(4567.89))
				.status(LoanStatus.APPROVED)
				.purpose("Home Renovation")
				.appliedAt(LocalDateTime.now()
						.minusDays(20))
				.approvedAt(LocalDateTime.now()
						.minusDays(18))
				.user(john)
				.build();
		loanRepository.save(johnLoan);

		Loan mikeLoan = Loan.builder()
				.loanAmount(new BigDecimal("50000.00"))
				.tenureMonths(12)
				.interestRate(8.5)
				.monthlyEmi(new BigDecimal("4370.12"))
				.status(LoanStatus.PENDING)
				.purpose("Personal Loan")
				.appliedAt(LocalDateTime.now()
						.minusDays(3))
				.approvedAt(LocalDateTime.now()
						.minusDays(2))
				.user(mike)
				.build();
		loanRepository.save(mikeLoan);

		System.out.println("✓ Created 2 Sample Loans (1 approved, 1 pending)");

		// Create Sample Fixed Deposits
		FixedDeposit sarahFd = FixedDeposit.builder()
				.principalAmount(new BigDecimal("100000.00"))
				.tenureMonths(12)
				.interestRate(6.0)
				.maturityAmount(new BigDecimal("106000.00"))
				.startDate(LocalDateTime.now()
						.minusDays(10))
				.maturityDate(LocalDateTime.now()
						.minusDays(10)
						.plusMonths(12))
				.status(FdStatus.ACTIVE)
				.user(sara)
				.build();
		fdRepository.save(sarahFd);

		FixedDeposit johnFd = FixedDeposit.builder()
				.principalAmount(new BigDecimal("50000.00"))
				.tenureMonths(6)
				.interestRate(5.5)
				.maturityAmount(new BigDecimal("51375.00"))
				.startDate(LocalDateTime.now()
						.minusDays(5))
				.maturityDate(LocalDateTime.now()
						.minusDays(5)
						.plusMonths(6))
				.status(FdStatus.ACTIVE)
				.user(john)
				.build();
		fdRepository.save(johnFd);

		System.out.println("✓ Created 2 Sample Fixed Deposits");

		System.out.println("===========================================");
		System.out.println("   Sample Data Initialization Complete!");
		System.out.println("===========================================");
		System.out.println("\nLOGIN CREDENTIALS:");
		System.out.println("------------------------------------------");
		System.out.println("Admin:    username: admin    password: admin123");
		System.out.println("Customer: username: john     password: password");
		System.out.println("Customer: username: sarah    password: password");
		System.out.println("Customer: username: mike     password: password");
		System.out.println("===========================================\n");

	}

}
