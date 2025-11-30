package com.pocketbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pocketbank.entity.Loan;
@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

}
