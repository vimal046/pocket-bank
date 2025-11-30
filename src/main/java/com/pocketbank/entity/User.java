package com.pocketbank.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.pocketbank.entity.enums.Role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "Username is required")
	@Column(unique = true,nullable = false)
	private String username;
	
	@NotBlank(message = "Password is required")
	@Column(nullable = false)
	private String password;
	
	@NotBlank(message = "Full name is required")
	@Column(nullable = false)
	private String fullName;
	
	@Email(message = "Invalid email format")
	@NotBlank(message = "Email is required")
	@Column(unique = true,nullable = false)
	private String email;
	
	@NotBlank(message = "Phone number is required")
	@Column(nullable = false)
	private String phoneNumber;
	
	@NotBlank(message = "Address is required")
	@Column(nullable = false)
	private String address;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private Role role=Role.CUSTOMER;
	
	@Column(nullable = false)
	@Builder.Default
	private Boolean enabled=true;
	
	@Column(nullable = false)
	@Builder.Default
	private LocalDateTime createdAt = LocalDateTime.now();
	
	//One to many : One user has many account
	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@ToString.Exclude
	@Builder.Default
	private List<Account> accounts = new ArrayList<>();
	
	//One to many: One user has many loans
	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@Builder.Default
	private List<Loan> loans = new ArrayList<>();
	
	//One-to-many: One user has many fixed deposits
	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@Builder.Default
	private List<FixedDeposit> fixedDeposits = new ArrayList<>();
}

