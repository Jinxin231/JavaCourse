package com.java.dao;

import com.java.dataobject.OrderDetailDao;
import com.java.dataobject.OrderDetailDaoKey;

public interface OrderDetailDaoMapper {
    int deleteByPrimaryKey(OrderDetailDaoKey key);

    int insert(OrderDetailDao record);

    int insertSelective(OrderDetailDao record);

    OrderDetailDao selectByPrimaryKey(OrderDetailDaoKey key);

    int updateByPrimaryKeySelective(OrderDetailDao record);

    int updateByPrimaryKey(OrderDetailDao record);
}