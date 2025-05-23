-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: croissanthouse
-- ------------------------------------------------------
-- Server version	8.0.40

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
-- Table structure for table `items`
--

DROP TABLE IF EXISTS `items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `items` (
  `ItemID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(100) NOT NULL,
  `Price` decimal(10,2) DEFAULT NULL,
  `CategoryID` int NOT NULL,
  `SupplierID` int DEFAULT NULL,
  PRIMARY KEY (`ItemID`),
  KEY `CategoryID` (`CategoryID`),
  KEY `SupplierID` (`SupplierID`),
  CONSTRAINT `items_ibfk_1` FOREIGN KEY (`CategoryID`) REFERENCES `category` (`CategoryID`) ON DELETE CASCADE,
  CONSTRAINT `items_ibfk_2` FOREIGN KEY (`SupplierID`) REFERENCES `supplier` (`ID`) ON DELETE SET NULL,
  CONSTRAINT `items_chk_1` CHECK ((`Price` >= 0)),
  CONSTRAINT `items_chk_2` CHECK ((`Price` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `items`
--

LOCK TABLES `items` WRITE;
/*!40000 ALTER TABLE `items` DISABLE KEYS */;
INSERT INTO `items` VALUES (1,'Espresso',10.00,1,1),(2,'Americano',12.00,1,1),(3,'Coffee Latte',15.00,1,1),(4,'Spanish Latte',16.00,1,1),(5,'Coffee Mocha',14.00,1,1),(6,'Macchiato',13.00,1,1),(7,'Hot Chocolate',15.00,1,1),(8,'French Vanilla',16.00,1,1),(9,'Chai Latte',14.00,1,1),(10,'Salted Caramel',15.00,1,1),(11,'Tea',8.00,1,1),(12,'Nescafe',10.00,1,1),(13,'Orange',12.00,2,1),(14,'Lemon',12.00,2,1),(15,'Mojito',15.00,2,1),(16,'Ice Vanilla',14.00,2,1),(17,'Ice Chocolate',16.00,2,1),(18,'Ice Caramel',16.00,2,1),(19,'Ice Coffee',14.00,2,1),(20,'Ice Latte',15.00,2,1),(21,'Ice Mocha',16.00,2,1),(22,'Ice Americano',13.00,2,1),(23,'Spanish Latte Cold',16.00,2,1),(24,'Ice Tea',12.00,2,1),(25,'Brazic',7.00,3,2),(26,'Thyme Fingers',6.00,3,2),(27,'Anise Fingers',6.50,3,2),(28,'Coffee Biscuits',8.00,3,2),(29,'Qarshala',5.00,3,2),(30,'Donut',10.00,5,3),(31,'Brownie',12.00,5,3),(32,'English Cake',15.00,5,3),(33,'Cookie',8.00,5,3),(34,'Cheese Cake',20.00,5,3),(35,'Trillica Cake',22.00,5,3),(36,'Muffin',9.00,5,3),(37,'Large Cake',50.00,4,3),(38,'Small Cake',30.00,4,3),(39,'Shishbarak',25.00,6,4),(40,'Cheese Balls',20.00,6,4),(41,'Kibbeh',22.00,6,4),(42,'Pizza Rolls',18.00,6,4),(43,'Potato Borek',15.00,6,4),(44,'Cheese Borek',17.00,6,4),(45,'Cappy',8.00,7,1),(46,'Cola',7.00,7,1),(47,'Sprite',7.00,7,1),(48,'Water',3.00,7,1),(49,'XL',10.00,7,1),(50,'Soda',5.00,7,1),(51,'Croissant',NULL,8,NULL);
/*!40000 ALTER TABLE `items` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-24  1:18:09
