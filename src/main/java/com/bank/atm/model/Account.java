package com.bank.atm.model;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Entity
@EqualsAndHashCode
@ToString
@Component
@Table(name = "account")
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @OneToOne(cascade = CascadeType.ALL)
  private BankUser bankUser;
  private BigDecimal balance;
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "account_id", referencedColumnName = "id")
  private List<Transaction> transactions;

  public Account() {
    this.balance = BigDecimal.ZERO;
  }
}