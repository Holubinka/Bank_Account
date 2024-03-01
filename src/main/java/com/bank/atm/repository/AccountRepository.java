package com.bank.atm.repository;

import com.bank.atm.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
  Optional<Account> findAccountByBankUserCardNumber(Long id);

  @Query(value = "SELECT * FROM account WHERE balance = (SELECT MAX(balance) FROM account)",nativeQuery = true)
  Account findUserWithHighestBalance();
}