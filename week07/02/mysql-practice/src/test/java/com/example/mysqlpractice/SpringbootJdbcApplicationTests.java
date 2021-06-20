package com.example.mysqlpractice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
public class SpringbootJdbcApplicationTests {

    @Autowired
    DataSource dataSource;// 自动注入链接池数据源对象

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void contextLoads() throws Exception {

        Connection connection = dataSource.getConnection();
        System.out.println("数据源："+dataSource);
        System.out.println("链接对象："+connection);
    }


    @Test
    void insertTest(){
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());

        // 简单的sql执行
        String sql = "insert into `t_order_head` (`order_id`, `store_code`,`member_id`,`member_name`,`sale_time`,`transaction_value`,`create_date`,`create_by`,`last_modified_by`,`enable_flag`) " +
                "VALUES ('0000000001','837A','00000001','jinx','" + formatter.format(date) + "',88,'" + formatter.format(date) + "','jinx','jinx',1);";
         jdbcTemplate.update(sql);
    }

    @Test
    void batchInsertBySql(){
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "insert into `t_order_head` (`order_id`, `store_code`,`member_id`,`member_name`,`sale_time`,`transaction_value`,`create_date`,`create_by`,`last_modified_by`,`enable_flag`)  VALUES ";

        int orderId = 1;
        String sqlDetail = "";

        for (int i = 0; i < 50000;i++){
            orderId++;
            Date date = new Date(System.currentTimeMillis());
            sqlDetail = sqlDetail + "(" + orderId + ",'837A','00000001','jinx','" + formatter.format(date) + "',88,'" + formatter.format(date) + "','jinx','jinx',1),";
        }

        sql = sql + sqlDetail.substring(0,sqlDetail.length() - 1).concat(";");

        int[] ans = jdbcTemplate.batchUpdate(sql);
    }

    @Test
    void batchInsertByStatement(){


        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String  dateS =  formatter.format(date);
        String sql = "insert into `t_order_head` (`order_id`, `store_code`,`member_id`,`member_name`,`sale_time`,`transaction_value`,`create_date`,`create_by`,`last_modified_by`,`enable_flag`)  " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";

        List<Integer> range = IntStream.rangeClosed(1, 1000000)
                .boxed()
                .collect(Collectors.toList());

        long start = System.currentTimeMillis();

        int[][] ans = jdbcTemplate.batchUpdate(sql, range, 1000000, new ParameterizedPreparedStatementSetter<Integer>() {
            @Override
            public void setValues(PreparedStatement preparedStatement, Integer i) throws SQLException {
                preparedStatement.setInt(1,i);
                preparedStatement.setString(2, "837A");
                preparedStatement.setString(3, "00000001");
                preparedStatement.setString(4, "jinx");
                preparedStatement.setString(5, dateS);
                preparedStatement.setBigDecimal(6, BigDecimal.valueOf(88));
                preparedStatement.setString(7, dateS);
                preparedStatement.setString(8, "jinx");
                preparedStatement.setString(9, "jinx");
                preparedStatement.setInt(10, 1);
            }
        });

        long end = System.currentTimeMillis();
        System.out.println("花费的时间为：" + (end - start));//20000条：625                                                                         //1000000条:14733

    }
}
