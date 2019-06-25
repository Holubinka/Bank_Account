package com.example.demo.services;

import com.example.demo.model.BankLog;
import java.util.List;

public interface BankLogService {
    List<BankLog> findBankLogByCard(Long cardId);

    List<BankLog> findBankLogByClient(String cardName);
}
