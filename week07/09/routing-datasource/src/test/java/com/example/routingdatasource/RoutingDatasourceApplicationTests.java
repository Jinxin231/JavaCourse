package com.example.routingdatasource;

import com.example.routingdatasource.service.implement.UserService;
import com.java.dataobject.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RoutingDatasourceApplicationTests {

    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {
    }


    @Test
    public void testWrite() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(2);
        userInfo.setName("jinx");
        userInfo.setDept("sn");
        userInfo.setPhone("13770572518");
        userInfo.setWebsite("");
        userService.insert(userInfo);
    }

    @Test
    public void testRead() {
        UserInfo userInfo = userService.selectAll(2);
        if (userInfo == null){
            System.out.println("备库无数据！");
        }else {
            System.out.println(userInfo.toString());
        }
    }

    @Test
    public void testMasterRead() {
        UserInfo userInfo = userService.selectMaster(2);
        if (userInfo == null){
            System.out.println("备库无数据！");
        }else {
            System.out.println(userInfo.toString());
        }
    }
}
