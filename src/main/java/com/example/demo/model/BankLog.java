package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "bank_log")
public class BankLog {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated
    private BankOperationType bankOperationType;

    @Column(name = "operation_description")
    private String operationDescription;

    @Column(name = "recipient")
    private Long recipient;

    @Column(name = "sender")
    private Long sender;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_and_time")
    @CreationTimestamp
    private Date dateTime;

    @Column(name = "sum_amount")
    private BigDecimal sum_amount;

    @JsonIgnoreProperties("bankLogs")
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "client_account_bank_log",
            joinColumns = @JoinColumn(name = "bank_log_id"),
            inverseJoinColumns = @JoinColumn(name = "client_account_id"))
    private List<ClientAccount> clientAccounts;

    public BankLog() {
    }

    public BankLog(BankOperationType bankOperationType, String operationDescription) {
        this.bankOperationType = bankOperationType;
        this.operationDescription = operationDescription;
    }

    public void addClientAccount(ClientAccount clientAccount) {
        if (clientAccounts == null) {
            clientAccounts = new ArrayList<>();
        }
        clientAccounts.add(clientAccount);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BankOperationType getBankOperationType() {
        return bankOperationType;
    }

    public void setBankOperationType(BankOperationType bankOperationType) {
        this.bankOperationType = bankOperationType;
    }

    public String getOperationDescription() {
        return operationDescription;
    }

    public void setOperationDescription(String operationDescription) {
        this.operationDescription = operationDescription;
    }

    public Long getRecipient() {
        return recipient;
    }

    public void setRecipient(Long recipient) {
        this.recipient = recipient;
    }

    public Long getSender() {
        return sender;
    }

    public void setSender(Long sender) {
        this.sender = sender;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public List<ClientAccount> getClientAccounts() {
        return clientAccounts;
    }

    public void setClientAccounts(List<ClientAccount> clientAccounts) {
        this.clientAccounts = clientAccounts;
    }

    public BigDecimal getSum_amount() {
        return sum_amount;
    }

    public void setSum_amount(BigDecimal sum_amount) {
        this.sum_amount = sum_amount;
    }
}