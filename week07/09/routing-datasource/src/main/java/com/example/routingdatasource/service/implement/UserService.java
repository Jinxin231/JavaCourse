package com.example.routingdatasource.service.implement;

import com.example.routingdatasource.annotation.Master;
import com.example.routingdatasource.service.IUserService;
import com.java.dao.UserInfoMapper;
import com.java.dataobject.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService implements IUserService {


    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public int insert(UserInfo userInfo) {
        return  userInfoMapper.insert(userInfo);
    }


    @Override
    public UserInfo selectAll(int key) {
        return userInfoMapper.selectByPrimaryKey(key);
    }

    @Master
    @Override
    public UserInfo selectMaster(int key) {
        return userInfoMapper.selectByPrimaryKey(key);
    }
}
