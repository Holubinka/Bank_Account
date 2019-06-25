package com.bank.atm.services;

import com.bank.atm.dao.BankLogDao;
import com.bank.atm.dao.CustomerDao;
import com.bank.atm.exception.CustomNotFoundException;
import com.bank.atm.exception.MoneyException;
import com.bank.atm.model.BankLog;
import com.bank.atm.model.BankOperationType;
import com.bank.atm.model.Customer;
import com.bank.atm.model.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service("CustomerServiceImpl")
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerDao CustomerDao;

    @Autowired
    BankLogDao bankLogDao;

    @Override
    public List<Customer> getAll() {
        return CustomerDao.findAll();
    }

    @Override
    public Optional<Customer> update(Long cardNumber, BigDecimal amount) {
        Customer Customer = CustomerDao.findById(cardNumber).orElseThrow(IllegalArgumentException::new);

        if(amount.compareTo(BigDecimal.valueOf(0))==-1) {
            notEnoughMoney(amount, Customer);
        }

        verifyValidAmount(amount);

        Customer.setSumAmount(amount);
        Customer.setAmount(Customer.getAmount().add(amount));
        saveMoneyHistoryBankLog(Customer);

        return Optional.of(CustomerDao.save(Customer));
    }

    @Override
    public Optional<List<Customer>>saveAll(Long cardNumberSender, Customer recipientCustomer) {
        BigDecimal amount = recipientCustomer.getAmount();
        Customer sender = CustomerDao.findById(cardNumberSender).orElseThrow(() -> new CustomNotFoundException("Not fount sender: " + cardNumberSender));
        Customer recipient = CustomerDao.findById(recipientCustomer.getcardNumber()).orElseThrow(() -> new CustomNotFoundException("Not fount recipient: " + recipientCustomer.getcardNumber()));

        notEnoughMoney(amount,sender);
        sender.setSumAmount(amount);
        recipient.setSumAmount(amount);
        sender.setAmount(sender.getAmount().subtract(amount));
        recipient.setAmount(recipient.getAmount().add(amount));

        Map<String, Customer> accountMap = new HashMap<>();
        accountMap.put(String.valueOf(Constants.SENDER), sender);
        accountMap.put(String.valueOf(Constants.RECIPIENT), recipient);
        saveTransferHistoryBankLog(accountMap);
        return Optional.of(CustomerDao.saveAll(accountMap.values()));
    }

    private void notEnoughMoney(BigDecimal amount, Customer Customer) {
        if (amount.compareTo(BigDecimal.valueOf(0)) == -1 || amount.compareTo(Customer.getAmount()) == 1) {
            throw new CustomNotFoundException("Not enough money for a transaction");
        }
    }

    private void verifyValidAmount(BigDecimal amount){
        if (amount.remainder(BigDecimal.valueOf(100)).compareTo(BigDecimal.valueOf(0)) != 0 && amount.remainder(BigDecimal.valueOf(200)).compareTo(BigDecimal.valueOf(0)) != 0 && amount.remainder(BigDecimal.valueOf(500)).compareTo(BigDecimal.valueOf(0)) != 0) {
            throw new MoneyException("The bank works with money multiples of 100, 200, 500");
        }
    }

    private void saveMoneyHistoryBankLog(Customer result) {
        BankLog bankLog;
        if (result.getSumAmount().compareTo(BigDecimal.valueOf(0))==-1) {
            Customer Customer = CustomerDao.findById(result.getcardNumber()).orElseThrow(IllegalArgumentException::new);
            bankLog = new BankLog(BankOperationType.GET_MONEY, BankOperationType.GET_MONEY.toString());
            bankLog.setSender(Customer.getcardNumber());
            bankLog.setRecipient(result.getcardNumber());
            bankLog.setSumAmount(Customer.getSumAmount());
            bankLog.addCustomer(Customer);
        } else {
            Customer Customer = CustomerDao.findById(result.getcardNumber()).orElseThrow(IllegalArgumentException::new);
            bankLog = new BankLog(BankOperationType.ADD_MONEY, BankOperationType.ADD_MONEY.toString());
            bankLog.setSender(Customer.getcardNumber());
            bankLog.setRecipient(result.getcardNumber());
            bankLog.setSumAmount(Customer.getSumAmount());
            bankLog.addCustomer(Customer);
        }
        bankLogDao.save(bankLog);
    }

    private void saveTransferHistoryBankLog(Map<String, Customer> result) {
        BankLog bankLog = new BankLog(BankOperationType.TRANSFER, BankOperationType.TRANSFER.toString());
        Customer sender = result.get(String.valueOf(Constants.SENDER));
        Customer recipient = result.get(String.valueOf(Constants.RECIPIENT));
        bankLog.setSender(sender.getcardNumber());
        bankLog.setRecipient(recipient.getcardNumber());
        bankLog.setSumAmount(recipient.getSumAmount());
        bankLog.addCustomer(sender);
        bankLog.addCustomer(recipient);

        bankLogDao.save(bankLog);
    }

    private UserDetails toUserDetails(Customer user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(String.valueOf(user.getcardNumber()))
                .password(user.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return CustomerDao.findById(Long.parseLong(username))
                .map(this::toUserDetails)
                .orElseGet(org.springframework.security.core.userdetails.User.builder().disabled(true)::build);
    }
}
