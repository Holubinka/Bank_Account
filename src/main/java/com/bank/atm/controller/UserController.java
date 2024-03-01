package com.bank.atm.controller;

import com.bank.atm.dto.AccountDto;
import com.bank.atm.dto.BasicTransactionDto;
import com.bank.atm.dto.ChangePasswordRequest;
import com.bank.atm.dto.TransactionDto;
import com.bank.atm.model.Account;
import com.bank.atm.model.Transaction;
import com.bank.atm.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final ModelMapper modelMapper = new ModelMapper();

  private final UserService service;

  @PatchMapping
  public ResponseEntity<?> changePassword(
    @RequestBody ChangePasswordRequest request,
    Principal connectedUser
  ) {
    service.changePassword(request, connectedUser);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/")
  public ResponseEntity<AccountDto> getUserDetails(Principal connectedUser) {
    Account account = service.getUserDetails(Long.parseLong(connectedUser.getName()));

    AccountDto accountDto = modelMapper.map(account, AccountDto.class);

    return new ResponseEntity<>(accountDto, HttpStatus.OK);
  }

  @PatchMapping("/deposit")
  public ResponseEntity<BasicTransactionDto> createUserDeposit(@RequestParam String amount, Principal connectedUser) {
    Transaction transaction = service.createDepositTransaction(Long.parseLong(connectedUser.getName()),new BigDecimal(amount));

    BasicTransactionDto basicTransactionDto = modelMapper.map(transaction, BasicTransactionDto.class);

    return new ResponseEntity<>(basicTransactionDto, HttpStatus.CREATED);
  }

  @PatchMapping("/withdraw")
  public ResponseEntity<BasicTransactionDto> createUserWithdraw(
    @RequestParam String amount,
    Principal connectedUser) {
    Transaction transaction = service.createWithdrawTransaction(Long.parseLong(connectedUser.getName()),new BigDecimal(amount));

    BasicTransactionDto basicTransactionDto = modelMapper.map(transaction, BasicTransactionDto.class);

    return new ResponseEntity<>(basicTransactionDto, HttpStatus.CREATED);
  }

  @PatchMapping("/transfer")
  public ResponseEntity<TransactionDto> createUserTransfer(
    @RequestParam String idAccount2,
    @RequestParam String amount, Principal connectedUser) {
    Transaction transaction = service.createTransferTransaction(Long.parseLong(connectedUser.getName()),
      Long.parseLong(idAccount2), new BigDecimal(amount));

    TransactionDto transactionDto = modelMapper.map(transaction, TransactionDto.class);

    return new ResponseEntity<>(transactionDto, HttpStatus.CREATED);
  }

  @GetMapping("/statement")
  public ResponseEntity<List<TransactionDto>> getBankStatementFromAPeriod(
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
    Principal connectedUser
    ) {
    List<Transaction> transactions = service.getBankStatement(Long.parseLong(connectedUser.getName()), start.atStartOfDay(),end.atStartOfDay());

    List<TransactionDto> transactionDtoList = transactions.stream().
      map(tr -> modelMapper.map(tr, TransactionDto.class))
      .toList();

    return new ResponseEntity<>(transactionDtoList,HttpStatus.OK);
  }
}