/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c109118106_w11;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import models.Order;
import models.OrderDAO;
import models.OrderDetail;
import models.DBConnection;
import models.ProductDAO;
import models.Product;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.util.converter.IntegerStringConverter;

/**
 *
 * @author RXiau6
 */
public class FXMLDocumentController implements Initializable {

    private List<Product> products = new ArrayList();
    private List<Order> orders = new ArrayList();
    //products也可以宣告為ObservableList<Product>，會更方便
    //private ObservableList<Product> products = FXCollections.observableArrayList();
    //若products是ObservableList<Product>要這樣寫才可行:
    //products.addAll( prodao.getAllProducts()); 
    //方便操作資料庫的物件
    private ProductDAO prodao = new ProductDAO();
    private OrderDAO orddao = new OrderDAO();

    @FXML
    private TableView<Product> table_product;
    @FXML
    private TableColumn<Product, String> col_id;
    @FXML
    private TableColumn<Product, String> col_name;
    @FXML
    private Pagination pagination;

    //表格的每一頁顯示幾個rows
    private final int RowsPerPage = 10;

    @FXML
    private TextField queryID;
    @FXML
    private TextField queryName;
    @FXML
    private TableColumn<Product, Integer> col_price;
    @FXML
    private TableColumn<Product, String> col_photo;
    @FXML
    private TableColumn<Product, String> col_description;
    @FXML
    private TableColumn<Product, String> col_category;
    @FXML
    private TableView<Order> table_order;
    @FXML
    private TableColumn<Order, String> col_order_num;
    @FXML
    private TableColumn<Order, String> col_order_date;
    @FXML
    private TableColumn<Order, Integer> col_total_price;
    @FXML
    private TableColumn<Order, String> col_customer_name;
    @FXML
    private TableColumn<Order, String> col_customer_address;
    @FXML
    private TableColumn<Order, String> col_customer_phone;
    @FXML
    private TableColumn<Order, String> col_recipt_num;
    @FXML
    private Pagination pagination1;
    @FXML
    private TextField queryPhone;
    @FXML
    private TextField queryUser;
    @FXML
    private TextArea log_pane_product;
    @FXML
    private TextArea log_pane_ord;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initTable(); //表格初始化
        //TextArea log_pane = new TextArea();
        log_pane_init(log_pane_product);
        log_pane_init(log_pane_ord);
    }

    public void log_pane_init(TextArea ta) {
        ta.setDisable(false);
        ta.appendText("initialize done");
    }

    public void tp_display(TextArea ta, String msg) {
        ta.setText(msg);
    }

    @FXML
    private void update(ActionEvent event) {
        Product prod = table_product.getSelectionModel().getSelectedItem();
        String id = prod.getProduct_id();
        String name = prod.getName();
        int price = prod.getPrice();
        String category = prod.getCategory();
        String photo = prod.getPhoto();
        String description = prod.getDescription();
        Product tmp = new Product(id, category, name, price, photo, description);
        prodao.update(tmp);
        products = prodao.getAllProducts();
        loadTable();
        tp_display(log_pane_product, "變更成功");
    }

    @FXML
    private void delete(ActionEvent event) {

        Product prod = table_product.getSelectionModel().getSelectedItem();
        String id = prod.getProduct_id();

        boolean sucess = prodao.delete(id);
        products = prodao.getAllProducts();
        loadTable();
        tp_display(log_pane_product, "刪除成功");
    }

    @FXML
    private void insert(ActionEvent event) {
        Product prod = table_product.getSelectionModel().getSelectedItem();
        String id = prod.getProduct_id();
        String name = prod.getName();
        int price = prod.getPrice();
        String category = prod.getCategory();
        String photo = prod.getPhoto();
        String description = prod.getDescription();
        prodao.insert(new Product(id, category, name, price, photo, description));
        products = prodao.getAllProducts();
        loadTable();
        tp_display(log_pane_product, "新增成功");
    }

    @FXML
    private void blankRecord(ActionEvent event) {
        try {
            Product tmp = new Product("000", "測試類別", "測試", 1, "test.png", "測試介面");
            table_product.getItems().add(tmp);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        tp_display(log_pane_product, "新增一個空白紀錄");
    }

    @FXML
    private void findID(ActionEvent event) {
        products.clear();
        products.add(prodao.findById(queryID.getText()));
        //products = prodao.selectByID(queryID.getText());
        loadTable();
        tp_display(log_pane_product, "查找ID完畢");
    }

    @FXML
    private void findName(ActionEvent event) {
        products = prodao.findByNameContaining(queryName.getText());
        loadTable();
        tp_display(log_pane_product, "查找名稱完畢");
    }

    @FXML
    private void findAll(ActionEvent event) {
        prodao.getAllProducts();

        products = prodao.getAllProducts();

        //若students是ObservableList<Student>要這樣寫才可行:
        //students.addAll( stdao.getAllStudents()); 
        loadTable();
        tp_display(log_pane_product, "資料庫讀取完畢，已顯示最新產品列表");
    }

    private void initTable() {

        //表格最後一欄是空白，不要顯示!
        table_product.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //table_product.setPrefHeight(200);
        col_id.setCellValueFactory(new PropertyValueFactory<>("product_id"));
        col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        col_category.setCellValueFactory(new PropertyValueFactory<>("category"));
        col_photo.setCellValueFactory(new PropertyValueFactory<>("photo"));
        col_description.setCellValueFactory(new PropertyValueFactory<>("description"));
        col_order_num.setCellValueFactory(new PropertyValueFactory<>("order_num"));
        col_order_date.setCellValueFactory(new PropertyValueFactory<>("order_date"));
        col_total_price.setCellValueFactory(new PropertyValueFactory<>("total_price"));
        col_customer_name.setCellValueFactory(new PropertyValueFactory<>("customer_name"));
        col_customer_address.setCellValueFactory(new PropertyValueFactory<>("customer_address"));
        col_customer_phone.setCellValueFactory(new PropertyValueFactory<>("customer_phone"));
        col_recipt_num.setCellValueFactory(new PropertyValueFactory<>("recipt_num"));
        //按下頁次會驅動的事件，寫法格式有點難理解，說明如後:
        //ObservableValue<? extends Number> 是介面，
        // ? extends Number 表示某種型態繼承Number類別  ?表示此型態沒被用到所以用?代替
        // changed 有三個參數: ObservableValue、舊的頁次、新的頁次
        // ObservableValue是頁次物件的一些屬性 印出如下的結果:
        //IntegerProperty [bean: Pagination[id=pagination, styleClass=pagination], name: currentPageIndex, value: 1]
        pagination.currentPageIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                showTablePage(newValue.intValue(), RowsPerPage);
                //System.out.println(observable);
            }
        });

        // 表格切換到一下筆，對應的驅動方法，此處暫時沒用到，寫法與前面類似
        table_product.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                System.out.println(newValue);
            }
        });

        //讓表格內容可以修改
        table_product.setEditable(true);
        //表格欄位設定成可以編輯必須分別塞入一個TextFieldTableCell類別元件
        col_category.setCellFactory(TextFieldTableCell.forTableColumn());
        col_id.setCellFactory(TextFieldTableCell.forTableColumn());
        col_name.setCellFactory(TextFieldTableCell.forTableColumn());
        col_photo.setCellFactory(TextFieldTableCell.forTableColumn());
        col_description.setCellFactory(TextFieldTableCell.forTableColumn());
        col_price.setCellFactory(TextFieldTableCell.<Product, Integer>forTableColumn(new IntegerStringConverter()));
        //ageColumn.setCellFactory(TextFieldTableCell.<DataModel, Integer>forTableColumn(new IntegerStringConverter()));

        //col_price.setCellFactory(TextFieldTableCell.forTableColumn());
        //學生學號欄位若有修改驅動這個方法
        col_category.setOnEditCommit(new EventHandler<CellEditEvent<Product, String>>() {
            @Override
            public void handle(CellEditEvent<Product, String> event) {
                //拿到表格中所在的該筆紀錄(是一筆學生物件)
                Product prod = table_product.getSelectionModel().getSelectedItem();
                prod.setCategory(event.getNewValue()); //將該筆學生物件修改成新的值
            }
        });
        col_id.setOnEditCommit(new EventHandler<CellEditEvent<Product, String>>() {
            @Override
            public void handle(CellEditEvent<Product, String> event) {
                //拿到表格中所在的該筆紀錄(是一筆學生物件)
                Product prod = table_product.getSelectionModel().getSelectedItem();
                prod.setProduct_id(event.getNewValue()); //將該筆學生物件修改成新的值
            }
        });

        //學生姓名欄位若有修改驅動這個方法
        col_name.setOnEditCommit(new EventHandler<CellEditEvent<Product, String>>() {
            @Override
            public void handle(CellEditEvent<Product, String> event) {
                //取得該筆紀錄的方式有以下3種寫法:
                //Product stud = event.getTableView().getItems().get(event.getTablePosition().getRow());
                //Product stud = (Product) event.getTableView().getItems().get(event.getTablePosition().getRow());
                Product prod = table_product.getSelectionModel().getSelectedItem();
                System.out.println(event.getNewValue());
                prod.setName(event.getNewValue());
                //也可這樣更新新值寫法2:
                //((Product) event.getTableView().getItems().get(event.getTablePosition().getRow())).setName(t.getNewValue());
            }
        });

        col_description.setOnEditCommit(new EventHandler<CellEditEvent<Product, String>>() {
            @Override
            public void handle(CellEditEvent<Product, String> event) {

                Product prod = table_product.getSelectionModel().getSelectedItem();
                System.out.println(event.getNewValue());
                prod.setDescription(event.getNewValue());

            }
        });

        col_photo.setOnEditCommit(new EventHandler<CellEditEvent<Product, String>>() {
            @Override
            public void handle(CellEditEvent<Product, String> event) {

                Product prod = table_product.getSelectionModel().getSelectedItem();
                System.out.println(event.getNewValue());
                prod.setPhoto(event.getNewValue());

            }
        });

        col_price.setOnEditCommit(new EventHandler<CellEditEvent<Product, Integer>>() {
            @Override
            public void handle(CellEditEvent<Product, Integer> event) {

                Product prod = table_product.getSelectionModel().getSelectedItem();
                System.out.println(event.getNewValue());
                prod.setPrice(event.getNewValue());

            }
        });

    }

    /*
    private void onPriceEditCommit(CellEditEvent<Product, String> event) {
        Product prod = table_product.getSelectionModel().getSelectedItem();
        prod.setPrice(Integer.parseInt(event.getNewValue()));
    }

     */
    private void loadTable() {
        int totalPage = (int) (Math.ceil(products.size() * 1.0 / RowsPerPage));
        pagination.setPageCount(totalPage);
        //pagination.setCurrentPageIndex(0);
        int currentpg = pagination.getCurrentPageIndex();
        showTablePage(currentpg, RowsPerPage);
        tp_display(log_pane_product, "讀取產品");
    }

    private void loadTable_ord() {
        int totalPage = (int) (Math.ceil(orders.size() * 1.0 / RowsPerPage));
        pagination1.setPageCount(totalPage);
        //pagination.setCurrentPageIndex(0);
        int currentpg = pagination1.getCurrentPageIndex();
        showTablePage_ord(currentpg, RowsPerPage);
        tp_display(log_pane_ord, "讀取訂單");
    }

    private void showTablePage_ord(int pg, int row_per_pg) {
        table_order.getItems().clear(); //先清除表格內容
        int from = pg * row_per_pg;  //計算在此頁面顯示第幾筆到第幾筆
        int to = Math.min(from + row_per_pg, orders.size());
        //products一筆一筆加到表格中
        for (int i = from; i < to; i++) {
            table_order.getItems().add(orders.get(i));
        }
    }

    private void showTablePage(int pg, int row_per_pg) {
        table_product.getItems().clear(); //先清除表格內容
        int from = pg * row_per_pg;  //計算在此頁面顯示第幾筆到第幾筆
        int to = Math.min(from + row_per_pg, products.size());
        //products一筆一筆加到表格中
        for (int i = from; i < to; i++) {
            table_product.getItems().add(products.get(i));
        }
    }

    @FXML
    private void showOrder(ActionEvent event) {
        orddao.getAllOrder();

        orders = orddao.getAllOrder();

        //若students是ObservableList<Student>要這樣寫才可行:
        //students.addAll( stdao.getAllStudents()); 
        loadTable_ord();
        tp_display(log_pane_ord, "資料庫讀取完畢，已顯示最新訂單列表");
    }

    @FXML
    private void ord_delete(ActionEvent event) {
        Order ord = table_order.getSelectionModel().getSelectedItem();
        String id = ord.getOrder_num();
        String recipt_num = ord.getRecipt_num();

        boolean sucess = false;
        sucess = orddao.delete(id, recipt_num);
        if (sucess) {
            orders = orddao.getAllOrder();
            loadTable_ord();
            tp_display(log_pane_ord, "訂單刪除成功");
        } else {
            tp_display(log_pane_ord, "訂單刪除失敗");
        }
    }

    @FXML
    private void findPhone(ActionEvent event) {
        orders.clear();
        orders = orddao.selectByPhone(queryPhone.getText());
        //orders.add(orddao.selectByPhone(queryPhone.getText()));
        //products = prodao.selectByID(queryID.getText());
        loadTable_ord();
        tp_display(log_pane_ord, "查找電話完畢");
    }

    @FXML
    private void findUser(ActionEvent event) {
        orders = orddao.selectByUser(queryUser.getText());
        loadTable_ord();
        tp_display(log_pane_ord, "查找客戶完畢\r");
    }

}
