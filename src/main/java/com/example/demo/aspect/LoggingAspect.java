package com.example.demo.aspect;

import com.example.demo.dao.BankLogRepository;
import com.example.demo.dao.ClientAccountRepository;
import com.example.demo.model.BankLog;
import com.example.demo.model.BankOperationType;
import com.example.demo.model.ClientAccount;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Aspect
@Configuration
public class LoggingAspect {

    private static final String CLIENT = "client";
    private static final String SENDER = "sender";
    private static final String RECIPIENT = "recipient";

    @Autowired
    private BankLogRepository bankLogRepository;

    @Autowired
    private ClientAccountRepository clientAccountRepository;

    @AfterReturning(value = "execution(* com.example.demo.controller.BankAccountController.*(..))",
            returning = "result")
    public void afterReturningBankAccountController(JoinPoint joinPoint, Object result) {

        BankLog bankLog;
        String method = joinPoint.getSignature().getName().toUpperCase();
        BankOperationType bankOperationType = BankOperationType.valueOf(method);

        if (bankOperationType == BankOperationType.GET_MONEY) {
            ClientAccount clientAccount = clientAccountRepository.findById(((ClientAccount) result).getNumber_card()).orElseThrow(IllegalArgumentException::new);
            bankLog = new BankLog(BankOperationType.GET_MONEY, BankOperationType.GET_MONEY.toString());
            bankLog.setSender(clientAccount.getNumber_card());
            bankLog.setRecipient(((ClientAccount) result).getNumber_card());
            bankLog.setSum_amount(clientAccount.getSum_amount().negate());
            bankLog.addClientAccount(clientAccount);
        } else {
            ClientAccount clientAccount = clientAccountRepository.findById(((ClientAccount) result).getNumber_card()).orElseThrow(IllegalArgumentException::new);
            bankLog = new BankLog(BankOperationType.ADD_MONEY, BankOperationType.ADD_MONEY.toString());
            bankLog.setSender(clientAccount.getNumber_card());
            bankLog.setRecipient(((ClientAccount) result).getNumber_card());
            bankLog.setSum_amount(clientAccount.getSum_amount());
            bankLog.addClientAccount(clientAccount);
        }

        bankLogRepository.save(bankLog);
    }

    @AfterReturning(value = "execution(* com.example.demo.controller.BankLogController.*(..))",
            returning = "result")
    public void afterReturningBankLogController(JoinPoint joinPoint, List<BankLog> result) {

        String method = joinPoint.getSignature().getName().toLowerCase();
        BankLog bankLog;
        if (method.contains(CLIENT)) {
            bankLog = new BankLog(BankOperationType.HISTORY_CLIENT, BankOperationType.HISTORY_CLIENT.toString());
        } else {
            bankLog = new BankLog(BankOperationType.HISTORY_CARD, BankOperationType.HISTORY_CARD.toString());
        }

        bankLogRepository.save(bankLog);
    }

    @AfterReturning(value = "execution(* com.example.demo.controller.ClientAccountController.*(..))",
            returning = "result")
    public void afterReturningClientAccountController(JoinPoint joinPoint, Map<String, ClientAccount> result) {

        BankLog bankLog = new BankLog(BankOperationType.TRANSFER, BankOperationType.TRANSFER.toString());
        ClientAccount sender = result.get(SENDER);
        ClientAccount recipient = result.get(RECIPIENT);
        bankLog.setSender(sender.getNumber_card());
        bankLog.setRecipient(recipient.getNumber_card());
        bankLog.setSum_amount(recipient.getSum_amount());
        bankLog.addClientAccount(sender);
        bankLog.addClientAccount(recipient);

        bankLogRepository.save(bankLog);
    }
}