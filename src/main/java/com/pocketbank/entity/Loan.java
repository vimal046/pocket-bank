package com.pocketbank.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false,precision = 15,scale = 2)
	private BigDecimal loanAmount;
	
	@Column(nullable = false)
	private Integer tenureMonths;
	
	@Column(nullable = false)
	private Double intrestRate;
	
	@Column(nullable = false,precision = 15,scale = 2)
	private BigDecimal monthlyEmi;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private LoanStatus status = LoanStatus.PENDING;
	
	@Column(length = 1000)
	private String purpose;
	
	@Column(nullable = false)
	@Builder.Default
	private LocalDateTime appliedAt = LocalDateTime.now();
	
	@Column(nullable = false)
	private LocalDateTime approvedAt;
	
	//Many-to-One: Many loans belongs to one user
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id",nullable = false)
	@ToString.Exclude
	private User user;
}

enum LoanStatus {
	PENDING, APPROVED, REJECTED, DISBURSED, CLOSED
}





























