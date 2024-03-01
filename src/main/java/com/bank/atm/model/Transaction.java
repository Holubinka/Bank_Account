package com.bank.atm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Component
@Entity
@Table(name = "transaction")
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private LocalDateTime timestamp;
  @Column(name = "trValue")
  private BigDecimal value;
  @Enumerated(EnumType.STRING)
  private TransactionType transactionType;
  private Integer fromIdAccount;
  private Integer toIdAccount;
}