package com.example.demo.controller;

import com.example.demo.model.BankLog;
import com.example.demo.services.BankLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/log")
public class BankLogController {

    @Autowired
    private BankLogService bankLogService;

    @RequestMapping(value = "/card/{cardId}", method = RequestMethod.GET)
    public ResponseEntity<List<BankLog>> findByCard(@PathVariable Long cardId) {
        return ResponseEntity.ok(bankLogService.findBankLogByCard(cardId));
    }

    @RequestMapping(value = "/client/{name}", method = RequestMethod.GET)
    public ResponseEntity<List<BankLog>> findByClient(@PathVariable String name) {
        return ResponseEntity.ok(bankLogService.findBankLogByClient(name));
    }


}
