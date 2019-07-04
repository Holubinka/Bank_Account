package com.bank.atm.controller;

import com.bank.atm.model.Customer;
import com.bank.atm.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/bank")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<Customer>> getAll() {
        List<Customer> categories = customerService.getAll();
        ResponseEntity<List<Customer>> result;

        if (categories.isEmpty()) {
            result = ResponseEntity.notFound().build();
        } else {
            result = new ResponseEntity<>(categories, HttpStatus.OK);
        }
        return result;
    }

    @RequestMapping(value = "/add_money", method = RequestMethod.PUT)
    public ResponseEntity<Customer> add_money(@RequestBody Customer Customer, Principal principal) {
        return customerService.update(Long.parseLong(principal.getName()), Customer.getAmount())
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.CONFLICT));
    }

    @RequestMapping(value = "/get_money", method = RequestMethod.PUT)
    public ResponseEntity<Customer> get_money(@RequestBody Customer Customer, Principal principal) {
        return customerService.update(Long.parseLong(principal.getName()), Customer.getAmount().negate())
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.CONFLICT));
    }

    @RequestMapping(value = "/transfer", method = RequestMethod.PUT)
    public ResponseEntity<List<Customer>> transfer(@RequestBody Customer recipientCustomer, Principal principal) {
        return customerService.saveAll(Long.parseLong(principal.getName()), recipientCustomer)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.CONFLICT));
    }
}
