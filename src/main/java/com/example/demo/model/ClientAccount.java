package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "client_account")
public class ClientAccount {

    @Id
    @Column(name = "number_card")
    private Long numberCard;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "amount")
    private BigDecimal amount;

    @Transient
    @JsonIgnore
    private BigDecimal sumAmount;

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

    public Long getNumberCard() {
        return numberCard;
    }

    public void setNumberCard(Long numberCard) {
        this.numberCard = numberCard;
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
        ClientAccount that = (ClientAccount) o;
        return Objects.equals(numberCard, that.numberCard) &&
                Objects.equals(name, that.name) &&
                Objects.equals(password, that.password) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(sumAmount, that.sumAmount) &&
                Objects.equals(bankLogs, that.bankLogs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numberCard, name, password, amount, sumAmount, bankLogs);
    }

    @Override
    public String toString() {
        return "ClientAccount{" +
                "numberCard=" + numberCard +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", amount=" + amount +
                ", sumAmount=" + sumAmount +
                ", bankLogs=" + bankLogs +
                '}';
    }
}