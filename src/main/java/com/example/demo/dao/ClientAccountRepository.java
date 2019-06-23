package com.example.demo.dao;

import com.example.demo.model.ClientAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientAccountRepository extends JpaRepository<ClientAccount, Long> {
    ClientAccount findByName(String name);
}