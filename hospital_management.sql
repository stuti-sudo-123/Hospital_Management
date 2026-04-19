-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: hospital_management
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `administrators`
--

DROP TABLE IF EXISTS `administrators`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `administrators` (
  `admin_id` int NOT NULL,
  `fixed_password` varchar(255) NOT NULL DEFAULT 'Admin@1234',
  PRIMARY KEY (`admin_id`),
  CONSTRAINT `administrators_ibfk_1` FOREIGN KEY (`admin_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `administrators`
--

LOCK TABLES `administrators` WRITE;
/*!40000 ALTER TABLE `administrators` DISABLE KEYS */;
INSERT INTO `administrators` VALUES (1,'Admin@1234');
/*!40000 ALTER TABLE `administrators` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `appointments`
--

DROP TABLE IF EXISTS `appointments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointments` (
  `appointment_id` int NOT NULL AUTO_INCREMENT,
  `patient_id` int NOT NULL,
  `doctor_id` int NOT NULL,
  `slot_id` int NOT NULL,
  `status` enum('BOOKED','CONFIRMED','COMPLETED','CANCELLED') DEFAULT 'BOOKED',
  `reminder_sent` tinyint(1) DEFAULT '0',
  `reminder_confirmed` tinyint(1) DEFAULT NULL,
  `cancellation_reason` varchar(255) DEFAULT NULL,
  `cancelled_at` datetime DEFAULT NULL,
  `booked_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`appointment_id`),
  UNIQUE KEY `slot_id` (`slot_id`),
  KEY `patient_id` (`patient_id`),
  KEY `doctor_id` (`doctor_id`),
  CONSTRAINT `appointments_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`) ON DELETE CASCADE,
  CONSTRAINT `appointments_ibfk_2` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`doctor_id`) ON DELETE CASCADE,
  CONSTRAINT `appointments_ibfk_3` FOREIGN KEY (`slot_id`) REFERENCES `doctor_slots` (`slot_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointments`
--

LOCK TABLES `appointments` WRITE;
/*!40000 ALTER TABLE `appointments` DISABLE KEYS */;
INSERT INTO `appointments` VALUES (1,4,2,1,'COMPLETED',1,1,NULL,NULL,'2025-07-09 10:00:00'),(2,5,2,2,'CANCELLED',1,0,'Patient requested cancellation within allowed window','2025-07-09 15:00:00','2025-07-08 12:00:00'),(3,6,2,6,'BOOKED',0,NULL,NULL,NULL,'2025-07-11 08:30:00');
/*!40000 ALTER TABLE `appointments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `billing`
--

DROP TABLE IF EXISTS `billing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `billing` (
  `bill_id` int NOT NULL AUTO_INCREMENT,
  `patient_id` int NOT NULL,
  `appointment_id` int DEFAULT NULL,
  `consultation_fee` decimal(10,2) DEFAULT '0.00',
  `medicine_cost` decimal(10,2) DEFAULT '0.00',
  `test_cost` decimal(10,2) DEFAULT '0.00',
  `total_amount` decimal(10,2) GENERATED ALWAYS AS (((`consultation_fee` + `medicine_cost`) + `test_cost`)) STORED,
  `payment_status` enum('PENDING','PAID','WAIVED') DEFAULT 'PENDING',
  `generated_by` int DEFAULT NULL,
  `generated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`bill_id`),
  KEY `patient_id` (`patient_id`),
  KEY `appointment_id` (`appointment_id`),
  KEY `generated_by` (`generated_by`),
  CONSTRAINT `billing_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`) ON DELETE CASCADE,
  CONSTRAINT `billing_ibfk_2` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`appointment_id`) ON DELETE SET NULL,
  CONSTRAINT `billing_ibfk_3` FOREIGN KEY (`generated_by`) REFERENCES `administrators` (`admin_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `billing`
--

LOCK TABLES `billing` WRITE;
/*!40000 ALTER TABLE `billing` DISABLE KEYS */;
INSERT INTO `billing` (`bill_id`, `patient_id`, `appointment_id`, `consultation_fee`, `medicine_cost`, `test_cost`, `payment_status`, `generated_by`, `generated_at`) VALUES (1,4,1,500.00,350.00,800.00,'PAID',1,'2026-04-19 11:01:23'),(2,5,2,500.00,0.00,0.00,'WAIVED',1,'2026-04-19 11:01:23'),(3,6,3,500.00,0.00,0.00,'PENDING',1,'2026-04-19 11:01:23');
/*!40000 ALTER TABLE `billing` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `complaints`
--

DROP TABLE IF EXISTS `complaints`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `complaints` (
  `complaint_id` int NOT NULL AUTO_INCREMENT,
  `patient_id` int NOT NULL,
  `description` text NOT NULL,
  `status` enum('PENDING','RESOLVED') DEFAULT 'PENDING',
  `resolved_by` int DEFAULT NULL,
  `resolved_at` datetime DEFAULT NULL,
  `submitted_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`complaint_id`),
  KEY `patient_id` (`patient_id`),
  KEY `resolved_by` (`resolved_by`),
  CONSTRAINT `complaints_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`) ON DELETE CASCADE,
  CONSTRAINT `complaints_ibfk_2` FOREIGN KEY (`resolved_by`) REFERENCES `administrators` (`admin_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `complaints`
--

LOCK TABLES `complaints` WRITE;
/*!40000 ALTER TABLE `complaints` DISABLE KEYS */;
INSERT INTO `complaints` VALUES (1,5,'My appointment was cancelled without proper notification. Requesting a refund.','RESOLVED',1,'2025-07-10 09:30:00','2025-07-09 20:00:00'),(2,4,'The billing amount seems incorrect ? charged for a test that was not performed.','PENDING',NULL,NULL,'2025-07-10 11:00:00');
/*!40000 ALTER TABLE `complaints` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `consultation_notes`
--

DROP TABLE IF EXISTS `consultation_notes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `consultation_notes` (
  `note_id` int NOT NULL AUTO_INCREMENT,
  `appointment_id` int NOT NULL,
  `written_by` int NOT NULL,
  `notes` text,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`note_id`),
  UNIQUE KEY `appointment_id` (`appointment_id`),
  KEY `written_by` (`written_by`),
  CONSTRAINT `consultation_notes_ibfk_1` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`appointment_id`) ON DELETE CASCADE,
  CONSTRAINT `consultation_notes_ibfk_2` FOREIGN KEY (`written_by`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consultation_notes`
--

LOCK TABLES `consultation_notes` WRITE;
/*!40000 ALTER TABLE `consultation_notes` DISABLE KEYS */;
INSERT INTO `consultation_notes` VALUES (1,1,2,'Patient presents with mild chest discomfort on exertion. BP: 130/85 mmHg. ECG normal. Advised lifestyle modification and follow-up in 4 weeks.','2026-04-19 11:01:21'),(2,3,6,'Intern observation: Patient reports occasional palpitations after meals. Vitals stable. Awaiting senior doctor review.','2026-04-19 11:01:21');
/*!40000 ALTER TABLE `consultation_notes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `departments`
--

DROP TABLE IF EXISTS `departments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `departments` (
  `department_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`department_id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `departments`
--

LOCK TABLES `departments` WRITE;
/*!40000 ALTER TABLE `departments` DISABLE KEYS */;
INSERT INTO `departments` VALUES (1,'Cardiology','Heart and cardiovascular system'),(2,'Neurology','Brain, spinal cord, and nervous system'),(3,'Orthopedics','Bones, joints, and musculoskeletal system'),(4,'Dermatology','Skin, hair, and nail conditions'),(5,'Pediatrics','Medical care for infants and children'),(6,'General Medicine','Common illnesses and primary care'),(7,'ENT','Ear, nose, and throat specialist'),(8,'Gastroenterology','Digestive system and related organs'),(9,'Psychiatry','Mental health and behavioral disorders'),(10,'Pulmonology','Lungs and respiratory system');
/*!40000 ALTER TABLE `departments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctor_slots`
--

DROP TABLE IF EXISTS `doctor_slots`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctor_slots` (
  `slot_id` int NOT NULL AUTO_INCREMENT,
  `doctor_id` int NOT NULL,
  `slot_date` date NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `is_booked` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`slot_id`),
  UNIQUE KEY `doctor_id` (`doctor_id`,`slot_date`,`start_time`),
  CONSTRAINT `doctor_slots_ibfk_1` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`doctor_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctor_slots`
--

LOCK TABLES `doctor_slots` WRITE;
/*!40000 ALTER TABLE `doctor_slots` DISABLE KEYS */;
INSERT INTO `doctor_slots` VALUES (1,2,'2025-07-10','09:00:00','09:30:00',1),(2,2,'2025-07-10','09:30:00','10:00:00',1),(3,2,'2025-07-10','10:00:00','10:30:00',0),(4,2,'2025-07-11','09:00:00','09:30:00',0),(5,2,'2025-07-11','09:30:00','10:00:00',0),(6,2,'2025-07-12','11:00:00','11:30:00',1),(7,3,'2025-07-10','14:00:00','14:30:00',0),(8,3,'2025-07-10','14:30:00','15:00:00',0),(9,3,'2025-07-11','10:00:00','10:30:00',0),(10,3,'2025-07-12','15:00:00','15:30:00',0);
/*!40000 ALTER TABLE `doctor_slots` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctors`
--

DROP TABLE IF EXISTS `doctors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctors` (
  `doctor_id` int NOT NULL,
  `department_id` int DEFAULT NULL,
  `qualification` varchar(200) DEFAULT NULL,
  `experience_yrs` int DEFAULT '0',
  `contact_number` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`doctor_id`),
  KEY `department_id` (`department_id`),
  CONSTRAINT `doctors_ibfk_1` FOREIGN KEY (`doctor_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `doctors_ibfk_2` FOREIGN KEY (`department_id`) REFERENCES `departments` (`department_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctors`
--

LOCK TABLES `doctors` WRITE;
/*!40000 ALTER TABLE `doctors` DISABLE KEYS */;
INSERT INTO `doctors` VALUES (2,1,'MD Cardiology, AIIMS Delhi',12,'9876500001'),(3,2,'DM Neurology, PGI Chandigarh',8,'9876500002');
/*!40000 ALTER TABLE `doctors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feedback`
--

DROP TABLE IF EXISTS `feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedback` (
  `feedback_id` int NOT NULL AUTO_INCREMENT,
  `appointment_id` int NOT NULL,
  `patient_id` int NOT NULL,
  `doctor_id` int NOT NULL,
  `rating` tinyint DEFAULT NULL,
  `comment` text,
  `submitted_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`feedback_id`),
  UNIQUE KEY `appointment_id` (`appointment_id`),
  KEY `patient_id` (`patient_id`),
  KEY `doctor_id` (`doctor_id`),
  CONSTRAINT `feedback_ibfk_1` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`appointment_id`) ON DELETE CASCADE,
  CONSTRAINT `feedback_ibfk_2` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`) ON DELETE CASCADE,
  CONSTRAINT `feedback_ibfk_3` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`doctor_id`) ON DELETE CASCADE,
  CONSTRAINT `feedback_chk_1` CHECK ((`rating` between 1 and 5))
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedback`
--

LOCK TABLES `feedback` WRITE;
/*!40000 ALTER TABLE `feedback` DISABLE KEYS */;
INSERT INTO `feedback` VALUES (1,1,4,2,5,'Dr. Mehta was very thorough and patient. Explained everything clearly. Highly recommend!','2026-04-19 11:01:22');
/*!40000 ALTER TABLE `feedback` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medical_interns`
--

DROP TABLE IF EXISTS `medical_interns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medical_interns` (
  `intern_id` int NOT NULL,
  `supervisor_doctor_id` int DEFAULT NULL,
  `department_id` int DEFAULT NULL,
  `intern_code` varchar(50) DEFAULT NULL,
  `can_view_records` tinyint(1) DEFAULT '1',
  `can_enter_notes` tinyint(1) DEFAULT '1',
  `can_prescribe` tinyint(1) DEFAULT '0',
  `can_order_tests` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`intern_id`),
  UNIQUE KEY `intern_code` (`intern_code`),
  KEY `supervisor_doctor_id` (`supervisor_doctor_id`),
  KEY `department_id` (`department_id`),
  CONSTRAINT `medical_interns_ibfk_1` FOREIGN KEY (`intern_id`) REFERENCES `patients` (`patient_id`) ON DELETE CASCADE,
  CONSTRAINT `medical_interns_ibfk_2` FOREIGN KEY (`supervisor_doctor_id`) REFERENCES `doctors` (`doctor_id`) ON DELETE SET NULL,
  CONSTRAINT `medical_interns_ibfk_3` FOREIGN KEY (`department_id`) REFERENCES `departments` (`department_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medical_interns`
--

LOCK TABLES `medical_interns` WRITE;
/*!40000 ALTER TABLE `medical_interns` DISABLE KEYS */;
INSERT INTO `medical_interns` VALUES (6,2,1,'INT-2024-001',1,1,0,0);
/*!40000 ALTER TABLE `medical_interns` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medical_tests`
--

DROP TABLE IF EXISTS `medical_tests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medical_tests` (
  `test_id` int NOT NULL AUTO_INCREMENT,
  `appointment_id` int NOT NULL,
  `doctor_id` int NOT NULL,
  `patient_id` int NOT NULL,
  `test_name` varchar(150) NOT NULL,
  `test_description` varchar(255) DEFAULT NULL,
  `ordered_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`test_id`),
  KEY `appointment_id` (`appointment_id`),
  KEY `doctor_id` (`doctor_id`),
  KEY `patient_id` (`patient_id`),
  CONSTRAINT `medical_tests_ibfk_1` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`appointment_id`) ON DELETE CASCADE,
  CONSTRAINT `medical_tests_ibfk_2` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`doctor_id`) ON DELETE CASCADE,
  CONSTRAINT `medical_tests_ibfk_3` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medical_tests`
--

LOCK TABLES `medical_tests` WRITE;
/*!40000 ALTER TABLE `medical_tests` DISABLE KEYS */;
INSERT INTO `medical_tests` VALUES (1,1,2,4,'Lipid Profile','Complete lipid panel: Total Cholesterol, LDL, HDL, Triglycerides','2026-04-19 11:01:21'),(2,1,2,4,'ECG (Electrocardiogram)','Resting 12-lead ECG to assess cardiac rhythm and conduction','2026-04-19 11:01:21');
/*!40000 ALTER TABLE `medical_tests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patient_medical_background`
--

DROP TABLE IF EXISTS `patient_medical_background`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `patient_medical_background` (
  `background_id` int NOT NULL AUTO_INCREMENT,
  `patient_id` int NOT NULL,
  `surgeries` text,
  `current_meds` text,
  `allergies` text,
  PRIMARY KEY (`background_id`),
  KEY `patient_id` (`patient_id`),
  CONSTRAINT `patient_medical_background_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patient_medical_background`
--

LOCK TABLES `patient_medical_background` WRITE;
/*!40000 ALTER TABLE `patient_medical_background` DISABLE KEYS */;
INSERT INTO `patient_medical_background` VALUES (1,4,'Appendectomy (2019)','Metformin 500mg (pre-diabetic condition)','Penicillin ? causes rash'),(2,5,'None','None','Sulfa drugs ? causes hives; Peanuts ? anaphylaxis'),(3,6,'Tonsillectomy (2015)','Levothyroxine 50mcg (hypothyroidism)','None known');
/*!40000 ALTER TABLE `patient_medical_background` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patients`
--

DROP TABLE IF EXISTS `patients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `patients` (
  `patient_id` int NOT NULL,
  `date_of_birth` date DEFAULT NULL,
  `contact_number` varchar(20) DEFAULT NULL,
  `blood_group` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`patient_id`),
  CONSTRAINT `patients_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patients`
--

LOCK TABLES `patients` WRITE;
/*!40000 ALTER TABLE `patients` DISABLE KEYS */;
INSERT INTO `patients` VALUES (4,'1998-04-15','9876541001','B+'),(5,'2001-09-22','9876541002','O+'),(6,'2000-07-10','9876541003','A+');
/*!40000 ALTER TABLE `patients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prescription_medicines`
--

DROP TABLE IF EXISTS `prescription_medicines`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prescription_medicines` (
  `med_id` int NOT NULL AUTO_INCREMENT,
  `prescription_id` int NOT NULL,
  `medicine_name` varchar(150) NOT NULL,
  `dosage` varchar(100) DEFAULT NULL,
  `duration` varchar(100) DEFAULT NULL,
  `instructions` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`med_id`),
  KEY `prescription_id` (`prescription_id`),
  CONSTRAINT `prescription_medicines_ibfk_1` FOREIGN KEY (`prescription_id`) REFERENCES `prescriptions` (`prescription_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prescription_medicines`
--

LOCK TABLES `prescription_medicines` WRITE;
/*!40000 ALTER TABLE `prescription_medicines` DISABLE KEYS */;
INSERT INTO `prescription_medicines` VALUES (1,1,'Aspirin','75 mg','30 days','Once daily after breakfast'),(2,1,'Atorvastatin','10 mg','30 days','Once daily at bedtime'),(3,1,'Metoprolol','25 mg','14 days','Twice daily ? morning and evening');
/*!40000 ALTER TABLE `prescription_medicines` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prescriptions`
--

DROP TABLE IF EXISTS `prescriptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prescriptions` (
  `prescription_id` int NOT NULL AUTO_INCREMENT,
  `appointment_id` int NOT NULL,
  `doctor_id` int NOT NULL,
  `patient_id` int NOT NULL,
  `issued_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`prescription_id`),
  KEY `appointment_id` (`appointment_id`),
  KEY `doctor_id` (`doctor_id`),
  KEY `patient_id` (`patient_id`),
  CONSTRAINT `prescriptions_ibfk_1` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`appointment_id`) ON DELETE CASCADE,
  CONSTRAINT `prescriptions_ibfk_2` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`doctor_id`) ON DELETE CASCADE,
  CONSTRAINT `prescriptions_ibfk_3` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prescriptions`
--

LOCK TABLES `prescriptions` WRITE;
/*!40000 ALTER TABLE `prescriptions` DISABLE KEYS */;
INSERT INTO `prescriptions` VALUES (1,1,2,4,'2026-04-19 11:01:21');
/*!40000 ALTER TABLE `prescriptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `symptom_department_map`
--

DROP TABLE IF EXISTS `symptom_department_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `symptom_department_map` (
  `map_id` int NOT NULL AUTO_INCREMENT,
  `symptom_id` int NOT NULL,
  `department_id` int NOT NULL,
  PRIMARY KEY (`map_id`),
  UNIQUE KEY `symptom_id` (`symptom_id`,`department_id`),
  KEY `department_id` (`department_id`),
  CONSTRAINT `symptom_department_map_ibfk_1` FOREIGN KEY (`symptom_id`) REFERENCES `symptoms` (`symptom_id`) ON DELETE CASCADE,
  CONSTRAINT `symptom_department_map_ibfk_2` FOREIGN KEY (`department_id`) REFERENCES `departments` (`department_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `symptom_department_map`
--

LOCK TABLES `symptom_department_map` WRITE;
/*!40000 ALTER TABLE `symptom_department_map` DISABLE KEYS */;
INSERT INTO `symptom_department_map` VALUES (1,1,1),(2,2,1),(28,2,10),(3,3,1),(4,4,2),(29,4,6),(5,5,2),(6,6,2),(7,7,2),(8,8,3),(9,9,3),(10,10,3),(11,11,4),(12,12,4),(13,13,4),(14,14,6),(15,15,6),(16,16,7),(17,17,7),(18,18,8),(19,19,8),(20,20,8),(21,21,8),(22,22,9),(23,23,9),(24,24,9),(25,25,10),(26,26,10),(27,27,10);
/*!40000 ALTER TABLE `symptom_department_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `symptoms`
--

DROP TABLE IF EXISTS `symptoms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `symptoms` (
  `symptom_id` int NOT NULL AUTO_INCREMENT,
  `symptom_name` varchar(150) NOT NULL,
  PRIMARY KEY (`symptom_id`),
  UNIQUE KEY `symptom_name` (`symptom_name`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `symptoms`
--

LOCK TABLES `symptoms` WRITE;
/*!40000 ALTER TABLE `symptoms` DISABLE KEYS */;
INSERT INTO `symptoms` VALUES (18,'abdominal pain'),(22,'anxiety'),(9,'back pain'),(27,'breathing difficulty'),(1,'chest pain'),(15,'cold'),(25,'cough'),(23,'depression'),(21,'diarrhea'),(5,'dizziness'),(17,'ear pain'),(14,'fever'),(10,'fracture'),(13,'hair loss'),(4,'headache'),(24,'insomnia'),(12,'itching'),(8,'joint pain'),(7,'memory loss'),(19,'nausea'),(3,'palpitations'),(6,'seizures'),(2,'shortness of breath'),(11,'skin rash'),(16,'sore throat'),(20,'vomiting'),(26,'wheezing');
/*!40000 ALTER TABLE `symptoms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `test_reports`
--

DROP TABLE IF EXISTS `test_reports`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `test_reports` (
  `report_id` int NOT NULL AUTO_INCREMENT,
  `test_id` int NOT NULL,
  `uploaded_by` int NOT NULL,
  `report_number` varchar(50) NOT NULL,
  `report_title` varchar(200) NOT NULL,
  `status` enum('PENDING','UPLOADED','REVIEWED') DEFAULT 'UPLOADED',
  `result_summary` varchar(500) DEFAULT NULL,
  `result_status` enum('NORMAL','ABNORMAL','CRITICAL') NOT NULL,
  `finding_1_label` varchar(150) DEFAULT NULL,
  `finding_1_value` varchar(100) DEFAULT NULL,
  `finding_1_unit` varchar(50) DEFAULT NULL,
  `finding_1_range` varchar(100) DEFAULT NULL,
  `finding_1_flag` enum('NORMAL','LOW','HIGH','CRITICAL') DEFAULT NULL,
  `finding_2_label` varchar(150) DEFAULT NULL,
  `finding_2_value` varchar(100) DEFAULT NULL,
  `finding_2_unit` varchar(50) DEFAULT NULL,
  `finding_2_range` varchar(100) DEFAULT NULL,
  `finding_2_flag` enum('NORMAL','LOW','HIGH','CRITICAL') DEFAULT NULL,
  `finding_3_label` varchar(150) DEFAULT NULL,
  `finding_3_value` varchar(100) DEFAULT NULL,
  `finding_3_unit` varchar(50) DEFAULT NULL,
  `finding_3_range` varchar(100) DEFAULT NULL,
  `finding_3_flag` enum('NORMAL','LOW','HIGH','CRITICAL') DEFAULT NULL,
  `finding_4_label` varchar(150) DEFAULT NULL,
  `finding_4_value` varchar(100) DEFAULT NULL,
  `finding_4_unit` varchar(50) DEFAULT NULL,
  `finding_4_range` varchar(100) DEFAULT NULL,
  `finding_4_flag` enum('NORMAL','LOW','HIGH','CRITICAL') DEFAULT NULL,
  `doctor_notes` text,
  `lab_name` varchar(150) DEFAULT 'Hospital Central Lab',
  `lab_technician` varchar(150) DEFAULT NULL,
  `sample_collected_at` datetime DEFAULT NULL,
  `report_generated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `uploaded_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`report_id`),
  UNIQUE KEY `test_id` (`test_id`),
  UNIQUE KEY `report_number` (`report_number`),
  KEY `uploaded_by` (`uploaded_by`),
  CONSTRAINT `test_reports_ibfk_1` FOREIGN KEY (`test_id`) REFERENCES `medical_tests` (`test_id`) ON DELETE CASCADE,
  CONSTRAINT `test_reports_ibfk_2` FOREIGN KEY (`uploaded_by`) REFERENCES `administrators` (`admin_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `test_reports`
--

LOCK TABLES `test_reports` WRITE;
/*!40000 ALTER TABLE `test_reports` DISABLE KEYS */;
INSERT INTO `test_reports` VALUES (1,1,1,'RPT-2025-001','Lipid Profile Report','REVIEWED','Borderline dyslipidemia detected. Low HDL and elevated LDL noted.','ABNORMAL','Total Cholesterol','198','mg/dL','< 200 mg/dL','NORMAL','LDL Cholesterol','125','mg/dL','< 100 mg/dL','HIGH','HDL Cholesterol','45','mg/dL','> 60 mg/dL','LOW','Triglycerides','165','mg/dL','< 150 mg/dL','HIGH','Diet modification advised. Reduce saturated fats and increase physical activity. Follow-up lipid panel in 3 months.','Hospital Central Lab','Mr. Ramesh Joshi','2025-07-10 07:30:00','2025-07-10 11:00:00','2026-04-19 11:08:14'),(2,2,1,'RPT-2025-002','ECG (Electrocardiogram) Report','REVIEWED','Normal resting ECG. No acute ischaemic changes detected.','NORMAL','Heart Rate','78','bpm','60?100 bpm','NORMAL','PR Interval','160','ms','120?200 ms','NORMAL','QRS Duration','90','ms','80?120 ms','NORMAL','QT Interval','380','ms','350?440 ms','NORMAL','Normal sinus rhythm. No ST elevation or depression. No T-wave abnormalities. Routine follow-up advised.','Hospital Cardiology Lab','Ms. Kavita Rao','2025-07-10 08:00:00','2025-07-10 10:30:00','2026-04-19 11:08:14');
/*!40000 ALTER TABLE `test_reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `full_name` varchar(150) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` enum('PATIENT','DOCTOR','ADMIN','MEDICAL_INTERN') NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Admin User','admin@hospital.com','Admin@1234','ADMIN','2026-04-19 11:01:21'),(2,'Dr. Anil Mehta','anil.mehta@hospital.com','Doc@1234','DOCTOR','2026-04-19 11:01:21'),(3,'Dr. Priya Sharma','priya.sharma@hospital.com','Doc@5678','DOCTOR','2026-04-19 11:01:21'),(4,'Rahul Patel','rahul.patel@gmail.com','Pat@1234','PATIENT','2026-04-19 11:01:21'),(5,'Sneha Desai','sneha.desai@gmail.com','Pat@5678','PATIENT','2026-04-19 11:01:21'),(6,'Kiran Shah','kiran.shah@gmail.com','Pat@9012','MEDICAL_INTERN','2026-04-19 11:01:21');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `vw_appointment_details`
--

DROP TABLE IF EXISTS `vw_appointment_details`;
/*!50001 DROP VIEW IF EXISTS `vw_appointment_details`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_appointment_details` AS SELECT 
 1 AS `appointment_id`,
 1 AS `status`,
 1 AS `booked_at`,
 1 AS `patient_name`,
 1 AS `doctor_name`,
 1 AS `specialisation`,
 1 AS `slot_date`,
 1 AS `start_time`,
 1 AS `end_time`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `vw_available_slots`
--

DROP TABLE IF EXISTS `vw_available_slots`;
/*!50001 DROP VIEW IF EXISTS `vw_available_slots`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_available_slots` AS SELECT 
 1 AS `slot_id`,
 1 AS `doctor_id`,
 1 AS `doctor_name`,
 1 AS `specialisation`,
 1 AS `slot_date`,
 1 AS `start_time`,
 1 AS `end_time`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `vw_billing_summary`
--

DROP TABLE IF EXISTS `vw_billing_summary`;
/*!50001 DROP VIEW IF EXISTS `vw_billing_summary`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_billing_summary` AS SELECT 
 1 AS `bill_id`,
 1 AS `patient_name`,
 1 AS `appointment_id`,
 1 AS `appointment_date`,
 1 AS `consultation_fee`,
 1 AS `medicine_cost`,
 1 AS `test_cost`,
 1 AS `total_amount`,
 1 AS `payment_status`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `vw_doctor_details`
--

DROP TABLE IF EXISTS `vw_doctor_details`;
/*!50001 DROP VIEW IF EXISTS `vw_doctor_details`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_doctor_details` AS SELECT 
 1 AS `doctor_id`,
 1 AS `doctor_name`,
 1 AS `email`,
 1 AS `qualification`,
 1 AS `experience_yrs`,
 1 AS `contact_number`,
 1 AS `specialisation`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `vw_patient_full_profile`
--

DROP TABLE IF EXISTS `vw_patient_full_profile`;
/*!50001 DROP VIEW IF EXISTS `vw_patient_full_profile`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_patient_full_profile` AS SELECT 
 1 AS `user_id`,
 1 AS `full_name`,
 1 AS `email`,
 1 AS `date_of_birth`,
 1 AS `contact_number`,
 1 AS `blood_group`,
 1 AS `surgeries`,
 1 AS `current_meds`,
 1 AS `allergies`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `vw_pending_complaints`
--

DROP TABLE IF EXISTS `vw_pending_complaints`;
/*!50001 DROP VIEW IF EXISTS `vw_pending_complaints`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_pending_complaints` AS SELECT 
 1 AS `complaint_id`,
 1 AS `patient_name`,
 1 AS `description`,
 1 AS `status`,
 1 AS `submitted_at`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `vw_appointment_details`
--

/*!50001 DROP VIEW IF EXISTS `vw_appointment_details`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = cp850 */;
/*!50001 SET character_set_results     = cp850 */;
/*!50001 SET collation_connection      = cp850_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_appointment_details` AS select `a`.`appointment_id` AS `appointment_id`,`a`.`status` AS `status`,`a`.`booked_at` AS `booked_at`,`pu`.`full_name` AS `patient_name`,`du`.`full_name` AS `doctor_name`,`dep`.`name` AS `specialisation`,`ds`.`slot_date` AS `slot_date`,`ds`.`start_time` AS `start_time`,`ds`.`end_time` AS `end_time` from ((((((`appointments` `a` join `patients` `p` on((`a`.`patient_id` = `p`.`patient_id`))) join `users` `pu` on((`p`.`patient_id` = `pu`.`user_id`))) join `doctors` `d` on((`a`.`doctor_id` = `d`.`doctor_id`))) join `users` `du` on((`d`.`doctor_id` = `du`.`user_id`))) join `departments` `dep` on((`d`.`department_id` = `dep`.`department_id`))) join `doctor_slots` `ds` on((`a`.`slot_id` = `ds`.`slot_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vw_available_slots`
--

/*!50001 DROP VIEW IF EXISTS `vw_available_slots`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = cp850 */;
/*!50001 SET character_set_results     = cp850 */;
/*!50001 SET collation_connection      = cp850_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_available_slots` AS select `ds`.`slot_id` AS `slot_id`,`ds`.`doctor_id` AS `doctor_id`,`u`.`full_name` AS `doctor_name`,`dep`.`name` AS `specialisation`,`ds`.`slot_date` AS `slot_date`,`ds`.`start_time` AS `start_time`,`ds`.`end_time` AS `end_time` from (((`doctor_slots` `ds` join `doctors` `d` on((`ds`.`doctor_id` = `d`.`doctor_id`))) join `users` `u` on((`ds`.`doctor_id` = `u`.`user_id`))) join `departments` `dep` on((`d`.`department_id` = `dep`.`department_id`))) where (`ds`.`is_booked` = false) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vw_billing_summary`
--

/*!50001 DROP VIEW IF EXISTS `vw_billing_summary`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = cp850 */;
/*!50001 SET character_set_results     = cp850 */;
/*!50001 SET collation_connection      = cp850_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_billing_summary` AS select `b`.`bill_id` AS `bill_id`,`u`.`full_name` AS `patient_name`,`b`.`appointment_id` AS `appointment_id`,`ds`.`slot_date` AS `appointment_date`,`b`.`consultation_fee` AS `consultation_fee`,`b`.`medicine_cost` AS `medicine_cost`,`b`.`test_cost` AS `test_cost`,`b`.`total_amount` AS `total_amount`,`b`.`payment_status` AS `payment_status` from ((((`billing` `b` join `patients` `p` on((`b`.`patient_id` = `p`.`patient_id`))) join `users` `u` on((`p`.`patient_id` = `u`.`user_id`))) left join `appointments` `a` on((`b`.`appointment_id` = `a`.`appointment_id`))) left join `doctor_slots` `ds` on((`a`.`slot_id` = `ds`.`slot_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vw_doctor_details`
--

/*!50001 DROP VIEW IF EXISTS `vw_doctor_details`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = cp850 */;
/*!50001 SET character_set_results     = cp850 */;
/*!50001 SET collation_connection      = cp850_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_doctor_details` AS select `u`.`user_id` AS `doctor_id`,`u`.`full_name` AS `doctor_name`,`u`.`email` AS `email`,`d`.`qualification` AS `qualification`,`d`.`experience_yrs` AS `experience_yrs`,`d`.`contact_number` AS `contact_number`,`dep`.`name` AS `specialisation` from ((`users` `u` join `doctors` `d` on((`u`.`user_id` = `d`.`doctor_id`))) join `departments` `dep` on((`d`.`department_id` = `dep`.`department_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vw_patient_full_profile`
--

/*!50001 DROP VIEW IF EXISTS `vw_patient_full_profile`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = cp850 */;
/*!50001 SET character_set_results     = cp850 */;
/*!50001 SET collation_connection      = cp850_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_patient_full_profile` AS select `u`.`user_id` AS `user_id`,`u`.`full_name` AS `full_name`,`u`.`email` AS `email`,`p`.`date_of_birth` AS `date_of_birth`,`p`.`contact_number` AS `contact_number`,`p`.`blood_group` AS `blood_group`,`mb`.`surgeries` AS `surgeries`,`mb`.`current_meds` AS `current_meds`,`mb`.`allergies` AS `allergies` from ((`users` `u` join `patients` `p` on((`u`.`user_id` = `p`.`patient_id`))) left join `patient_medical_background` `mb` on((`p`.`patient_id` = `mb`.`patient_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vw_pending_complaints`
--

/*!50001 DROP VIEW IF EXISTS `vw_pending_complaints`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = cp850 */;
/*!50001 SET character_set_results     = cp850 */;
/*!50001 SET collation_connection      = cp850_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_pending_complaints` AS select `c`.`complaint_id` AS `complaint_id`,`u`.`full_name` AS `patient_name`,`c`.`description` AS `description`,`c`.`status` AS `status`,`c`.`submitted_at` AS `submitted_at` from ((`complaints` `c` join `patients` `p` on((`c`.`patient_id` = `p`.`patient_id`))) join `users` `u` on((`p`.`patient_id` = `u`.`user_id`))) where (`c`.`status` = 'PENDING') */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-19 11:14:19
