package com.example.atomikospra.service;


import com.example.atomikospra.dao.Log;
import com.example.atomikospra.dao.LogDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogService {
    @Autowired
    private LogDao logDao;

    @Transactional
    // @Transactional(propagation = Propagation.REQUIRES_NEW)
    // @Transactional(propagation = Propagation.NESTED)
    public void insertLog(Log log) throws Exception {
        this.logDao.insert(log);
//      if(true) {
//          throw new RuntimeException("asdfasdffsadf");
//      }
    }
}
