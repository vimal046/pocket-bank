package com.pocketbank.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.pocketbank.entity.enums.AccountStatus;
import com.pocketbank.entity.enums.AccountType;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true,nullable = false)
	private String accountNumber;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AccountType accountType;
	
	@Column(nullable = false,precision = 15,scale = 2)
	@Builder.Default
	private BigDecimal balance=BigDecimal.ZERO;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private AccountStatus status=AccountStatus.PENDING;
	
	@Column(nullable = false)
	@Builder.Default
	private LocalDateTime createdAt=LocalDateTime.now();
	
	//Many-to-One: Many Accounts belongs to one user
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id",nullable = false)
	@ToString.Exclude
	private User user;
	
	//One-to-Many: One Account has many transactions
	@OneToMany(mappedBy = "account",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@ToString.Exclude
	@Builder.Default
	private List<Transaction> transactions=new ArrayList<>();
}



