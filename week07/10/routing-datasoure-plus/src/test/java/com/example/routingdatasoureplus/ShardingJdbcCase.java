package com.example.routingdatasoureplus;


import org.apache.shardingsphere.api.hint.HintManager;
import org.apache.shardingsphere.sql.parser.binder.metadata.util.JdbcUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ShardingJdbcCase {

    private static Logger logger = LoggerFactory.getLogger(ShardingJdbcCase.class);

    @Resource
    private DataSource dataSource;


    /**
     * 读写分离，主库写，从库读
     * 同一线程且同一数据库连接内，如有写入操作，以后的读操作均从主库读取，用于保证数据一致性
     */
    @Test
    public void readWrite() throws SQLException {
        Connection con = null;
        try {
            con = dataSource.getConnection();
            Statement st = con.createStatement();

            //从slave0读数据
            ResultSet rs = st.executeQuery("select * from user");
            while (rs.next()) {
                System.out.println(rs.getString("id") + "|" + rs.getString("name"));
            }

            //写入master
            st.executeUpdate("insert into user(name,dept,phone) values ('jinx','anb','13770572518')");

            //从master读数据
            rs = st.executeQuery("select * from user");

            while (rs.next()) {
                System.out.println(rs.getString(1) + "|" + rs.getString(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null){
                con.close();
            }
        }
    }

    /**
     * 读写分离，强制主库路由
     */
    @Test
    public void hintMasterRouteOnly() throws SQLException {
        Connection con = null;
        try {
            con = dataSource.getConnection();
            Statement st = con.createStatement();
            HintManager hintManager = HintManager.getInstance();
            //主库路由
            hintManager.setMasterRouteOnly();

            //从master读数据
            ResultSet rs = st.executeQuery("select * from user");
            while (rs.next()) {
                logger.info(rs.getString(1) + "|" + rs.getString(2));
            }
            hintManager.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null){
                con.close();
            }
        }
    }
}
