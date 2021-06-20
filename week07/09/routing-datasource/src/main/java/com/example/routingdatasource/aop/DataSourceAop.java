package com.example.routingdatasource.aop;

import com.example.routingdatasource.routing.DBContextHolder;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DataSourceAop {

    @Pointcut("!@annotation(com.example.routingdatasource.annotation.Master) " +
            "&& (execution(* com.example.routingdatasource.service..*.select*(..)) " +
            "|| execution(* com.example.routingdatasource.service..*.get*(..)))")
    public void readPointcut() {

    }

    @Pointcut("@annotation(com.example.routingdatasource.annotation.Master) " +
            "|| execution(* com.example.routingdatasource.service..*.insert*(..)) " +
            "|| execution(* com.example.routingdatasource.service..*.add*(..)) " +
            "|| execution(* com.example.routingdatasource.service..*.update*(..)) " +
            "|| execution(* com.example.routingdatasource.service..*.edit*(..)) " +
            "|| execution(* com.example.routingdatasource.service..*.delete*(..)) " +
            "|| execution(* com.example.routingdatasource.service..*.remove*(..))")
    public void writePointcut() {

    }

    @Before("readPointcut()")
    public void read() {
        DBContextHolder.slave();
    }

    @Before("writePointcut()")
    public void write() {
        DBContextHolder.master();
    }
}
