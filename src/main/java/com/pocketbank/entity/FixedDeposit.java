package com.pocketbank.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.pocketbank.entity.enums.FdStatus;

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
@Table(name = "fixed_deposits")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FixedDeposit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal principalAmount;

	@Column(nullable = false)
	private Integer tenureMonths;

	@Column(nullable = false)
	private Double interestRate;

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal maturityAmount;

	@Column(nullable = false)
	@Builder.Default
	private LocalDateTime startDate = LocalDateTime.now();

	@Column(nullable = false)
	private LocalDateTime maturityDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private FdStatus status = FdStatus.ACTIVE;

	// Many-to-One: Many FDs belongs to one User
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@ToString.Exclude
	private User user;
}
