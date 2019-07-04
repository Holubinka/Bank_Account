package com.bank.atm.services;

import com.bank.atm.model.BankLog;

import java.util.Date;
import java.util.List;

public interface BankLogService {
    List<BankLog> findBankLogByCard(Long cardId, Date fromDate, Date toDate);
}
