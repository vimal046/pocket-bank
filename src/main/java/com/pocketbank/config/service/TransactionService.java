package com.pocketbank.config.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pocketbank.entity.Transaction;
import com.pocketbank.repository.AccountRepository;
import com.pocketbank.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

	private final TransactionRepository transactionRepository;
	private final AccountRepository accountRepository;
	
	//Get recent transactions for an account
	public List<Transaction> getRecentTransactions(Long accountId) {
		return transactionRepository.findTop10ByAccountIdOrderByTransactionDateDesc(accountId);
	}

}
