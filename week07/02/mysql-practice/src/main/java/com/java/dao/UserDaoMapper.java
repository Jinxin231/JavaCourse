package com.java.dao;

import com.java.dataobject.UserDao;

public interface UserDaoMapper {
    int deleteByPrimaryKey(String memberId);

    int insert(UserDao record);

    int insertSelective(UserDao record);

    UserDao selectByPrimaryKey(String memberId);

    int updateByPrimaryKeySelective(UserDao record);

    int updateByPrimaryKey(UserDao record);
}