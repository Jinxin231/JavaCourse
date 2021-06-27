package com.example.atomikospra.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class LogDao {

    @Autowired
    @Qualifier("first")
    private JdbcTemplate jdbcTemplate;

    public void insert(Log log) {
        jdbcTemplate.update("insert t_log(id,log) values(?,?)", log.getId(), log.getLog());
    }
}

