package com.pocketbank.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pocketbank.config.service.AccountService;
import com.pocketbank.config.service.FixedDepositService;
import com.pocketbank.config.service.LoanService;
import com.pocketbank.config.service.TransactionService;
import com.pocketbank.config.service.UserService;
import com.pocketbank.entity.Account;
import com.pocketbank.entity.FixedDeposit;
import com.pocketbank.entity.Loan;
import com.pocketbank.entity.Transaction;
import com.pocketbank.entity.User;
import com.pocketbank.entity.enums.AccountStatus;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

	private final UserService userService;
	private final AccountService accountService;
	private final TransactionService transactionService;
	private final LoanService loanService;
	private final FixedDepositService fdService;

	// Admin Dashboard
	@GetMapping("/dashboard")
	public String dashboard(Model model) {

		// statics
		Long totalCustomers = userService.countCustomers();
		Long totalAccounts = accountService.countAccounts();
		Long totalTransactions = transactionService.countTransactions();
		BigDecimal totalBalance = accountService.getTotalBalance();

		// Recent data
		List<Transaction> recentTransaction = transactionService.getAllRecentTransactions();
		List<Account> pendingAccounts = accountService.getPendingAccounts();
		List<Loan> pendingLoans = loanService.getPendingLoans();

		// Financial summary
		BigDecimal totalDeposits = transactionService.getTotalDeposits();
		BigDecimal totalWithdrawals = transactionService.getTotalWithdrawals();

		model.addAttribute("totalCustomers", totalCustomers);
		model.addAttribute("totalAccounts", totalAccounts);
		model.addAttribute("totalTransactions", totalTransactions);
		model.addAttribute("totalBalance", totalBalance);
		model.addAttribute("totalDeposits", totalDeposits);
		model.addAttribute("totalWithdrawals", totalWithdrawals);
		model.addAttribute("recentTransaction", recentTransaction);
		model.addAttribute("pendingAccounts", pendingAccounts);
		model.addAttribute("pendingLoans", pendingLoans);

		return null;
	}

	// User Management
	@GetMapping("/users")
	public String users(Model model) {

		List<User> customers = userService.getAllCustomers();
		model.addAttribute("users", customers);
		return "admin/users";
	}

	@GetMapping("/users/{id}")
	public String userDetails(@PathVariable Long id, Model model) {
		User user = userService.findById(id);
		List<Account> accounts = accountService.getUserAccounts(id);
		List<Loan> loans = loanService.getUserLoans(id);
		List<FixedDeposit> fds = fdService.getUserFixedDeposits(id);

		model.addAttribute("user", user);
		model.addAttribute("accounts", accounts);
		model.addAttribute("loans", loans);
		model.addAttribute("fixedDeposits", fds);

		return "admin/user-details";
	}

	// Account Management
	@GetMapping("/accounts")
	public String accounts(Model model) {
		List<Account> accounts = accountService.getAllAccounts();
		model.addAttribute("accounts", accounts);
		return "admin/accounts";
	}

	@GetMapping("/accounts/pending")
	public String pendingAccounts(Model model) {
		List<Account> pendingAccounts = accountService.getPendingAccounts();
		model.addAttribute("accounts", pendingAccounts);
		return "admin/pending-accounts";
	}

	@PostMapping("/accounts/{id}/approve")
	public String approveAccount(@PathVariable Long id, RedirectAttributes redirectAttributes) {

		try {
			accountService.approveAccount(id);
			redirectAttributes.addFlashAttribute("success", "Account approved successfully");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/admin/accounts/pending";
	}

	@PostMapping("/accounts/{id}/suspend")
	public String suspendAccount(@PathVariable Long id, RedirectAttributes redirectAttributes) {

		try {
			accountService.suspendAccount(id);
			redirectAttributes.addFlashAttribute("success", "Account suspended successfully");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return null;
	}

	// Transaction Management
	@GetMapping("/transactions")
	public String transactions(Model model) {
		List<Transaction> transactions = transactionService.getAllRecentTransactions();
		model.addAttribute("transactions", transactions);
		return "admin/transactions";
	}

	// Loan Management
	@GetMapping("/loans")
	public String loans(Model model) {
		List<Loan> allLoans = loanService.getAllLoans();
		List<Loan> pendingLoans = loanService.getPendingLoans();

		model.addAttribute("loans", allLoans);
		model.addAttribute("pendingLoans", pendingLoans);
		return "admin/loans";
	}

	@GetMapping("/loans/{id}")
	public String loanDetails(@PathVariable Long id, Model model) {
		Loan loan = loanService.getUserLoans(id)
				.stream()
				.filter(l -> l.getId()
						.equals(id))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Loan not found"));

		List<Account> userAccounts = accountService.getUserAccounts(loan.getUser()
				.getId());

		model.addAttribute("loan", loan);
		model.addAttribute("accounts",
				userAccounts.stream()
						.filter(a -> a.getStatus() == AccountStatus.APPROVED)
						.toList());

		return "admin/loan-details";
	}

	@PostMapping("/loans/{id}/approve")
	public String approveLoan(@PathVariable Long id,
			@RequestParam String accountNumber,
			RedirectAttributes redirectAttributes) {
		try {
			loanService.approveLoan(id, accountNumber);
			redirectAttributes.addFlashAttribute("success",
					"Loan approved and disbursed successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/admin/loans";
	}

	@PostMapping("/loans/{id}/reject")
	public String rejectLoan(@PathVariable Long id, RedirectAttributes redirectAttributes) {

		try {
			loanService.rejectLoan(id);
			redirectAttributes.addFlashAttribute("success", "Loan rejected");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/admin/loans";
	}

	// Fixed deposits management
	@GetMapping("/fixed-deposits")
	public String fixedDeposits(Model model) {
		List<FixedDeposit> fds = fdService.getAllFixedDeposits();
		model.addAttribute("fixedDeposits", fds);
		return "admin/fixed-deposits";
	}

	// Reports
	@GetMapping("/reports")
	public String reports(Model model) {
		// Financial summary
		BigDecimal totalDeposits = transactionService.getTotalDeposits();
		BigDecimal totalWithdrawals = transactionService.getTotalWithdrawals();
		BigDecimal totalBalance = accountService.getTotalBalance();

		// Counts
		Long totalCustomers = userService.countCustomers();
		Long totalAccounts = accountService.countAccounts();
		Long totalTransactions = transactionService.countTransactions();

		model.addAttribute("totalDeposits", totalDeposits);
		model.addAttribute("totalWithdrawals", totalWithdrawals);
		model.addAttribute("totalBalance", totalBalance);
		model.addAttribute("totalCustomers", totalCustomers);
		model.addAttribute("totalAccounts", totalAccounts);
		model.addAttribute("totalTransactions", totalTransactions);

		return "admin/reports";
	}
}
