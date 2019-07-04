package com.bank.atm.dao;

import com.bank.atm.model.BankLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface BankLogDao extends JpaRepository<BankLog, Long> {

    @Query(value = "select * " +
            "from bank_log as bl " +
            "inner join client_account_bank_log as ca_bl " +
            "on bl.id=ca_bl.bank_log_id " +
            "where ca_bl.client_account_id=(:cardId) and bl.date_and_time between (:fromDate) and (:toDate)",
            nativeQuery = true)
    List<BankLog> findBankLogByCard(@Param("cardId") Long cardId, @Param("fromDate") Date fromDate, @Param("toDate") Date toDate);
}