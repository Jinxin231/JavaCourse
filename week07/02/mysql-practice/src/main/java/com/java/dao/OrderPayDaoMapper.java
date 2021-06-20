package com.java.dao;

import com.java.dataobject.OrderPayDao;
import com.java.dataobject.OrderPayDaoKey;

public interface OrderPayDaoMapper {
    int deleteByPrimaryKey(OrderPayDaoKey key);

    int insert(OrderPayDao record);

    int insertSelective(OrderPayDao record);

    OrderPayDao selectByPrimaryKey(OrderPayDaoKey key);

    int updateByPrimaryKeySelective(OrderPayDao record);

    int updateByPrimaryKey(OrderPayDao record);
}