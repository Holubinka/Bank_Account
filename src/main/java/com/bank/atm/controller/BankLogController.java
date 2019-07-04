package com.bank.atm.controller;

import com.bank.atm.model.BankLog;
import com.bank.atm.services.BankLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/log")
public class BankLogController {

    @Autowired
    private BankLogService bankLogService;

    @RequestMapping(value = "/card", method = RequestMethod.GET)
    public ResponseEntity<List<BankLog>> findByCard(Principal principal, @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
                                                    @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {
        return ResponseEntity.ok(bankLogService.findBankLogByCard(Long.parseLong(principal.getName()), fromDate, toDate));
    }
}
