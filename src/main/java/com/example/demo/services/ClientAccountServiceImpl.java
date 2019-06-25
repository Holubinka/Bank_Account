package com.example.demo.services;

import com.example.demo.dao.BankLogDao;
import com.example.demo.dao.ClientAccountDao;
import com.example.demo.exception.CustomNotFoundException;
import com.example.demo.exception.MoneyException;
import com.example.demo.model.BankLog;
import com.example.demo.model.BankOperationType;
import com.example.demo.model.ClientAccount;
import com.example.demo.model.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service("clientAccountServiceImpl")
public class ClientAccountServiceImpl implements ClientAccountService {

    @Autowired
    ClientAccountDao clientAccountDao;

    @Autowired
    BankLogDao bankLogDao;

    @Override
    public List<ClientAccount> getAll() {
        return clientAccountDao.findAll();
    }

    @Override
    public Optional<ClientAccount> update(Long numberCard, BigDecimal amount) {
        ClientAccount clientAccount = clientAccountDao.findById(numberCard).orElseThrow(IllegalArgumentException::new);

        if(amount.compareTo(BigDecimal.valueOf(0))==-1) {
            notEnoughMoney(amount.negate(), clientAccount);
        }

        moneyMultiples(amount);

        clientAccount.setSumAmount(amount);
        clientAccount.setAmount(clientAccount.getAmount().add(amount));
        saveMoneyHistoryBankLog(clientAccount);

        return Optional.of(clientAccountDao.save(clientAccount));
    }

    @Override
    public Optional<List<ClientAccount>>saveAll(Long numberCardSender, ClientAccount recipientClientAccount) {
        BigDecimal amount = recipientClientAccount.getAmount();
        ClientAccount sender = clientAccountDao.findById(numberCardSender).orElseThrow(() -> new CustomNotFoundException("Not fount sender: " + numberCardSender));
        ClientAccount recipient = clientAccountDao.findById(recipientClientAccount.getNumberCard()).orElseThrow(() -> new CustomNotFoundException("Not fount recipient: " + recipientClientAccount.getNumberCard()));

        notEnoughMoney(amount,sender);
        sender.setSumAmount(amount);
        recipient.setSumAmount(amount);
        sender.setAmount(sender.getAmount().subtract(amount));
        recipient.setAmount(recipient.getAmount().add(amount));

        Map<String, ClientAccount> accountMap = new HashMap<>();
        accountMap.put(String.valueOf(Constants.SENDER), sender);
        accountMap.put(String.valueOf(Constants.RECIPIENT), recipient);
        saveTransferHistoryBankLog(accountMap);
        return Optional.of(clientAccountDao.saveAll(accountMap.values()));
    }

    private void notEnoughMoney(BigDecimal amount, ClientAccount clientAccount) {
        if (amount.compareTo(BigDecimal.valueOf(0)) == -1 || amount.compareTo(clientAccount.getAmount()) == 1) {
            throw new CustomNotFoundException("Not enough money for a transaction");
        }
    }

    private void moneyMultiples(BigDecimal amount){
        if (amount.remainder(BigDecimal.valueOf(100)).compareTo(BigDecimal.valueOf(0)) != 0 && amount.remainder(BigDecimal.valueOf(200)).compareTo(BigDecimal.valueOf(0)) != 0 && amount.remainder(BigDecimal.valueOf(500)).compareTo(BigDecimal.valueOf(0)) != 0) {
            throw new MoneyException("The bank works with money multiples of 100, 200, 500");
        }
    }

    private void saveMoneyHistoryBankLog(ClientAccount result) {
        BankLog bankLog;
        if (result.getSumAmount().compareTo(BigDecimal.valueOf(0))==-1) {
            ClientAccount clientAccount = clientAccountDao.findById(result.getNumberCard()).orElseThrow(IllegalArgumentException::new);
            bankLog = new BankLog(BankOperationType.GET_MONEY, BankOperationType.GET_MONEY.toString());
            bankLog.setSender(clientAccount.getNumberCard());
            bankLog.setRecipient(result.getNumberCard());
            bankLog.setSumAmount(clientAccount.getSumAmount());
            bankLog.addClientAccount(clientAccount);
        } else {
            ClientAccount clientAccount = clientAccountDao.findById(result.getNumberCard()).orElseThrow(IllegalArgumentException::new);
            bankLog = new BankLog(BankOperationType.ADD_MONEY, BankOperationType.ADD_MONEY.toString());
            bankLog.setSender(clientAccount.getNumberCard());
            bankLog.setRecipient(result.getNumberCard());
            bankLog.setSumAmount(clientAccount.getSumAmount());
            bankLog.addClientAccount(clientAccount);
        }
        bankLogDao.save(bankLog);
    }

    private void saveTransferHistoryBankLog(Map<String, ClientAccount> result) {
        BankLog bankLog = new BankLog(BankOperationType.TRANSFER, BankOperationType.TRANSFER.toString());
        ClientAccount sender = result.get(String.valueOf(Constants.SENDER));
        ClientAccount recipient = result.get(String.valueOf(Constants.RECIPIENT));
        bankLog.setSender(sender.getNumberCard());
        bankLog.setRecipient(recipient.getNumberCard());
        bankLog.setSumAmount(recipient.getSumAmount());
        bankLog.addClientAccount(sender);
        bankLog.addClientAccount(recipient);

        bankLogDao.save(bankLog);
    }

    private UserDetails toUserDetails(ClientAccount user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(String.valueOf(user.getNumberCard()))
                .password(user.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return clientAccountDao.findById(Long.parseLong(username))
                .map(this::toUserDetails)
                .orElseGet(org.springframework.security.core.userdetails.User.builder().disabled(true)::build);
    }
}
