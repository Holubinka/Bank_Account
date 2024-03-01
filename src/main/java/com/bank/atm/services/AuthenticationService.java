package com.bank.atm.services;

import com.bank.atm.dto.AuthenticationRequest;
import com.bank.atm.dto.AuthenticationResponse;
import com.bank.atm.dto.RegisterRequest;
import com.bank.atm.exception.UserAlreadyExistsException;
import com.bank.atm.model.Account;
import com.bank.atm.model.BankUser;
import com.bank.atm.model.Token;
import com.bank.atm.model.TokenType;
import com.bank.atm.repository.AccountRepository;
import com.bank.atm.repository.TokenRepository;
import com.bank.atm.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final AccountRepository accountRepository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationResponse register(RegisterRequest request) {
    var user = BankUser.builder()
      .cardNumber(request.getCardNumber())
      .password(passwordEncoder.encode(request.getPassword()))
      .build();
    repository.findBankUserByCardNumber(user.getCardNumber())
      .ifPresent(s -> { throw new UserAlreadyExistsException("User already exists!");});

    Account account = new Account();
    account.setBankUser(user);
    accountRepository.save(account);
    var savedUser = repository.save(user);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    saveUserToken(savedUser, jwtToken);
    return AuthenticationResponse.builder()
      .accessToken(jwtToken)
      .refreshToken(refreshToken)
      .build();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        request.getCardNumber(),
        request.getPassword()
      )
    );
    var user = repository.findBankUserByCardNumber(request.getCardNumber())
      .orElseThrow();
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return AuthenticationResponse.builder()
      .accessToken(jwtToken)
      .refreshToken(refreshToken)
      .build();
  }

  private void saveUserToken(BankUser user, String jwtToken) {
    var token = Token.builder()
      .user(user)
      .token(jwtToken)
      .tokenType(TokenType.BEARER)
      .expired(false)
      .revoked(false)
      .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(BankUser user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public void refreshToken(
    HttpServletRequest request,
    HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String cardNumber;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    cardNumber = jwtService.extractCardNumber(refreshToken);
    if (cardNumber != null) {
      var user = this.repository.findBankUserByCardNumber(Long.valueOf(cardNumber))
        .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthenticationResponse.builder()
          .accessToken(accessToken)
          .refreshToken(refreshToken)
          .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }
}