-- --------------------------------------------------------
-- 主機:                           127.0.0.1
-- 伺服器版本:                        10.5.9-MariaDB - mariadb.org binary distribution
-- 伺服器作業系統:                      Win64
-- HeidiSQL 版本:                  11.2.0.6213
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- 傾印 db_pos 的資料庫結構
CREATE DATABASE IF NOT EXISTS `db_pos` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `db_pos`;

-- 傾印  資料表 db_pos.order_detail 結構
CREATE TABLE IF NOT EXISTS `order_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_num` varchar(20) NOT NULL,
  `product_id` varchar(20) NOT NULL,
  `quantity` int(11) NOT NULL DEFAULT 0,
  `product_price` int(11) DEFAULT NULL,
  `product_name` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_order_detail_product` (`product_id`),
  KEY `FK_order_detail_order` (`order_num`),
  CONSTRAINT `FK_order_detail_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`),
  CONSTRAINT `FK_order_detail_sale_order` FOREIGN KEY (`order_num`) REFERENCES `sale_order` (`order_num`)
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8mb4;

-- 正在傾印表格  db_pos.order_detail 的資料：~3 rows (近似值)
/*!40000 ALTER TABLE `order_detail` DISABLE KEYS */;
REPLACE INTO `order_detail` (`id`, `order_num`, `product_id`, `quantity`, `product_price`, `product_name`) VALUES
	(67, 'ord-101', 'p-j-101', 1, 70, '奇異果汁'),
	(68, 'ord-102', 'p-j-102', 1, 80, '椰子汁'),
	(69, 'ord-102', 'p-j-104', 3, 100, '葡萄汁'),
	(70, 'ord-103', 'p-j-107', 1, 120, '櫻桃汁'),
	(71, 'ord-103', 'p-j-104', 1, 100, '葡萄汁'),
	(72, 'ord-103', 'p-t-114', 1, 50, '珍珠奶茶'),
	(73, 'ord-104', 'p-j-107', 1, 120, '櫻桃汁'),
	(74, 'ord-104', 'p-j-104', 1, 100, '葡萄汁');
/*!40000 ALTER TABLE `order_detail` ENABLE KEYS */;

-- 傾印  資料表 db_pos.product 結構
CREATE TABLE IF NOT EXISTS `product` (
  `product_id` varchar(20) NOT NULL,
  `category` varchar(50) NOT NULL,
  `name` varchar(150) NOT NULL,
  `price` int(11) NOT NULL,
  `photo` varchar(200) NOT NULL,
  `description` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 正在傾印表格  db_pos.product 的資料：~13 rows (近似值)
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
REPLACE INTO `product` (`product_id`, `category`, `name`, `price`, `photo`, `description`) VALUES
	('p-j-101', '果汁', '奇異果汁', 70, 'kiwi.png', '紐西蘭特選，酸甜的口味，令人忍不住再喝一口'),
	('p-j-102', '果汁', '椰子汁', 80, 'coconut.png', '產品描述'),
	('p-j-103', '果汁', '水蜜桃汁', 90, 'peach.png', '產品描述'),
	('p-j-104', '果汁', '葡萄汁', 100, 'grapes.png', '產品描述'),
	('p-j-105', '果汁', '草莓汁', 100, 'strawberry.png', '產品描述'),
	('p-j-106', '果汁', '芒果汁', 100, 'mango.png', '產品描述'),
	('p-j-107', '果汁', '櫻桃汁', 120, 'cherry.png', '產品描述'),
	('p-j-108', '果汁', '香蕉汁', 75, 'banana.png', '產品描述'),
	('p-j-109', '果汁', '橘子汁', 65, 'orange.png', '產品描述'),
	('p-j-110', '果汁', '西瓜汁', 60, 'watermelon.png', '產品描述'),
	('p-j-116', '果汁', '測試汁', 87, 'test.png', '測試介面'),
	('p-t-112', '茶飲', '紅茶', 45, 'blacktea.jpg', '產品描述'),
	('p-t-113', '茶飲', '綠茶', 45, 'greentea.jpg', '產品描述'),
	('p-t-114', '茶飲', '珍珠奶茶', 45, 'perlmilktea.jpg', '產品描述');
/*!40000 ALTER TABLE `product` ENABLE KEYS */;

-- 傾印  資料表 db_pos.sale_order 結構
CREATE TABLE IF NOT EXISTS `sale_order` (
  `order_num` varchar(20) NOT NULL,
  `order_date` datetime NOT NULL DEFAULT current_timestamp(),
  `total_price` double(22,0) NOT NULL DEFAULT 0,
  `customer_name` varchar(150) DEFAULT NULL,
  `customer_address` varchar(250) DEFAULT NULL,
  `customer_phone` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`order_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 正在傾印表格  db_pos.sale_order 的資料：~2 rows (近似值)
/*!40000 ALTER TABLE `sale_order` DISABLE KEYS */;
REPLACE INTO `sale_order` (`order_num`, `order_date`, `total_price`, `customer_name`, `customer_address`, `customer_phone`) VALUES
	('ord-101', '2021-05-04 22:54:47', 70, '王範例', '高雄市楠梓區大學路一號', '093256789'),
	('ord-102', '2021-05-04 22:55:19', 380, '王範例', '高雄市楠梓區大學路一號', '093256789'),
	('ord-103', '2021-05-24 13:15:13', 270, '無姓名', '無地址', '無電話'),
	('ord-104', '2021-05-24 13:15:35', 220, '無姓名', '無地址', '無電話');
/*!40000 ALTER TABLE `sale_order` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
