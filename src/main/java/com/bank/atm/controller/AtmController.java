package com.bank.atm.controller;

import com.bank.atm.dto.AccountDto;
import com.bank.atm.dto.BankUserDto;
import com.bank.atm.model.Account;
import com.bank.atm.model.BankUser;
import com.bank.atm.model.Transaction;
import com.bank.atm.services.AtmService;
import org.modelmapper.ModelMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/v1/bank")
public class AtmController {

  private final ModelMapper modelMapper;
  private final AtmService atmService;

  public AtmController(AtmService atmService) {
    this.modelMapper = new ModelMapper();
    this.atmService = atmService;
  }

  @GetMapping("/users")
  public ResponseEntity<List<BankUserDto>> getUsersList() {
    List<BankUser> bankUserList = atmService.getAllUsers();

    List<BankUserDto> bankUserDtoList = bankUserList.stream()
      .map(user -> modelMapper.map(user, BankUserDto.class))
      .toList();

    return new ResponseEntity<>(bankUserDtoList, HttpStatus.OK);
  }

  @GetMapping("/accounts")
  public ResponseEntity<List<AccountDto>> getUsersAccountList() {
    List<Account> accountList = atmService.getAllUsersAccount();

    List<AccountDto> accountDtoList = accountList.stream()
      .map(account -> modelMapper.map(account, AccountDto.class))
      .toList();

    return new ResponseEntity<>(accountDtoList, HttpStatus.OK);
  }

  @GetMapping("/transactions")
  public ResponseEntity<List<Transaction>> getTransactionList() {
    List<Transaction> accountList = atmService.getAllTransactions();

    return new ResponseEntity<>(accountList, HttpStatus.OK);
  }

  @GetMapping("/highest-balance")
  public ResponseEntity<AccountDto> getAccountWithHighestBalance() {
    Account account = atmService.getHighestAccountBalance();

    AccountDto accountDto = modelMapper.map(account, AccountDto.class);

    return new ResponseEntity<>(accountDto, HttpStatus.OK);
  }

  @GetMapping("/balance")
  public ResponseEntity<BigDecimal> getBankBalance() {
    BigDecimal balance = atmService.calculateBankBalance();

    return new ResponseEntity<>(balance,HttpStatus.OK);
  }

  @GetMapping("/user-most-transactions")
  public ResponseEntity<BankUserDto> getBankUserWithMostTransactions() {
    BankUser bankUser = atmService.findUserWithMostTransactions();

    BankUserDto bankUserDto = modelMapper.map(bankUser, BankUserDto.class);

    return new ResponseEntity<>(bankUserDto,HttpStatus.OK);
  }

  @GetMapping("/date-most-transactions")
  public ResponseEntity<Date> getDateUserWithMostTransactions() {
    Date date = atmService.findDateWithMostTransactions();

    return new ResponseEntity<>(date,HttpStatus.OK);
  }

  @GetMapping("/transactions-between-dates")
  public ResponseEntity<List<Transaction>> getTransactionsBetweenDates(
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
    List<Transaction> transactions = atmService.getTransactionsBetweenDate(start.atStartOfDay(),end.atStartOfDay());

    return new ResponseEntity<>(transactions,HttpStatus.OK);
  }
}