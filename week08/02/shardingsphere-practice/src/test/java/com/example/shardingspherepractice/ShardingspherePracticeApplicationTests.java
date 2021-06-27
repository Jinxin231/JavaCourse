package com.example.shardingspherepractice;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
class ShardingspherePracticeApplicationTests {

    @Autowired
    private DataSource dataSource;

    @Test
    void contextLoads() {
    }

    @Test
    void initOrder(){
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String  dateS =  formatter.format(date);
        String sql = "insert into `t_order_head` (`order_id`,`store_code`,`member_id`,`member_name`,`sale_time`,`transaction_value`,`create_date`,`create_by`,`last_modified_by`,`enable_flag`)  " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";

        Connection con = null;
        try {
            con = dataSource.getConnection();
            PreparedStatement pst = (PreparedStatement) con.prepareStatement(sql);
            con.setAutoCommit(false);

            for (int i = 0; i < 10000; i++){
                pst.setInt(1,i+1);
                pst.setString(2, "837A");
                pst.setString(3, "00000001");
                pst.setString(4, "jinx");
                pst.setString(5, dateS);
                pst.setBigDecimal(6, BigDecimal.valueOf(88));
                pst.setString(7, dateS);
                pst.setString(8, "jinx");
                pst.setString(9, "jinx");
                pst.setInt(10, 1);
                pst.addBatch();
            }

            pst.executeBatch();
            con.commit();
            pst.close();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Test
    void getOrder(){

        Connection con = null;
        try {
            con = dataSource.getConnection();
            Statement st =  con.createStatement();
            String sql = "select * from t_order_head";
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                System.out.println("order_id= " + rs.getString("order_id") + ",member_name=" + rs.getString("member_name"));
            }



        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
