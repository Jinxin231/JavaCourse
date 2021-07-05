package com.example.dubbodemoconsumer.service;

import entity.Account;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hmily.annotation.HmilyTCC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.AccountService;

/**
 * @author 13041285
 * @Description TODO
 * @date 2021/7/5-19:57
 */

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    final private AccountService accountService;

    /**
     * 这个注入很关键，这样注入就能进入RPC的切面，没有就报错
     * @param accountService
     */
    @Autowired(required = false)
    public TransactionServiceImpl(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    @HmilyTCC(confirmMethod = "confirmOrderStatus", cancelMethod = "cancelOrderStatus")
    public void transaction() {
        transactionA();
        transactionB();
    }

    private void transactionA() {
        log.info("============py one dubbo try 执行确认付款接口===============");
        Account account = new Account();
        account.setId(1L);
        account.setUs_account(-1L);
        account.setRmb_account(7L);
        accountService.pay(account);
    }

    private void transactionB() {
        log.info("============py two dubbo try 执行确认付款接口===============");
        Account account = new Account();
        account.setId(2L);
        account.setUs_account(1L);
        account.setRmb_account(-7L);
        accountService.pay(account);
    }

    public void confirmOrderStatus() {
        log.info("=========进行订单confirm操作完成================");
    }

    public void cancelOrderStatus() {
        log.info("=========进行订单cancel操作完成================");
    }
}
