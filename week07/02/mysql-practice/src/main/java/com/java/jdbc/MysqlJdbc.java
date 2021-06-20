package com.java.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class MysqlJdbc {

    @Autowired
    static DataSource dataSource;// 自动注入链接池数据源对象

    @Autowired
    static JdbcTemplate jdbcTemplate;

    private static boolean insertBySql() {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());

        // 简单的sql执行
        String sql = "insert into `t_order_head` (`order_id`, `store_code`,`member_id`,`member_name`,`sale_time`,`transaction_value`,`create_date`,`create_by`,`last_modified_by`,`enable_flag`) " +
                "VALUES (1,'837A','00000001','jinx','" + formatter.format(date) + "',88,'" + formatter.format(date) + "','jinx','jinx',1);";
        return jdbcTemplate.update(sql) > 0;
    }

    public static void main(String[] args) {

    }


    private void batchInsertBySql() {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "insert into `t_order_head` (`order_id`, `store_code`,`member_id`,`member_name`,`sale_time`,`transaction_value`,`create_date`,`create_by`,`last_modified_by`,`enable_flag`)  VALUES ";

        int orderId = 1;
        String sqlDetail = "";

        for (int i = 0; i < 10000;i++){
            orderId++;
            Date date = new Date(System.currentTimeMillis());
            sqlDetail = sqlDetail + "(" + orderId + ",'837A','00000001','jinx','" + formatter.format(date) + "',88,'" + formatter.format(date) + "','jinx','jinx',1),";
        }

        sql = sql + sqlDetail.substring(0,sqlDetail.length() - 1).concat(";");

        int[] ans = jdbcTemplate.batchUpdate(sql);
    }


//    private void batchInsertByStatement() {
//        String sql = "INSERT INTO `money` (`name`, `money`, `is_deleted`) VALUES (?, ?, ?);";
//
//        int[] ans = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
//            @Override
//            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
//                if (i == 0) {
//                    preparedStatement.setString(1, "batch 一灰灰5");
//                } else {
//                    preparedStatement.setString(1, "batch 一灰灰6");
//                }
//                preparedStatement.setInt(2, 300);
//                byte b = 0;
//                preparedStatement.setByte(3, b);
//            }
//
//            @Override
//            public int getBatchSize() {
//                return 2;
//            }
//        });
//        System.out.println("batch insert by statement: " + JSON.toJSONString(ans));
//    }




    public boolean insert(String table, String column, List<Object> values,Connection connection) {
        try {
            String insertTemplate = buildInsertSqlTemplate(table, column, values.size());
            PreparedStatement preparedStatement = connection.prepareStatement(insertTemplate);
            for (int i = 1; i< values.size() + 1; i++) {
                preparedStatement.setObject(i, values.get(i-1));
            }
            System.out.println(preparedStatement.toString());

            preparedStatement.execute();
            System.out.println("Insert successfully");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String buildInsertSqlTemplate(String table, String column, int valueAmount) {
//        return "insert into " + table + " " + column + " values (?" +
//                ",?".repeat(Math.max(0, valueAmount - 1)) +
//                ")";
        return "";
    }



}
