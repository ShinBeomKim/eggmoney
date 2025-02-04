-- --------------------------------------------------------
-- 호스트:                          j11c204.p.ssafy.io
-- 서버 버전:                        10.6.18-MariaDB-0ubuntu0.22.04.1 - Ubuntu 22.04
-- 서버 OS:                        debian-linux-gnu
-- HeidiSQL 버전:                  12.6.0.6765
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- 테이블 mydatabase.savings_logs 구조 내보내기
DROP TABLE IF EXISTS `savings_logs`;
CREATE TABLE IF NOT EXISTS `savings_logs` (
  `balance` int(11) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `savings_id` bigint(20) DEFAULT NULL,
  `savings_log_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`savings_log_id`),
  KEY `FKsct78jubbnlrc0aq21ng2yytx` (`savings_id`),
  CONSTRAINT `FKsct78jubbnlrc0aq21ng2yytx` FOREIGN KEY (`savings_id`) REFERENCES `savings` (`savings_id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 테이블 데이터 mydatabase.savings_logs:~33 rows (대략적) 내보내기
INSERT INTO `savings_logs` (`balance`, `created_at`, `savings_id`, `savings_log_id`, `updated_at`) VALUES
	(10000, '2024-09-19 03:00:58.000000', 10, 1, '2024-09-19 03:00:58.000000'),
	(30000, '2024-09-25 16:24:05.368079', 10, 2, '2024-09-25 16:24:05.368079'),
	(40000, '2024-09-25 16:24:08.075759', 10, 3, '2024-09-25 16:24:08.075759'),
	(50000, '2024-09-25 16:24:08.991199', 10, 4, '2024-09-25 16:24:08.991199'),
	(60000, '2024-09-25 16:24:09.968740', 10, 5, '2024-09-25 16:24:09.968740'),
	(70000, '2024-09-27 14:21:59.121728', 10, 6, '2024-09-27 14:21:59.121728'),
	(80000, '2024-09-27 14:22:31.788471', 11, 7, '2024-09-27 14:22:31.788471'),
	(90000, '2024-09-27 14:22:41.151817', 11, 8, '2024-09-27 14:22:41.151817'),
	(100000, '2024-09-27 14:25:07.605642', 11, 9, '2024-09-27 14:25:07.605642'),
	(110000, '2024-09-27 14:47:31.751887', 11, 10, '2024-09-27 14:47:31.751887'),
	(120000, '2024-09-27 15:59:17.270564', 11, 11, '2024-09-27 15:59:17.270564'),
	(130000, '2024-09-27 17:39:45.926289', 11, 12, '2024-09-27 17:39:45.926289'),
	(140000, '2024-09-27 17:41:52.129774', 11, 13, '2024-09-27 17:41:52.129774'),
	(150000, '2024-09-27 17:43:48.343413', 11, 14, '2024-09-27 17:43:48.343413'),
	(160000, '2024-09-27 17:43:53.044728', 11, 15, '2024-09-27 17:43:53.044728'),
	(80000, '2024-09-27 17:44:24.055879', 10, 16, '2024-09-27 17:44:24.055879'),
	(90000, '2024-09-27 17:44:40.556392', 10, 17, '2024-09-27 17:44:40.556392'),
	(100000, '2024-09-27 17:44:52.399186', 10, 18, '2024-09-27 17:44:52.399186'),
	(170000, '2024-09-29 11:04:42.400895', 11, 19, '2024-09-29 11:04:42.400895'),
	(110000, '2024-09-29 11:08:49.378218', 10, 20, '2024-09-29 11:08:49.378218'),
	(120000, '2024-09-29 14:48:42.727374', 10, 21, '2024-09-29 14:48:42.727374'),
	(130000, '2024-09-30 11:19:59.462537', 10, 22, '2024-09-30 11:19:59.462537'),
	(140000, '2024-09-30 16:59:34.752457', 10, 23, '2024-09-30 16:59:34.752457'),
	(150000, '2024-10-02 09:13:25.880119', 10, 24, '2024-10-02 09:13:25.880119'),
	(160000, '2024-10-02 09:13:28.762253', 10, 25, '2024-10-02 09:13:28.762253'),
	(180000, '2024-10-04 15:54:38.258157', 11, 26, '2024-10-04 15:54:38.258157'),
	(170000, '2024-10-05 18:06:33.987947', 10, 27, '2024-10-05 18:06:33.987947'),
	(180000, '2024-10-07 13:31:54.034529', 10, 28, '2024-10-07 13:31:54.034529'),
	(3000, '2024-10-10 21:57:40.893848', 11001, 29, '2024-10-10 21:57:40.893848'),
	(6000, '2024-10-10 22:11:38.714992', 11001, 30, '2024-10-10 22:11:38.714992'),
	(3000, '2024-10-11 09:45:00.156570', 11003, 32, '2024-10-11 09:45:00.156570'),
	(750000, '2024-10-11 10:27:48.009760', 5606, 33, '2024-10-11 10:27:48.009760'),
	(760000, '2024-10-11 10:46:05.023857', 5606, 34, '2024-10-11 10:46:05.023857');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
