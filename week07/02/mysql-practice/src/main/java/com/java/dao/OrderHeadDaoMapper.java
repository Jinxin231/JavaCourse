package com.java.dao;

import com.java.dataobject.OrderHeadDao;
import com.java.dataobject.OrderHeadDaoKey;

public interface OrderHeadDaoMapper {
    int deleteByPrimaryKey(OrderHeadDaoKey key);

    int insert(OrderHeadDao record);

    int insertSelective(OrderHeadDao record);

    OrderHeadDao selectByPrimaryKey(OrderHeadDaoKey key);

    int updateByPrimaryKeySelective(OrderHeadDao record);

    int updateByPrimaryKey(OrderHeadDao record);
}