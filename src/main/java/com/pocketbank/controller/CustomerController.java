package com.pocketbank.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pocketbank.config.service.AccountService;
import com.pocketbank.config.service.FixedDepositService;
import com.pocketbank.config.service.LoanService;
import com.pocketbank.config.service.TransactionService;
import com.pocketbank.config.service.UserService;
import com.pocketbank.entity.Account;
import com.pocketbank.entity.Transaction;
import com.pocketbank.entity.User;

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

	//Customer Dashboard
	@GetMapping("/dashboard")
	public String dashboard(Authentication auth,
			Model model) {
		User user = userService.findByUsername(auth.getName());
		List<Account> accounts = accountService.getUserAccounts(user.getId());

		model.addAttribute("user", user);
		model.addAttribute("accounts", accounts);

		// Get recent transactions for first account if exists
		if (!accounts.isEmpty()) {
			List<Transaction> recentTransactions = transactionService.getRecentTransactions(accounts.get(0)
					.getId());
			model.addAttribute("recentTransactions", recentTransactions);
		}
		return "customer/dashboard";
	}
}
