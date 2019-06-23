package com.example.demo.controller;

import com.example.demo.dao.ClientAccountRepository;
import com.example.demo.exception.CustomNotFoundException;
import com.example.demo.exception.MoneyException;
import com.example.demo.model.ClientAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/bank")
public class BankAccountController {

    private final static String ACCOUNT = "account";
    private final static String AMOUNT = "amount";

    @Autowired
    private ClientAccountRepository clientAccountRepository;

    @PostMapping("/add_money")
    public ClientAccount add_money(@RequestBody Map<String, Long> payload) {
        BigDecimal amount = BigDecimal.valueOf(payload.get(AMOUNT));

        Long accountId = payload.get(ACCOUNT);
        ClientAccount clientAccount = clientAccountRepository.findById(accountId).orElseThrow(IllegalArgumentException::new);

        someMethod(amount);

        clientAccount.setSum_amount(amount);
        clientAccount.setAmount(clientAccount.getAmount().add(amount));
        clientAccountRepository.save(clientAccount);

        return clientAccount;
    }

    @PostMapping("/get_money")
    public ClientAccount get_money(@RequestBody Map<String, Long> payload) {
        BigDecimal amount = BigDecimal.valueOf(payload.get(AMOUNT));

        Long accountId = payload.get(ACCOUNT);
        ClientAccount clientAccount = clientAccountRepository.findById(accountId).orElseThrow(IllegalArgumentException::new);

        if (amount.compareTo(BigDecimal.valueOf(0)) == -1 || amount.compareTo(clientAccount.getAmount()) == 1) {
            throw new CustomNotFoundException("Not enough money for a transaction");
        }
        someMethod(amount);
        clientAccount.setSum_amount(amount);
        clientAccount.setAmount(clientAccount.getAmount().subtract(amount));

        clientAccountRepository.save(clientAccount);

        return clientAccount;
    }

    private void someMethod(BigDecimal amount) {
        if (amount.remainder(BigDecimal.valueOf(100)).compareTo(BigDecimal.valueOf(0)) != 0 && amount.remainder(BigDecimal.valueOf(200)).compareTo(BigDecimal.valueOf(0)) != 0 && amount.remainder(BigDecimal.valueOf(500)).compareTo(BigDecimal.valueOf(0)) != 0) {
            throw new MoneyException("The bank works with money multiples of 100, 200, 500");
        }
    }
}
