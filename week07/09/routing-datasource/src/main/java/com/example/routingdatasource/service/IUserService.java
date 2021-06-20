package com.example.routingdatasource.service;

import com.java.dataobject.UserInfo;

import java.util.List;

public interface IUserService {

    int insert(UserInfo userInfo);

    UserInfo selectAll(int key);

    UserInfo selectMaster(int key);
}
