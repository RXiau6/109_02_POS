package Project.demo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
            state.setString(6, genReceipt());
            state.execute();
            success = true;
        } catch (SQLException ex) {
            System.out.println("Order_insert異常:" + ex.toString());
        }
        return success;
    }
    
    public List<Order> getReceipt(String order_num){
               conn = DBConnection.getConnection();
        String query = "select * from sale_order where order_num = ?";
        List<Order> receipt_list = new ArrayList();

        try {
            PreparedStatement ps
                    = conn.prepareStatement(query);

            ps.setString(1, order_num);
            ResultSet rset = ps.executeQuery();
/*
                    private String order_num;
    private String order_date;
    private int total_price;
    private String customer_name;
    private String customer_address;
    private String customer_phtone;
    private String recipt_num;*/
            while (rset.next()) {
                Order order = new Order();
                order.setOrder_num(rset.getString("order_num"));
                order.setOrder_date(rset.getString("order_date"));
                order.setTotal_price(rset.getInt("total_price"));
                order.setCustomer_name(rset.getString("customer_name"));
                order.setCustomer_address(rset.getString("customer_address"));
                order.setCustomer_phtone(rset.getString("customer_phone"));
                order.setRecipt_num(rset.getString("recipt_num"));
                receipt_list.add(order);
                
                //不要斷線，一直會用到，使用持續連線的方式
               //conn.close();
            }
        } catch (SQLException ex) {
            System.out.println("getReceipt異常:" + ex.toString());
        }

        return receipt_list;
    }
    
    public List<OrderDetail> getAllDetails(String order_num){
       conn = DBConnection.getConnection();
        String query = "select * from order_detail where order_num = ?";
        List<OrderDetail> detail_list = new ArrayList();

        try {
            PreparedStatement ps
                    = conn.prepareStatement(query);

            ps.setString(1, order_num);
            ResultSet rset = ps.executeQuery();

            while (rset.next()) {
                OrderDetail detail = new OrderDetail();
                detail.setOrder_num(rset.getString("order_num"));
                detail.setProduct_name(rset.getString("product_name"));
                detail.setQuantity(rset.getInt("quantity"));
                detail.setProduct_price(rset.getInt("product_price"));
                detail_list.add(detail);
                
                //不要斷線，一直會用到，使用持續連線的方式
               //conn.close();
            }
        } catch (SQLException ex) {
            System.out.println("getAllproducts異常:" + ex.toString());
        }

        return detail_list;
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
            //state.setString(6, genReceipt);
            state.execute();
            success = true;
        } catch (SQLException ex) {
            System.out.println("Order_detail_insert異常:" + ex.toString());
        }
        return success;
    }
    public static String genReceipt(){
        return randChar()+randNum();
    }
    public static String randNum(){
        int n =0;
        
        Random r = new Random(System.currentTimeMillis());
        n = r.nextInt(99999999);
        String num = String.format("%08d",n);
        return num;
    }
    public static String randChar(){
        String track ="";
        int code =0;
        while(track.length()<2){
        Random r = new Random();
        code = r.nextInt(25)+65;
        track += (char)code;
        }
        return track;
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
