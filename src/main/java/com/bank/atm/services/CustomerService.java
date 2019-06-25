package com.bank.atm.services;

import com.bank.atm.model.Customer;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CustomerService extends UserDetailsService {
    List<Customer> getAll();

    Optional<Customer> update(Long cardNumber, BigDecimal amount);

    Optional<List<Customer>> saveAll(Long cardNumberSender, Customer recipientCustomer);
}
