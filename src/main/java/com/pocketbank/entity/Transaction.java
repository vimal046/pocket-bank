package com.pocketbank.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.pocketbank.entity.enums.TransactionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TransactionType type;

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal amount;

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal balanceAfter;

	@Column(length = 500)
	private String description;

	// For transfer recipt account number
	private String recipientAccountNumber;

	@Column(nullable = false)
	@Builder.Default
	private LocalDateTime transactionDate = LocalDateTime.now();

	// Many-to-One
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id", nullable = false)
	@ToString.Exclude
	private Account account;
}
