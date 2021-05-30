package javafx_drinkpos;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;
import models.Order;
import models.OrderDAO;
import models.OrderDetail;
import models.Product;
import models.ProductDAO;
import models.ReciptDAO;
import models.Recipt;

public class JuiceFXMLController implements Initializable {

    //放置飲料選單的窗格/視窗
    @FXML
    private AnchorPane menuPane;

    //放所有產品  產品編號  產品物件
    private final TreeMap<String, Product> product_dict = new TreeMap();

    //置放訂單明細項目，也是給表格顯示的資料項目
    ObservableList<OrderDetail> order_list;

    //點選的產品，當把產品放入購物籃會用到
    private Product selectedItem;

    //顯示點選到的產品名稱價格圖片
    @FXML
    private Label item_name;
    @FXML
    private Label item_price;
    @FXML
    private ImageView item_image;

    //買幾杯
    @FXML
    private ComboBox<String> quantity;

    //顯示訂單內容的表格 裡面存放的是OrderDetail訂單細節項目
    @FXML
    private TableView<OrderDetail> table;

    //顯示訂單總金額
    @FXML
    private TextArea display;

    //***********產生資料DAO來使用
    ProductDAO productDao = new ProductDAO();
    OrderDAO orderDao = new OrderDAO();
    ReciptDAO reciptDao = new ReciptDAO();
    @FXML
    private TextField acc_input;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //讀產品 從資料庫中讀入
        prepareProduct();

