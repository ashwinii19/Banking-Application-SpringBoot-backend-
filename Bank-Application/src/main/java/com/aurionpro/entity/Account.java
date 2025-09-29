package com.aurionpro.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long accountId;

	 @Column(unique = true, nullable = false, length = 12)
	    private String accountNumber;

	@NotBlank
	@Pattern(regexp = "^(SAVINGS|CURRENT|SALARY|FD)$", message = "Invalid account type")
	private String accountType;

	@NotNull
	@DecimalMin(value = "500.00", message = "Minimum balance must be ₹500.00")
	private Double balance;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;
	
	@OneToMany(mappedBy = "account",cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
	private List<Transaction> transactions;

	@NotBlank
	@Size(max=1)
	private String isAccountDeleted = "N";

}
