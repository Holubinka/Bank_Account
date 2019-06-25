package com.example.demo.services;

import com.example.demo.model.ClientAccount;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ClientAccountService extends UserDetailsService {
    List<ClientAccount> getAll();

    Optional<ClientAccount> update(Long numberCard, BigDecimal amount);

    Optional<List<ClientAccount>> saveAll(Long numberCardSender, ClientAccount recipientClientAccount);
}
