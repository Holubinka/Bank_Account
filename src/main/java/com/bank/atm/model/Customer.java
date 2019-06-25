package com.bank.atm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "client_account")
public class Customer {

    @Id
    @Column(name = "number_card")
    private Long cardNumber;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "amount")
    private BigDecimal amount;

    @Transient
    @JsonIgnore
    private BigDecimal sumAmount;

    @JsonIgnoreProperties("customers")
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

    public Long getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(Long cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getSumAmount() {
        return sumAmount;
    }

    public void setSumAmount(BigDecimal sumAmount) {
        this.sumAmount = sumAmount;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer that = (Customer) o;
        return Objects.equals(cardNumber, that.cardNumber) &&
                Objects.equals(name, that.name) &&
                Objects.equals(password, that.password) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(sumAmount, that.sumAmount) &&
                Objects.equals(bankLogs, that.bankLogs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber, name, password, amount, sumAmount, bankLogs);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "cardNumber=" + cardNumber +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", amount=" + amount +
                ", sumAmount=" + sumAmount +
                ", bankLogs=" + bankLogs +
                '}';
    }
}