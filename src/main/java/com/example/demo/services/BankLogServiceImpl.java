package com.example.demo.services;

import com.example.demo.dao.BankLogDao;
import com.example.demo.model.BankLog;
import com.example.demo.model.BankOperationType;
import com.example.demo.model.Constants;
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
        if (method.contains(String.valueOf(Constants.CLIENT))) {
            bankLog = new BankLog(BankOperationType.HISTORY_CLIENT, BankOperationType.HISTORY_CLIENT.toString());
        } else {
            bankLog = new BankLog(BankOperationType.HISTORY_CARD, BankOperationType.HISTORY_CARD.toString());
        }
        bankLogDao.save(bankLog);
    }


}
