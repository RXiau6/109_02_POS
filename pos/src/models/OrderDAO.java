package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OrderDAO {

    private Connection conn;

    public String getMaxOrderNum() {
        conn = DBConnection.getConnection();
        String maxVal = null;

        String query = "SELECT Max(order_num) as `max_id` FROM `sale_order`";
        //String query = "SELECT Max(order_num) as `max_id` FROM `sale_order`";
        //String query = "SELECT Max(order_num) as `max_id` FROM `sale_order` LIMIT 1";
        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rset = state.executeQuery();
            while (rset.next()) {
                maxVal = rset.getString("max_id");
            }
        } catch (SQLException ex) {
            System.out.println("資料庫getMaxOrderNum操作異常:" + ex.toString());
        }
        return maxVal;
    }

    public int getRecipt_num() {
        conn = DBConnection.getConnection();
        String query = "select * from recipt";
        int num =0;
        try {
            PreparedStatement ps
                    = conn.prepareStatement(query);
            ResultSet rset = ps.executeQuery();

            while (rset.next()) {
                num = rset.getInt("number");
                
                //不要斷線，一直會用到，使用持續連線的方式
                //conn.close();
            }
            return num;
        } catch (SQLException ex) {
            System.out.println("getRecipt_num異常:" + ex.toString());
            return -1;
        }
        
    }

    public boolean setRecipt_num(int num) {
        conn = DBConnection.getConnection();
        String query = "update recipt set number=?";
        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setInt(1,num+1);
            state.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.out.println("updateRecipt異常:" + ex.toString());
            return false;
        }
    }

    public boolean insertCart(Order cart) {
        //String order_num =  getMaxOrderNum();
        conn = DBConnection.getConnection();
        String query = "insert into `sale_order`(order_num,total_price,"
                + "customer_name,customer_phone, customer_address,recipt_num) "
                + "VALUES (?, ?, ?, ?, ?,?)";
        boolean success = false;
        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, cart.getOrder_num());
            state.setInt(2, cart.getTotal_price());
            state.setString(3, cart.getCustomer_name());
            state.setString(4, cart.getCustomer_phtone());
            state.setString(5, cart.getCustomer_address());
            state.setString(6, cart.getRecipt_num());
            state.execute();
            success = true;
        } catch (SQLException ex) {
            System.out.println("Order_insert異常:" + ex.toString());
        }
        return success;
    }

    //新增訂單明細 應該寫在OrderDetailDAO.java比較好
    public boolean insertOrderDetailItem(OrderDetail item) {
        //String order_num =  getMaxOrderNum();
        conn = DBConnection.getConnection();

        String query = "INSERT INTO `order_detail` (`order_num`, `product_id`, `quantity`, product_price, product_name) VALUES (?, ?, ?, ?, ?)";
        boolean success = false;
        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, item.getOrder_num());
            state.setString(2, item.getProduct_id());
            state.setInt(3, item.getQuantity());
            state.setInt(4, item.getProduct_price()); //optional
            state.setString(5, item.getProduct_name());//optional
            //state.setString(6, item.getRecipt_num());
            state.execute();
            success = true;
        } catch (SQLException ex) {
            System.out.println("Order_detail_insert異常:" + ex.toString());
        }
        return success;
    }

    public static void main(String[] args) {

        OrderDAO orddao = new OrderDAO();
        System.out.println(orddao.getMaxOrderNum());

        String ordNum = "ord-501";

        Order cart = new Order();
        cart.setOrder_num(ordNum);
        orddao.insertCart(cart);

        OrderDetail item = new OrderDetail();
        item.setOrder_num(ordNum);
        item.setQuantity(2);
        item.setProduct_id("p-j-103");

        orddao.insertOrderDetailItem(item);
    }

}
