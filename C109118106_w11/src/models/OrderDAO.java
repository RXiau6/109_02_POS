package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    private Connection conn;

    public List<Order> getAllOrder() {

        conn = DBConnection.getConnection();
        String query = "select * from sale_order";
        List<Order> order_list = new ArrayList();

        try {
            PreparedStatement ps
                    = conn.prepareStatement(query);
            ResultSet rset = ps.executeQuery();

            while (rset.next()) {
                Order order = new Order();
                order.setOrder_num(rset.getString("order_num"));
                order.setOrder_date(rset.getString("order_date"));
                order.setTotal_price(rset.getInt("total_price"));
                order.setCustomer_name(rset.getString("customer_name"));
                order.setCustomer_address(rset.getString("customer_address"));
                order.setCustomer_phone(rset.getString("customer_phone"));
                order.setRecipt_num(rset.getString("recipt_num"));
                order_list.add(order);
                //不要斷線，一直會用到，使用持續連線的方式
                //conn.close();
            }
        } catch (SQLException ex) {
            System.out.println("getAllorders異常:" + ex.toString());
        }

        return order_list;
    }

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
        int num = 0;
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
            state.setInt(1, num + 1);
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
            state.setString(4, cart.getCustomer_phone());
            state.setString(5, cart.getCustomer_address());
            state.setString(6, cart.getRecipt_num());
            state.execute();
            success = true;
        } catch (SQLException ex) {
            System.out.println("insert異常:" + ex.toString());
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
            System.out.println("insert異常:" + ex.toString());
        }
        return success;
    }

    public List<Order> selectByPhone(String id) {
        conn = DBConnection.getConnection();
        boolean success = false;
        String query = "select * from sale_order where customer_phone = ?";
        //String query = String.format("select * from product where product_id = '%s'", id);

        List order_list = new ArrayList();
        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, id);
            ResultSet rset = state.executeQuery();
            while (rset.next()) {
                success = true;
                Order order = new Order();
                order.setOrder_num(rset.getString("order_num"));
                order.setOrder_date(rset.getString("order_date"));
                order.setCustomer_name(rset.getString("customer_name"));
                order.setCustomer_address(rset.getString("customer_address"));
                order.setCustomer_phone(rset.getString("customer_phone"));
                order.setTotal_price(rset.getInt("total_price"));
                order_list.add(order);
            }
        } catch (SQLException ex) {
            System.out.println("資料庫selectByPhone操作異常:" + ex.toString());
        }

        if (success) {
            return order_list;
        } else {
            return null;
        }

    }

    public List<Order> selectByUser(String name_str) {
        conn = DBConnection.getConnection();
        boolean success = false;
        List<Order> order_list = new ArrayList();
        //String query = String.format("select * from product where name like '%s%s'", name_str, "%");
        //String query = String.format("select * from product where name like '%s'", name_str);
        String query = "select * from sale_order where customer_name like ?";
        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, name_str);

            ResultSet rset = state.executeQuery();
            while (rset.next()) {
                Order order = new Order();
                order.setOrder_num(rset.getString("order_num"));
                order.setOrder_date(rset.getString("order_date"));
                order.setCustomer_name(rset.getString("customer_name"));
                order.setCustomer_address(rset.getString("customer_address"));
                order.setCustomer_phone(rset.getString("customer_phone"));
                order.setTotal_price(rset.getInt("total_price"));
                order_list.add(order);
            }
        } catch (SQLException ex) {
            System.out.println("資料庫selectByUser操作異常:" + ex.toString());
        }
        return order_list;
    }

    public boolean delete(String id, String recipt_num) {
        conn = DBConnection.getConnection();
        String query = "delete from sale_order where order_num =?";
        String query_detail = "delete from order_detail where order_num =?";
        String query_recipt = "delete from gov_recipt where recipt_num =?";
        boolean sucess = false;
        try {

            //order_detail
            PreparedStatement statement_detail = conn.prepareStatement(query_detail);
            statement_detail.setString(1, id);
            sucess = statement_detail.executeUpdate() > 0;
            if (sucess) {
                System.out.println("detail_Record deleted successfully.");
            } else {
                System.out.println("detail_Record not found.");
            }
            //order
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, id);
            //statement.execute();
            sucess = statement.executeUpdate() > 0;
            if (sucess) {
                System.out.println("Record deleted successfully.");
            } else {
                System.out.println("Record not found.");
            }
            //gov_recipt
            PreparedStatement statement_recipt = conn.prepareStatement(query_recipt);
            statement_recipt.setString(1, recipt_num);
            sucess = statement_recipt.executeUpdate() > 0;
            if (sucess) {
                System.out.println("recipt_Record deleted successfully.");
            } else {
                System.out.println("recipt_Record not found.");
            }
            
        } catch (SQLException ex) {
            System.out.println("delete異常:\n" + ex.toString());
        }
        return sucess;
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
