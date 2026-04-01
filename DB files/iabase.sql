-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: iabase
-- ------------------------------------------------------
-- Server version	8.0.44

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
-- Table structure for table `flashcard_tags`
--

DROP TABLE IF EXISTS `flashcard_tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flashcard_tags` (
  `flashcard_id` int NOT NULL,
  `tag_id` int NOT NULL,
  PRIMARY KEY (`flashcard_id`,`tag_id`),
  KEY `tag_id` (`tag_id`),
  CONSTRAINT `flashcard_tags_ibfk_1` FOREIGN KEY (`flashcard_id`) REFERENCES `flashcards` (`id`) ON DELETE CASCADE,
  CONSTRAINT `flashcard_tags_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flashcard_tags`
--

LOCK TABLES `flashcard_tags` WRITE;
/*!40000 ALTER TABLE `flashcard_tags` DISABLE KEYS */;
INSERT INTO `flashcard_tags` VALUES (27,4),(28,4),(32,4),(21,5),(22,5),(27,5),(32,5),(14,6),(15,6),(16,6);
/*!40000 ALTER TABLE `flashcard_tags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flashcards`
--

DROP TABLE IF EXISTS `flashcards`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flashcards` (
  `id` int NOT NULL AUTO_INCREMENT,
  `question` varchar(255) NOT NULL,
  `answer` varchar(255) NOT NULL,
  `active` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flashcards`
--

LOCK TABLES `flashcards` WRITE;
/*!40000 ALTER TABLE `flashcards` DISABLE KEYS */;
INSERT INTO `flashcards` VALUES (14,'What does CPU stand for?','Central Processing Unit.',0),(15,'Which data structure follows the Last In, First Out (LIFO) principle?','A stack.',0),(16,'What binary value does the boolean condition true usually correspond to?','1.',0),(17,'What is the main purpose of RAM in a computer?','To temporarily store data and programs currently in use.',0),(18,'What does HTML stand for?','HyperText Markup Language.',0),(19,'Which algorithmic notation is commonly used to describe time complexity?','Big-O notation.',0),(20,'In networking, what does IP stand for?','Internet Protocol.',0),(21,'What unit is used to measure electric current?','The ampere (A).',0),(22,'What is Newton\'s second law of motion?','Force equals mass times acceleration (F = ma).',0),(23,'What is the approximate speed of light in a vacuum?','About 3.0 × 10^8 metres per second.',0),(24,'What happens to the resistance of a wire if its length increases while material and thickness stay the same?','Its resistance increases.',0),(25,'What is the SI unit of energy?','The joule (J).',0),(26,'What does Ohm\'s law state?','Voltage equals current times resistance (V = IR).',0),(27,'What is the atomic number of an element?','The number of protons in its nucleus.',1),(28,'What is the pH of a neutral solution at 25°C?','7.',0),(29,'What type of bond involves the sharing of electrons between atoms?','A covalent bond.',0),(30,'What is the chemical symbol for sodium?','Na.',0),(31,'Which group of elements in the periodic table is known for being very unreactive?','The noble gases.',0),(32,'What is the name of the process by which a liquid turns into a gas?','Evaporation or vaporization.',0),(33,'What does a catalyst do in a chemical reaction?','It increases the reaction rate without being used up.',0);
/*!40000 ALTER TABLE `flashcards` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `questions`
--

DROP TABLE IF EXISTS `questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `questions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `question` varchar(255) NOT NULL,
  `firstAnswer` varchar(255) DEFAULT NULL,
  `secondAnswer` varchar(255) DEFAULT NULL,
  `thirdAnswer` varchar(255) DEFAULT NULL,
  `fourthAnswer` varchar(255) DEFAULT NULL,
  `correctAnswer` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `questions`
--

LOCK TABLES `questions` WRITE;
/*!40000 ALTER TABLE `questions` DISABLE KEYS */;
INSERT INTO `questions` VALUES (6,'What is the capital of France?','Berlin','Madrid','Paris','Rome',3),(7,'Which language is primarily spoken in Brazil?','Spanis','Portuguese','English','French',2),(8,'What is the longest river in the world?','Amazon','Nile','Yangtze','Mississippi',2),(10,'Which planet is known as the Red Planet?','Venus','Mars','Jupiter','Mercury',2),(11,'What is the largest mammal in the world?','Elephant','Blue whale','Giraffe','Orca',2),(12,'What is the chemical symbol for gold?','Go','Hg','Fe','Au',4),(13,'What gas do plants absorb from the atmosphere?','Oxygen','Sulfur dioxide','Xenon','Carbon dioxide',4),(14,'Who wrote The Lord of The Flies?','Jane Austen','Mark Twain','William Golding','William Shakespeare',3),(15,'Who wrote Romeo and Juliet?','Robertas Petrauskas','Mao Zedong','Xi Jinping','William Shakespeare',4);
/*!40000 ALTER TABLE `questions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `quiz_questions`
--

DROP TABLE IF EXISTS `quiz_questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `quiz_questions` (
  `quiz_id` int NOT NULL,
  `question_id` int NOT NULL,
  `position` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`quiz_id`,`question_id`),
  KEY `idx_quiz_questions_question_id` (`question_id`),
  CONSTRAINT `fk_qq_question` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_qq_quiz` FOREIGN KEY (`quiz_id`) REFERENCES `quizzes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `quiz_questions`
--

LOCK TABLES `quiz_questions` WRITE;
/*!40000 ALTER TABLE `quiz_questions` DISABLE KEYS */;
INSERT INTO `quiz_questions` VALUES (9,6,1),(9,7,2),(9,8,3),(10,10,1),(10,11,2),(10,12,3),(10,13,4),(11,14,1),(11,15,2);
/*!40000 ALTER TABLE `quiz_questions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `quiz_tags`
--

DROP TABLE IF EXISTS `quiz_tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `quiz_tags` (
  `quiz_id` int NOT NULL,
  `tag_id` int NOT NULL,
  PRIMARY KEY (`quiz_id`,`tag_id`),
  KEY `idx_quiz_tags_tag_id` (`tag_id`),
  CONSTRAINT `fk_qt_quiz` FOREIGN KEY (`quiz_id`) REFERENCES `quizzes` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_qt_tag` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `quiz_tags`
--

LOCK TABLES `quiz_tags` WRITE;
/*!40000 ALTER TABLE `quiz_tags` DISABLE KEYS */;
INSERT INTO `quiz_tags` VALUES (9,7),(10,8),(11,9);
/*!40000 ALTER TABLE `quiz_tags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `quizzes`
--

DROP TABLE IF EXISTS `quizzes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `quizzes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `quizName` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `quizName_UNIQUE` (`quizName`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `quizzes`
--

LOCK TABLES `quizzes` WRITE;
/*!40000 ALTER TABLE `quizzes` DISABLE KEYS */;
INSERT INTO `quizzes` VALUES (11,'Book quiz'),(9,'Geography quiz'),(10,'Science quiz');
/*!40000 ALTER TABLE `quizzes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'teacher'),(2,'student');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tags`
--

DROP TABLE IF EXISTS `tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tags` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tag_name` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tag_name` (`tag_name`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tags`
--

LOCK TABLES `tags` WRITE;
/*!40000 ALTER TABLE `tags` DISABLE KEYS */;
INSERT INTO `tags` VALUES (4,'Chemistry'),(6,'CS'),(7,'Geography'),(9,'Literature'),(5,'Physics'),(8,'Science');
/*!40000 ALTER TABLE `tags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role_id` int NOT NULL,
  `is_blocked` tinyint(1) DEFAULT '0',
  `must_change_password` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (4,'v4k4@gymn.edu','$2a$10$jvTye6AJGXgsvyyE7beILeJMo8Um3/aWEWf290q2WrIZBK2nAhX72',2,0,0),(5,'d@gmail.com','$2a$10$yM2YNRBF72xWRZGjsOVvbuoFXJjNOkDdxGoZRke2IkJTDLCE74lNe',1,0,0),(6,'student@gmail.com','$2a$10$W99aoFAcKr1u5ERGHqlUOuwxIzMgKrbSckVkmOg9c3L3tXX0CQ4bG',2,0,0),(7,'demo@demo.com','$2a$10$Nmto1PZGAwIMhobgXzplZ.Vkpb4QsYN5gA7S.mg7yPkbxkxWwr97y',1,0,0),(8,'demos@demo.com','$2a$10$cVw.VmLEGsW6vksAITRiHO9KTkFq2UcptHOqgIKAmn/mCSkGoNi5.',2,0,0);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-01 22:26:02
