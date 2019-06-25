package com.bank.atm.services;

import com.bank.atm.dao.BankLogDao;
import com.bank.atm.model.BankLog;
import com.bank.atm.model.BankOperationType;
import com.bank.atm.model.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankLogServiceImpl implements BankLogService {

    @Autowired
    BankLogDao bankLogDao;

    @Override
    public List<BankLog> findBankLogByCard(Long cardId) {
        List<BankLog> bankLogList = bankLogDao.findBankLogByCard(cardId);
        saveHistoryBankLogs("findBankLogByCard");
        return bankLogList;
    }

    @Override
    public List<BankLog> findBankLogByClient(String cardName) {
        List<BankLog> bankLogList = bankLogDao.findBankLogByClient(cardName);
        saveHistoryBankLogs(" findBankLogByClient");
        return bankLogList;
    }

    private void saveHistoryBankLogs(String nameMethod) {
        String method = nameMethod.toUpperCase();
        BankLog bankLog;
        if (method.contains(String.valueOf(Constants.CUSTOMER))) {
            bankLog = new BankLog(BankOperationType.VIEW_CUSTOMER_HISTORY, BankOperationType.VIEW_CUSTOMER_HISTORY.toString());
        } else {
            bankLog = new BankLog(BankOperationType.VIEW_CARD_HISTORY, BankOperationType.VIEW_CARD_HISTORY.toString());
        }
        bankLogDao.save(bankLog);
    }
}
