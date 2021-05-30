/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author RXiau6
 */
public class ReciptDAO {

    private Connection conn;

    public List<Recipt> getAllRecipts(String Account) {

        conn = DBConnection.getConnection();
        String query = "select * from gov_recipt where account = ?";
        List<Recipt> acc_recipt = new ArrayList();

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, Account);
            ResultSet rset = state.executeQuery();

            while (rset.next()) {
                Recipt recipt = new Recipt();
                recipt.setAccount(rset.getString("account"));
                recipt.setTransaction_date(rset.getString("transaction_date"));
                recipt.setTransaction_amount(rset.getInt("transaction_amount"));
                recipt.setRecipt_num(rset.getString("recipt_num"));
                acc_recipt.add(recipt);

                //不要斷線，一直會用到，使用持續連線的方式
                //conn.close();
            }
        } catch (SQLException ex) {
            System.out.println("getAllproducts異常:" + ex.toString());
        }

        return acc_recipt;
    }

    public boolean insert(Recipt recipt) {
        conn = DBConnection.getConnection();
        String query = "insert into gov_recipt(account,"
                + "transaction_amount,recipt_num,currency) VALUES (?,?,?,?)";
        boolean success = false;
        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, recipt.getAccount());
            state.setDouble(2, recipt.getTransaction_amount());
            state.setString(3, recipt.getRecipt_num());
            state.setString(4, recipt.getCurrency());

            state.execute();
            //state.executeUpdate();
            success = true;
        } catch (SQLException ex) {
            System.out.println("Recipt_insert異常:" + ex.toString());
        }
        return success;
    }

    public boolean delete(String recipt_num) {
        conn = DBConnection.getConnection();
        String query = "delete from gov_recipt where recipt_num= ?";
        boolean sucess = false;
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, recipt_num);
            //statement.execute();
            sucess = statement.executeUpdate() > 0;
            if (sucess) {
                System.out.println("Record deleted successfully.");
            } else {
                System.out.println("Record not found.");
            }
        } catch (SQLException ex) {
            System.out.println("delete異常:\n" + ex.toString());
        }
        return sucess;
    }

    public static void main(String[] args) {
        ReciptDAO dao = new ReciptDAO();
        Recipt recipt = new Recipt("/1**OJHQ","NF80566859",100.0,"TWD");
        List<Recipt> acc_recipt= dao.getAllRecipts("/1S**JHQ");
        System.out.println(dao.insert(recipt));
        //System.out.println(dao.delete("NF80566859"));
        for (Recipt my_recipt : acc_recipt){
            System.out.println(my_recipt);
        }
    }
}
