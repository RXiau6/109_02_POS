/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Project.demo;

import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AppController {

    private final ProductDAO productDao = new ProductDAO();
    private final OrderDAO orderDao = new OrderDAO();

    //讓SpringBoot幫我們取得session物件
    @Autowired
    private HttpSession httpSession;

    //url根路徑 或稱為 家路徑 也適合
    @RequestMapping("/")
    public String home(Model model) {
        return "index.html";
    }

    //product list
    @RequestMapping("/list")
    public String list(Model model) {
        List<Product> products = productDao.getAllProducts();
        System.out.println(products.get(0).toString());
        model.addAttribute("listProducts", products);
        System.out.println(model.toString());
        return "list.html";
    }

    //order
    @RequestMapping("/order")
    public String pdHome(Model model) {

        // 產品首頁顯示的產品 可以選定一個產品類別或是全部
        List<Product> products = productDao.findByCate("果汁"); //只有果汁類
        //List<Product> products = productDao.getAllProducts(); //全部

        model.addAttribute("products", products);
        return "order.html";
    }

    //order search 1
    @RequestMapping("/searchProductByName")
    public String searchByName(@RequestParam("name") String name, Model model) {

        List<Product> products = productDao.findByNameContaining(name);
        model.addAttribute("products", products);
        return "order.html";
    }

    //order search 2
    @RequestMapping("/searchProductByPrice")
    public String searchByPrice(@RequestParam(name = "price") String strprice, Model model) {

        int price = 0;
        try {
            price = Integer.parseInt(strprice);
        } catch (NumberFormatException ex) {
            System.out.println("價格輸入格式錯誤");
            System.out.println(ex);
            return "redirect:" + "/order";  //跳到到根路徑網頁  等於回到order的意思         
        }

        List<Product> products = productDao.findByPriceLessThanEqual(price);
        model.addAttribute("products", products);
        return "order.html";
    }

    //order search 3
    @RequestMapping("/searchProductByCate")
    public String searchByCate(@RequestParam(name = "category") String cate, Model model) {

        System.out.println("cate:" + cate);
        List<Product> products = productDao.findByCate(cate);
        model.addAttribute("products", products);
        return "order.html";
    }

    //新增產品
    @GetMapping("/showNewProductForm")
    public String showNewProductForm(Model model) {
        // 新增一個產品物件，內容填入範例資料，全都不填入資料也是可以
        Product product = new Product();
        product.setProduct_id("p-j-000");
        product.setCategory("茶飲");
        product.setName("花草茶??");
        product.setPrice(95);
        //product.setPhoto("herbtea.jpg");
        product.setDescription("英式口味 風味獨特");
        model.addAttribute("product", product);
        return "new-product.html"; //渲染並開啟一個新增產品網頁
    }

    //確定新增一筆資料，進行寫入資料庫，並重新導向到網站的根路徑
    @PostMapping("/createProduct")
    //public String saveProduct(@ModelAttribute("product") Product product) {
    public String saveProduct(Product product) {
        productDao.insert(product);
        return "redirect:/";
    }

    // 修改產品Modify Update
// 寫法1 使用GET傳遞產品編號 後端使用@RequestPath抓取路徑參數的方式
    // http://localhost:8080/showFormForUpdate/p-j-000
    @GetMapping("/showFormForUpdate/{pid}")
    public String showFormForUpdate(@PathVariable(value = "pid") String product_id, Model model) {
        // get product
        Product product = productDao.findById(product_id);
        System.out.println(product.getProduct_id());

        // product bean
        model.addAttribute("product", product);
        return "update-product.html";
    }

    /*    
    // 寫法2 使用GET傳遞產品編號 後端使用@RequestParam抓取參數的方式
    // http://localhost:8080/showFormForUpdate?pid=p-j-000
    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam(name="pid") String product_id, Model model) {
        // get product
        Product product = productDao.findById(product_id);
        System.out.println(product.getProduct_id());

        // product bean
        model.addAttribute("product", product);
        return "update-product.html";
    }
     */
    //確定更新這一筆資料，進行寫入資料庫，並重新導向到網站的根路徑
    @RequestMapping("/updateProduct")
    public String updateProduct(@ModelAttribute("product") Product product) {
        System.out.println(product.getProduct_id());
        this.productDao.update(product);
        return "redirect:/";
    }

    //產品刪除功能
    /*
    //寫法A  http://localhost:8080/deleteProduct/pid=p-j-000  可以刪除成功
    @GetMapping("/deleteProduct/{pid}")
    public String deleteProduct(@PathVariable(value = "pid") String product_id) {
        this.productDao.delete(product_id);
        return "redirect:/";
    }
     */
}
