package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "client_account")
public class ClientAccount {

    @Id
    @Column(name = "number_card")
    private Long number_card;

    @Column(name = "name")
    private String name;

    @Column(name = "amount")
    private BigDecimal amount;

    @Transient
    @JsonIgnore
    private BigDecimal sum_amount;

    @JsonIgnoreProperties("clientAccounts")
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "client_account_bank_log",
            joinColumns = @JoinColumn(name = "client_account_id"),
            inverseJoinColumns = @JoinColumn(name = "bank_log_id"))
    private List<BankLog> bankLogs;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public List<BankLog> getBankLogs() {
        return bankLogs;
    }

    public void setBankLogs(List<BankLog> bankLogs) {
        this.bankLogs = bankLogs;
    }

    public Long getNumber_card() {
        return number_card;
    }

    public void setNumber_card(Long number_card) {
        this.number_card = number_card;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getSum_amount() {
        return sum_amount;
    }

    public void setSum_amount(BigDecimal sum_amount) {
        this.sum_amount = sum_amount;
    }
}