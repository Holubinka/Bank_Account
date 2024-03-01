package com.bank.atm.repository;

import com.bank.atm.model.BankUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<BankUser, Integer> {
  Optional<BankUser> findBankUserByCardNumber(Long cardNumber);
  Optional<BankUser> findBankUserById(Integer id);
}