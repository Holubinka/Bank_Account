package com.example.demo.controller;

import com.example.demo.dao.ClientAccountRepository;
import com.example.demo.exception.CustomNotFoundException;
import com.example.demo.model.ClientAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/account")
public class ClientAccountController {

    private final static String SENDER = "sender";
    private final static String RECIPIENT = "recipient";
    private final static String AMOUNT = "amount";

    @Autowired
    private ClientAccountRepository clientAccountRepository;

    @PostMapping("/transfer")
    public Map<String, ClientAccount> transfer(@RequestBody Map<String, Long> payload) {

        Long senderId = payload.get(SENDER);
        Long recipientId = payload.get(RECIPIENT);
        BigDecimal amount = BigDecimal.valueOf(payload.get(AMOUNT));

        ClientAccount sender = clientAccountRepository.findById(senderId).orElseThrow(() -> new CustomNotFoundException("Not fount sender: " + senderId));
        ClientAccount recipient = clientAccountRepository.findById(recipientId).orElseThrow(() -> new CustomNotFoundException("Not fount recipient: " + recipientId));
        BigDecimal senderAmount = sender.getAmount();

        if (amount.compareTo(BigDecimal.valueOf(0)) == -1 || senderAmount.compareTo(amount) == -1) {
            throw new CustomNotFoundException("The sender:" + senderId + " does not have enough money for the transaction");
        }
        sender.setSum_amount(amount.negate());
        recipient.setSum_amount(amount);
        sender.setAmount(senderAmount.subtract(amount));
        recipient.setAmount(recipient.getAmount().add(amount));

        Map<String, ClientAccount> accountMap = new HashMap<>();
        accountMap.put(SENDER, sender);
        accountMap.put(RECIPIENT, recipient);

        clientAccountRepository.saveAll(accountMap.values());

        return accountMap;
    }
}