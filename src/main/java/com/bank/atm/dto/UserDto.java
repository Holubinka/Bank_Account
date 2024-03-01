package com.bank.atm.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDto {

  private Integer id;
  private Long cardNumber;
  private String password;
}