package com.example.demo.dao;

import com.example.demo.model.ClientAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientAccountDao extends JpaRepository<ClientAccount, Long> {
}