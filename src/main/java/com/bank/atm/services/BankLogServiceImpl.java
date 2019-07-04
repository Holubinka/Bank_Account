package com.bank.atm.services;

import com.bank.atm.dao.BankLogDao;
import com.bank.atm.model.BankLog;
import com.bank.atm.model.BankOperationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BankLogServiceImpl implements BankLogService {

    @Autowired
    BankLogDao bankLogDao;

    @Override
    public List<BankLog> findBankLogByCard(Long cardId, Date fromDate, Date toDate) {
        List<BankLog> bankLogList = bankLogDao.findBankLogByCard(cardId, fromDate, toDate);
        BankLog bankLog = new BankLog(BankOperationType.VIEW_CUSTOMER_HISTORY, BankOperationType.VIEW_CUSTOMER_HISTORY.toString());
        bankLogDao.save(bankLog);
        return bankLogList;
    }

}