        //將所有可視元件初始化布置好，事件也準備好
        initMyComponents();
        /*
        String s1 = "/1SOOJHQ";
        String s2 = "/1SOOJH";
        String s3 = "@@@";
        String s4 = "\\";

        System.out.println("s1"+textCheck(s1)); 
        System.out.println("s2"+textCheck(s2));
        System.out.println("s3"+textCheck(s3));
        System.out.println("s4"+textCheck(s4));
         */

    }

    //準備產品的字典 從資料庫中讀入
    private void prepareProduct() {

        //***************從檔案或資料庫讀入產品菜單資訊
        List<Product> products = productDao.getAllProducts();

        //放入product_dict中點選產品與顯示產品比較方便
        for (Product product : products) {
            System.out.println(product.getCategory());
            product_dict.put(product.getProduct_id(), product);
        }

    }

    //初始化元件
    public void initMyComponents() {

        //數量combobox
        quantity.getItems().setAll("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        quantity.getSelectionModel().select(1);
        selectedItem = product_dict.firstEntry().getValue();

        //ObservableList    order_list有新增或刪除都會處動table的更新，也就是發生任何改變時都被通知
        order_list = FXCollections.observableArrayList();

        //表格table塞入三個欄位
        TableColumn<OrderDetail, String> order_item_name = new TableColumn("品名");
        order_item_name.setCellValueFactory(new PropertyValueFactory("product_name"));

        TableColumn<OrderDetail, Integer> order_item_price = new TableColumn<>("價格");
        order_item_price.setCellValueFactory(new PropertyValueFactory<>("product_price"));

        TableColumn order_item_qty = new TableColumn<>("數量");
        order_item_qty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        order_item_qty.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        table.setEditable(true);

        table.getColumns().addAll(order_item_name, order_item_price, order_item_qty);

        order_item_qty.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent event) {

                //印出細節內容看看裡面的內容是甚麼
                //System.out.println(event.getTablePosition());
                //System.out.println(event.getTablePosition().getRow());
                //System.out.println(((Order)event.getTableView().getItems().get(event.getTablePosition().getRow())).getQty());
                //System.out.println(event.getTableView().getItems().get(event.getTablePosition().getRow()));
                int new_val = (Integer) event.getNewValue();
                int row_num = event.getTablePosition().getRow();
                OrderDetail target = (OrderDetail) event.getTableView().getItems().get(row_num);

                //
                System.out.println(new_val);
                target.setQuantity(new_val);

                System.out.println(order_list.get(row_num).getQuantity());
                check_total();
                //顯示總金額於display
                String totalmsg = String.format("%s %d\n", "總金額:", check_total());
                display.setText(totalmsg);
            }
        });

        //表格最後一欄是空白，不要顯示!
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(250);

        table.setItems(order_list);

        //把果汁菜單填入產品窗格
        menuPane.getChildren().clear();
        menuPane.getChildren().add(getCategoryMenu("果汁"));

        System.out.println("OK");

    }

    //取得飲料種類的菜單:引數是--果汁 茶飲 咖啡
    public GridPane getCategoryMenu(String category) {

        //準備一個格子窗格
        GridPane menuGrid = new GridPane();
        menuGrid.setHgap(10); //設定間隔距離
        menuGrid.setVgap(10);

        int column = 0;
        int row = 0;

        for (String prod_id : product_dict.keySet()) {

            System.out.println("prod_id:" + prod_id);
            System.out.println(product_dict.get(prod_id).getCategory());

            if (product_dict.get(prod_id).getCategory().equals(category)) {

                AnchorPane productPane = getProductItemPane(product_dict.get(prod_id));

                //置放3個行(欄)
                if (column == 3) {
                    column = 0;
                    row++; //3行放完才換下一個列
                }
                menuGrid.add(productPane, column++, row); // (0,0)  (1,0)  (2,0)前面是行編號後面是列編號 <--第0個row
            }
        }

        //ScrollPane scrollPane = new ScrollPane(new AnchorPane(menuGrid));
        //scrollPane.setStyle("-fx-background-color:transparent;");
        //return menuPane;
        //return scrollPane;
        return menuGrid;

    }

    //這是產品菜單的小窗格 每個產品有一個專屬的窗格物件
    public AnchorPane getProductItemPane(Product product) {

        //準備一個茅點窗格
        AnchorPane itemPane = new AnchorPane();

        String css
                = "-fx-background-color: #FFF0AC;"
                + " -fx-background-radius: 25;"
                + "-fx-font-size: 20px;";

        itemPane.setStyle(css); //套用樣式

        //顯示產品名稱與價格
        Label nameLabel = new Label(product.getName());
        Label priceLabel = new Label("$" + product.getPrice());

        HBox hb_name_price = new HBox();
        hb_name_price.setAlignment(Pos.CENTER);
        hb_name_price.setSpacing(10);
        hb_name_price.getChildren().addAll(nameLabel, priceLabel);

        //放圖片
        ImageView image = new ImageView();
        image.setFitWidth(150);
        image.setFitHeight(100);

        //幾種讀圖檔的方法   Stream是InputStream輸入串流
        Image img = new Image(getClass().getResourceAsStream("/imgs/" + product.getPhoto()));
        //Image image = new Image(getClass().getResource("/imgs/" + product.getImgSrc()).toString());
        //Image image = new Image("/imgs/" +product.getImgSrc()); //不建議

        image.setImage(img);

        VBox vbox_product = new VBox();
        vbox_product.getChildren().addAll(hb_name_price, image);
        vbox_product.setPadding(new Insets(10, 10, 10, 10));
        vbox_product.setMaxWidth(150);

        itemPane.getChildren().addAll(vbox_product);

        //定義滑鼠事件mouse event滑鼠點下去
        itemPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                selectedItem = product; //放入購物籃會用到
                showSelectedItem(product);
            }
        });

        return itemPane;
    }

    public void showSelectedItem(Product product) {
        //顯示產品名稱價格與圖片
        item_name.setText(product.getName());
        item_price.setText("$" + product.getPrice());
        Image image = new Image(getClass().getResource("/imgs/" + product.getPhoto()).toString());
        item_image.setImage(image);

        //數量設定為1個
        quantity.getSelectionModel().select(1);
    }

    @FXML
    private void add_to_cart(ActionEvent event) {

        String prod_id = selectedItem.getProduct_id();
        System.out.println(prod_id); //印出看看哪個產品加入購物車

        //拿到訂購數量幾杯?
        int qty = Integer.parseInt(quantity.getSelectionModel().getSelectedItem());
        System.out.println(qty);

        //檢查購物車內是否有這項產品
        boolean duplication = false;

        for (OrderDetail od : order_list) {
            if (od.getProduct_id().equals(prod_id)) {
                duplication = true;
            }
        }

        if (duplication) {
            System.out.println(prod_id + "已經加入購物車");
        } else {

            //購物車加入現在點選的品項
            OrderDetail newOrdd = new OrderDetail();
            newOrdd.setProduct_id(product_dict.get(prod_id).getProduct_id());
            newOrdd.setProduct_name(product_dict.get(prod_id).getName());
            newOrdd.setProduct_price(product_dict.get(prod_id).getPrice());
            newOrdd.setQuantity(qty);
            order_list.add(newOrdd);
            System.out.println(prod_id);
        }

        //顯示總金額於display
        String totalmsg = String.format("%s %d\n", "總金額:", check_total());
        display.setText(totalmsg);

    }

    //計算總金額
    public int check_total() {
        double total = 0;
        //將所有的訂單顯示在表格   計算總金額
        for (OrderDetail od : order_list) {
            //加總
            total += od.getProduct_price() * od.getQuantity();
        }

        return (int) total;

    }

    //訂單刪除
    @FXML
    private void delete_order(ActionEvent event) {

        display.setText("");
        order_list.clear();
        //顯示總金額於display
        String totalmsg = String.format("%s %d\n", "總金額:", check_total());
        display.setText(totalmsg);

    }

    //是否含有非法字元，只允許英文、數字及"/"
    //need to fix;
    public static boolean textCheck(String str) {
        Pattern pattern = Pattern.compile("\\/\\w\\w\\w\\w\\w\\w\\w");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    //連接DB，取得最後發票號碼，並更新下一位號碼至DB
    private String recipt_num_gen() {
        String track = "AA";
        int num = orderDao.getRecipt_num();
        if (num == -1) {
            System.out.println("ERROR");
        } else {
            orderDao.setRecipt_num(num);
        }
        String recipt_num = String.format("%s" + "%08d", track, num);
        return recipt_num;
    }

    //結帳*******************這裡寫入訂單明細到資料庫
    @FXML
    private void check(ActionEvent event) {
        int sum = check_total();
        if (sum != 0) {
            String acc = acc_input.getText();
            int acc_len = acc_input.getLength();
            boolean chk = textCheck(acc); //確認載具是否含有非法字元
            boolean acc_addr = false; //是否使用載具
            
            if (acc_len == 0) {
                acc_addr = false;
                chk = true;
            } else if (acc_len == 8) {
                acc_addr = true;
            } else {
                chk = false;
                display.setText("錯誤，請檢查載具是否錯誤");
                //System.out.println(acc);
            }
            //分辨有無載具
            String recipt_num = recipt_num_gen(); //到此步驟確認無載具問題，產生發票號碼
            if (chk == true && acc_addr == false) {
                display.setText("已結帳，發票列印中...\n");
                display.appendText("發票號碼 : " + recipt_num + "\n");
            } else if (chk == true && acc_addr == true) {
                String msg = String.format("已結帳\n發票明細已儲存至載具\n(%s)", acc);
                display.setText(msg);
                System.out.println(acc);
            } else {
                System.out.println("textCheck_FAIL");
            }
            //append_order_to_csv(); //將這一筆訂單附加儲存到檔案或資料庫
            //這裡要取得不重複的order_num編號
            String order_num = orderDao.getMaxOrderNum();

            if (order_num == null) {
                order_num = "ord-100";
            }

            System.out.println(order_num);
            System.out.println(order_num.split("-")[1]);

            //將現有訂單編號加上1
            int serial_num = Integer.parseInt(order_num.split("-")[1]) + 1;
            System.out.println(serial_num);

            //每家公司都有其訂單或產品的編號系統，這裡用ord-xxx表之
            String new_order_num = "ord-" + serial_num;
            //將發票號碼匯入資料庫
            if (chk==true||acc_addr==false){
                Recipt rec = new Recipt();
                rec.setAccount(acc);
                rec.setTransaction_amount(sum);
                rec.setRecipt_num(recipt_num);
                rec.setCurrency("TWD");
                reciptDao.insert(rec);
            }
            //Cart crt = new Cart(new_order_num, "2021-05-01", 123, userName);
            Order crt = new Order();
            crt.setOrder_num(new_order_num);
            crt.setTotal_price(sum);
            crt.setCustomer_name("無姓名");
            crt.setCustomer_phtone("無電話");
            crt.setCustomer_address("無地址");
            crt.setRecipt_num(recipt_num); //寫入一筆訂單道資料庫
            orderDao.insertCart(crt);

            //逐筆寫入訂單明細
            for (int i = 0; i < order_list.size(); i++) {
                OrderDetail item = new OrderDetail();
                item.setOrder_num(new_order_num); //設定訂單編號
                item.setProduct_id(order_list.get(i).getProduct_id()); //設定產品編號
                item.setQuantity(order_list.get(i).getQuantity());//設定訂購數量 多少杯
                item.setProduct_price(order_list.get(i).getProduct_price()); //產品單價 建議不要這個欄位 不符合正規化
                item.setProduct_name(order_list.get(i).getProduct_name());//產品名稱 建議不要這個欄位 不符合正規化
                //item.setRecipt_num(order_list.get(i).getRecipt_num());//取得發票號碼

                orderDao.insertOrderDetailItem(item);
            }
            order_list.clear();
        } else {
            display.setText("購物車是空的\n將商品加入購物車再結帳吧！");
        }

    }

//選擇飲料菜單種類
    @FXML
    private void select_menu(ActionEvent event) {

        String category = ((Button) event.getSource()).getText();

        menuPane.getChildren().clear();//先刪除原有的窗格再加入新的類別窗格
        if (category.equals("茶飲")) {
            menuPane.getChildren().addAll(getCategoryMenu("茶飲"));
        } else if (category.equals("果汁")) {
            menuPane.getChildren().addAll(getCategoryMenu("果汁"));
        } else if (category.equals("咖啡")) {
            menuPane.getChildren().addAll(getCategoryMenu("咖啡"));
        }
    }

    @FXML
    private void delete_row(ActionEvent event) {
        Object selectedItem = table.getSelectionModel().getSelectedItem();
        table.getItems().remove(selectedItem);
        check_total();
        //顯示總金額於display
        String totalmsg = String.format("%s %d\n", "總金額:", check_total());
        display.setText(totalmsg);
    }
}
