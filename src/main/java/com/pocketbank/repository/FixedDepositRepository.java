package com.pocketbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pocketbank.entity.FixedDeposit;
@Repository
public interface FixedDepositRepository extends JpaRepository<FixedDeposit, Long> {

}
