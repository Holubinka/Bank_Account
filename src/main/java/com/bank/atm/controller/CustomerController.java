package com.bank.atm.controller;

import com.bank.atm.model.Customer;
import com.bank.atm.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bank")
public class CustomerController {

    @Autowired
    private CustomerService CustomerService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<Customer>> getAll() {
        List<Customer> categories = CustomerService.getAll();
        ResponseEntity<List<Customer>> result;

        if (categories.isEmpty()) {
            result = ResponseEntity.notFound().build();
        } else {
            result = new ResponseEntity<>(categories, HttpStatus.OK);
        }
        return result;
    }

    @RequestMapping(value = "/add_money/{cardNumber}", method = RequestMethod.PUT)
    public ResponseEntity<Customer> add_money(@RequestBody Customer Customer, @PathVariable Long cardNumber) {
        return CustomerService.update(cardNumber, Customer.getAmount())
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.CONFLICT));
    }

    @RequestMapping(value = "/get_money/{cardNumber}", method = RequestMethod.PUT)
    public ResponseEntity<Customer> get_money(@RequestBody Customer Customer, @PathVariable Long cardNumber) {
        return CustomerService.update(cardNumber, Customer.getAmount())
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.CONFLICT));
    }

    @RequestMapping(value = "/transfer/{cardNumberSender}", method = RequestMethod.PUT)
    public ResponseEntity<List<Customer>> transfer(@RequestBody Customer recipientCustomer, @PathVariable Long cardNumberSender) {
        return CustomerService.saveAll(cardNumberSender, recipientCustomer)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.CONFLICT));
    }
}
