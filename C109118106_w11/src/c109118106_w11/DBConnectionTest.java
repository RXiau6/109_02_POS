/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c109118106_w11;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author RXiau6
 */
public class DBConnectionTest {
    public static void main(String[] args) throws SQLException {

        //使用帳號與密碼連線到資料庫...
        Connection conn = DBConnection.getConnection();

        String query = "Select * From product";
        Statement state = conn.createStatement();
        ResultSet result = state.executeQuery(query);

        while (result.next()) {
            System.out.println(result.getString("id") + " " + result.getString("name"));
        }

        conn.close(); //關閉連線物件     

        // 多取得幾個連線物件試試看
        // 使用帳號與密碼連線到資料庫...
        // 因為連線物件被關閉，會再一次連線到資料庫
        Connection conn2 = DBConnection.getConnection();

        //取得已連線靜態物件connection
        Connection conn3 = DBConnection.getConnection();

        //取得已連線靜態物件connection
        Connection conn4 = DBConnection.getConnection();
    }
}
