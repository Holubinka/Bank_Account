package com.example.demo.dao;

import com.example.demo.model.BankLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BankLogDao extends JpaRepository<BankLog, Long> {

    @Query(value = "select * " +
            "from bank_log as bl " +
            "inner join client_account_bank_log as ca_bl " +
            "on bl.id=ca_bl.bank_log_id " +
            "where ca_bl.client_account_id=(:cardId)",
            nativeQuery = true)
    List<BankLog> findBankLogByCard(@Param("cardId") Long cardId);

    @Query(value = "select * " +
            "from bank_log as bl " +
            "where bl.id in " +
            "(select ca_bl.bank_log_id " +
            "from client_account_bank_log as ca_bl " +
            "where ca_bl.client_account_id in " +
            "(select ca.number_card " +
            "from client_account as ca " +
            "where ca.name=(:cardName)))",
            nativeQuery = true)
    List<BankLog> findBankLogByClient(@Param("cardName") String cardName);


}