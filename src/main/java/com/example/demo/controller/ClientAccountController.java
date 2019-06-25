package com.example.demo.controller;

import com.example.demo.model.ClientAccount;
import com.example.demo.services.ClientAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bank")
public class ClientAccountController {

    @Autowired
    private ClientAccountService clientAccountService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<ClientAccount>> getAll() {
        List<ClientAccount> categories = clientAccountService.getAll();
        ResponseEntity<List<ClientAccount>> result;

        if (categories.isEmpty()) {
            result = ResponseEntity.notFound().build();
        } else {
            result = new ResponseEntity<>(categories, HttpStatus.OK);
        }
        return result;
    }

    @RequestMapping(value = "/add_money/{numberCard}", method = RequestMethod.PUT)
    public ResponseEntity<ClientAccount> add_money(@RequestBody ClientAccount clientAccount, @PathVariable Long numberCard) {
        return clientAccountService.update(numberCard, clientAccount.getAmount())
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.CONFLICT));
    }

    @RequestMapping(value = "/get_money/{numberCard}", method = RequestMethod.PUT)
    public ResponseEntity<ClientAccount> get_money(@RequestBody ClientAccount clientAccount, @PathVariable Long numberCard) {
        return clientAccountService.update(numberCard, clientAccount.getAmount().negate())
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.CONFLICT));
    }

    @RequestMapping(value = "/transfer/{numberCardSender}", method = RequestMethod.PUT)
    public ResponseEntity<List<ClientAccount>> transfer(@RequestBody ClientAccount recipientClientAccount, @PathVariable Long numberCardSender) {
        return clientAccountService.saveAll(numberCardSender, recipientClientAccount)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.CONFLICT));
    }
}
