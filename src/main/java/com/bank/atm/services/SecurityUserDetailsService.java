package com.bank.atm.services;

import com.bank.atm.model.BankUser;
import com.bank.atm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SecurityUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username)
    throws UsernameNotFoundException {

    return userRepository.findBankUserByCardNumber(Long.parseLong(username))
      .orElseThrow(() -> new UsernameNotFoundException("User not present"));
  }
}