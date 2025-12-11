package com.pocketbank.config.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pocketbank.entity.Account;
import com.pocketbank.repository.AccountRepository;
import com.pocketbank.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;
	private final UserRepository userRepository;

	// get all accounts for a user
	public List<Account> getUserAccounts(Long userId) {
		return accountRepository.findByUserId(userId);
	}

}
