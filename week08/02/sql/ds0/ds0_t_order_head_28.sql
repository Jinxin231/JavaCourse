-- MySQL dump 10.13  Distrib 8.0.23, for Win64 (x86_64)
--
-- Host: localhost    Database: ds0
-- ------------------------------------------------------
-- Server version	5.7.24

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `t_order_head_28`
--

DROP TABLE IF EXISTS `t_order_head_28`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_order_head_28` (
  `order_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单编码',
  `store_code` varchar(6) COLLATE utf8_unicode_ci NOT NULL COMMENT '门店编码',
  `member_id` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT '会员ID',
  `member_name` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT '会员姓名',
  `sale_time` datetime NOT NULL COMMENT '销售时间',
  `transaction_value` decimal(14,4) NOT NULL COMMENT '交易金额',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `create_by` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '创建人',
  `last_modified_by` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '最后更新人',
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `enable_flag` bit(1) NOT NULL COMMENT '删除标识',
  PRIMARY KEY (`order_id`,`store_code`)
) ENGINE=InnoDB AUTO_INCREMENT=9981 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='订单头信息';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-06-26 16:07:58
