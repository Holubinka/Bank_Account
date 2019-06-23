package com.example.demo.controller;

import com.example.demo.dao.BankLogRepository;
import com.example.demo.dao.ClientAccountRepository;
import com.example.demo.exception.CustomNotFoundException;
import com.example.demo.model.BankLog;
import com.example.demo.model.ClientAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@RequestMapping("/log")
public class BankLogController {

    @Autowired
    private BankLogRepository bankLogRepository;

    @Autowired
    private ClientAccountRepository clientAccountRepository;

    @GetMapping("/card/{cardId}")
    public List<BankLog> findByCard(@PathVariable Long cardId) {

        return bankLogRepository.findBankLogByCard(cardId);
    }

    @GetMapping("/client/{name}")
    public List<BankLog> findByClient(@PathVariable String name) {

        ClientAccount byName = clientAccountRepository.findByName(name);

        if (byName == null) {
            throw new CustomNotFoundException("Client not found: " + name);
        }

        return bankLogRepository.findBankLogByClient(byName.getNumber_card());
    }


}
