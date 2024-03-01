package com.bank.atm.services;

import com.bank.atm.dto.ChangePasswordRequest;
import com.bank.atm.exception.LowBalanceException;
import com.bank.atm.exception.UserNotFoundException;
import com.bank.atm.exception.UserTransactionsNotFoundException;
import com.bank.atm.model.Account;
import com.bank.atm.model.BankUser;
import com.bank.atm.model.Transaction;
import com.bank.atm.model.TransactionType;
import com.bank.atm.repository.AccountRepository;
import com.bank.atm.repository.TransactionRepository;
import com.bank.atm.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

  private final PasswordEncoder passwordEncoder;
  private final UserRepository repository;
  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;
  private static final String NOT_FOUND_MESSAGE = "User not found!";
  private static final String BENEFICIARY_NOT_FOUND_MESSAGE = "Beneficiary user not found!";
  public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

    var user = (BankUser) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

    // check if the current password is correct
    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
      throw new IllegalStateException("Wrong password");
    }
    // check if the two new passwords are the same
    if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
      throw new IllegalStateException("Password are not the same");
    }

    // update the password
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));

    // save the new password
    repository.save(user);
  }

  public Account getUserDetails(Long cardNumber) {
    return accountRepository.findAccountByBankUserCardNumber(cardNumber)
      .orElseThrow(() -> new UserNotFoundException(NOT_FOUND_MESSAGE));
  }

  @Transactional
  public Transaction createDepositTransaction(Long cardNumber, BigDecimal amount) {
    Account bankUserAccount = accountRepository.findAccountByBankUserCardNumber(cardNumber)
      .orElseThrow(() -> new UserNotFoundException(NOT_FOUND_MESSAGE));

    Transaction transaction = createTransaction(amount, TransactionType.DEPOSIT);
    bankUserAccount.getTransactions().add(transaction);
    bankUserAccount.setBalance(bankUserAccount.getBalance().add(amount));
    accountRepository.save(bankUserAccount);

    return transaction;
  }

  @Transactional
  public Transaction createWithdrawTransaction(Long cardNumber, BigDecimal amount) {
    Account bankUserAccount = accountRepository.findAccountByBankUserCardNumber(cardNumber)
      .orElseThrow(() -> new UserNotFoundException(NOT_FOUND_MESSAGE));

    if (amount.compareTo(bankUserAccount.getBalance()) > 0)
      throw new LowBalanceException("Insufficient funds!");

    Transaction transaction = createTransaction(amount, TransactionType.WITHDRAW);
    bankUserAccount.getTransactions().add(transaction);
    bankUserAccount.setBalance(bankUserAccount.getBalance().subtract(amount));
    accountRepository.save(bankUserAccount);

    return transaction;
  }

  public Transaction createTransferTransaction(Long senderCardNumber, Long beneficiaryCardNumber, BigDecimal amount) {
    Account senderAccount = accountRepository.findAccountByBankUserCardNumber(senderCardNumber)
      .orElseThrow(() -> new UserNotFoundException(NOT_FOUND_MESSAGE));

    Account beneficiaryAccount = accountRepository.findAccountByBankUserCardNumber(beneficiaryCardNumber)
      .orElseThrow(() -> new UserNotFoundException(BENEFICIARY_NOT_FOUND_MESSAGE));

    if (amount.compareTo(senderAccount.getBalance()) > 0)
      throw new LowBalanceException("Insufficient funds!");

    return realizeTransferTransaction(amount, senderAccount, beneficiaryAccount);
  }

  @Transactional
  public Transaction realizeTransferTransaction(BigDecimal amount, Account senderAccount, Account beneficiaryAccount) {
    Transaction sendTransaction = createTransaction(amount, TransactionType.TRANSFER);
    sendTransaction.setFromIdAccount(senderAccount.getId());
    sendTransaction.setToIdAccount(beneficiaryAccount.getId());
    senderAccount.getTransactions().add(sendTransaction);
    senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
    transactionRepository.save(sendTransaction);
    accountRepository.save(senderAccount);

    Transaction receiveTransaction = createTransaction(amount, TransactionType.TRANSFER);
    receiveTransaction.setFromIdAccount(senderAccount.getId());
    receiveTransaction.setToIdAccount(beneficiaryAccount.getId());
    beneficiaryAccount.getTransactions().add(receiveTransaction);
    beneficiaryAccount.setBalance(beneficiaryAccount.getBalance().add(amount));
    transactionRepository.save(receiveTransaction);
    accountRepository.save(beneficiaryAccount);

    return sendTransaction;
  }

  public List<Transaction> getBankStatement(Long cardNumber, LocalDateTime startDate, LocalDateTime endDate) {
    if (accountRepository.findAccountByBankUserCardNumber(cardNumber).isEmpty())
      throw new UserNotFoundException(NOT_FOUND_MESSAGE);

    List<Transaction> transactionList = transactionRepository.getUserTransactionsBetweenDates(1, startDate, endDate);
    if (transactionList.isEmpty())
      throw new UserTransactionsNotFoundException("No transactions found!");

    return transactionList;
  }

  public Transaction createTransaction(BigDecimal amount, TransactionType transactionType) {
    Transaction transaction = new Transaction();
    transaction.setValue(amount);
    transaction.setTransactionType(transactionType);
    transaction.setTimestamp(LocalDateTime.now());
    transactionRepository.save(transaction);
    return transaction;
  }
}