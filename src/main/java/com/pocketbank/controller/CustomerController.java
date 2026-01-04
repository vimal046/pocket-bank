package com.pocketbank.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import com.pocketbank.entity.enums.AccountType;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

	private final UserService userService;
	private final AccountService accountService;
	private final TransactionService transactionService;
	private final LoanService loanService;
	private final FixedDepositService fdService;

	// Customer Dashboard
	@GetMapping("/dashboard")
	public String dashboard(Authentication auth, Model model) {
		User user = userService.findByUsername(auth.getName());
		List<Account> accounts = accountService.getUserAccounts(user.getId());

		model.addAttribute("user", user);
		model.addAttribute("accounts", accounts);

		// Get recent transactions for first account if exists
		if (!accounts.isEmpty()) {
			List<Transaction> recentTransactions = transactionService
					.getRecentTransactions(accounts.get(0)
							.getId());
			model.addAttribute("recentTransactions", recentTransactions);
		}
		return "customer/dashboard";
	}

	// profile management
	@GetMapping("/profile")
	public String profile(Authentication auth, Model model) {
		User user = userService.findByUsername(auth.getName());
		model.addAttribute("user", user);
		return "customer/profile";
	}

	@PostMapping("/profile/update")
	public String updateProfile(@ModelAttribute User updatedUser,
			Authentication auth,
			RedirectAttributes redirectAttributes) {
		try {
			User user = userService.findByUsername(auth.getName());
			userService.updateProfile(user.getId(), updatedUser);
			redirectAttributes.addFlashAttribute("success", "Profile updated successfully");

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/customer/profile";

	}

	// Account Management
	@GetMapping("/accounts")
	public String accounts(Authentication auth, Model model) {
		User user = userService.findByUsername(auth.getName());
		List<Account> accounts = accountService.getUserAccounts(user.getId());
		model.addAttribute("accounts", accounts);
		return "customer/accounts";
	}

	@PostMapping("accounts/create")
	public String createAccount(@RequestParam AccountType accountType,
			Authentication auth,
			RedirectAttributes redirectAttributes) {

		try {
			User user = userService.findByUsername(auth.getName());
			accountService.createAccount(user.getId(), accountType);
			redirectAttributes.addAttribute("success",
					"Account creation request submitted. Awaiting admin approval.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/customer/accounts";
	}

	// Transactions
	@GetMapping("/accounts/{id}/transactions")
	public String accountTransactions(@PathVariable Long id, Authentication auth, Model model) {

		Account account = accountService.findById(id);
		User user = userService.findByUsername(auth.getName());

		// security check: ensure account belongs to user
		if (!account.getUser()
				.getId()
				.equals(user.getId())) {
			return "redirect:/customer/dashboard";
		}

		List<Transaction> transactions = transactionService.getAccountTransactions(id);
		model.addAttribute("account", account);
		model.addAttribute("transactions", transactions);
		return "customer/transactions";

	}

	// Deposit
	@GetMapping("/deposit")
	public String depositPage(Authentication auth, Model model) {
		User user = userService.findByUsername(auth.getName());
		List<Account> accounts = accountService.getUserAccounts(user.getId());
		model.addAttribute("accounts",
				accounts.stream()
						.filter(a -> a.getStatus() == AccountStatus.APPROVED)
						.toList());
		return "customer/deposit";
	}

	@PostMapping("/deposit")
	public String deposit(@RequestParam String accountNumber,
			@RequestParam BigDecimal amount,
			@RequestParam(required = false) String description,
			Authentication auth,
			RedirectAttributes redirectAttributes) {

		try {
			transactionService.deposit(accountNumber, amount, description);
			redirectAttributes.addFlashAttribute("success",
					"Deposit of ₹" + amount + " successful");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/customer/deposit";
	}

	// withdraw
	@GetMapping("/withdraw")
	public String withdrawPage(Authentication auth, Model model) {
		User user = userService.findByUsername(auth.getName());
		List<Account> accounts = accountService.getUserAccounts(user.getId());
		model.addAttribute("accounts",
				accounts.stream()
						.filter(a -> a.getStatus() == AccountStatus.APPROVED)
						.toList());
		return "customer/withdraw";
	}

	@PostMapping("/withdraw")
	public String withdraw(@RequestParam String accountNumber,
			@RequestParam BigDecimal amount,
			@RequestParam(required = false) String description,
			Authentication auth,
			RedirectAttributes redirectAttributes) {

		try {
			transactionService.withdraw(accountNumber, amount, description);
			redirectAttributes.addFlashAttribute("success",
					"Withdrawal of ₹" + amount + " successful");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/customer/withdraw";
	}

	// Transfer
	@GetMapping("/transfer")
	public String transferPage(Authentication auth, Model model) {
		User user = userService.findByUsername(auth.getName());
		List<Account> accounts = accountService.getUserAccounts(user.getId());
		model.addAttribute("accounts",
				accounts.stream()
						.filter(a -> a.getStatus() == AccountStatus.APPROVED)
						.toList());
		return "customer/transfer";
	}

	public String transfer(@RequestParam String fromAccountNumber,
			@RequestParam String toAccountNumber,
			@RequestParam BigDecimal amount,
			@RequestParam(required = false) String description,
			RedirectAttributes redirectAttributes) {

		try {
			transactionService.transfer(fromAccountNumber, toAccountNumber, amount, description);
			redirectAttributes.addFlashAttribute("success",
					"Transfer of ₹" + amount + " successful");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/customer/transfer";
	}

	// Loans
	@GetMapping("/loans")
	public String loans(Authentication auth, Model model) {
		User user = userService.findByUsername(auth.getName());
		List<Loan> loans = loanService.getUserLoans(user.getId());
		model.addAttribute("loans", loans);
		return "customer/loans";
	}

	@PostMapping("/loans/apply")
	public String applyLoan(@RequestParam BigDecimal loanAmount,
			@RequestParam Integer tenureMonths,
			@RequestParam String purpose,
			Authentication auth,
			RedirectAttributes redirectAttributes) {

		try {
			User user = userService.findByUsername(auth.getName());
			loanService.applyForLoan(user.getId(), loanAmount, tenureMonths, purpose);
			redirectAttributes.addFlashAttribute("success",
					"Loan application submitted successfully");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/customer/loans";
	}

	// Fixed Deposit
	@GetMapping("/fixed-deposits")
	public String fixedDeposit(Authentication auth, Model model) {
		User user = userService.findByUsername(auth.getName());
		List<FixedDeposit> fds = fdService.getUserFixedDeposits(user.getId());
		List<Account> accounts = accountService.getUserAccounts(user.getId());

		model.addAttribute("fixedDeposits", fds);
		model.addAttribute("accounts",
				accounts.stream()
						.filter(a -> a.getStatus() == AccountStatus.APPROVED)
						.toList());
		return "customer/fixed-deposits";
	}

	@PostMapping("/fixed-deposits/create")
	public String createFd(@RequestParam String accountNumber,
			@RequestParam BigDecimal principalAmount,
			@RequestParam Integer tenureMonths,
			Authentication auth,
			RedirectAttributes redirectAttributes) {

		try {
			User user = userService.findByUsername(auth.getName());
			fdService
					.createFixedDeposit(user.getId(), accountNumber, principalAmount, tenureMonths);
			redirectAttributes.addFlashAttribute("success", "Fixed Deposit Created Successfully");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/customer/fixed-deposits";
	}
}
