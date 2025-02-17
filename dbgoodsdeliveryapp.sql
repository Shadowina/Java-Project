-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jan 28, 2025 at 03:42 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `dbgoodsdeliveryapp`
--

-- --------------------------------------------------------

--
-- Table structure for table `cart`
--

CREATE TABLE `cart` (
  `cart_id` int(11) NOT NULL,
  `cart_cst_id` int(11) NOT NULL,
  `cart_prd_id` int(11) NOT NULL,
  `quantity` double NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `cart`
--

INSERT INTO `cart` (`cart_id`, `cart_cst_id`, `cart_prd_id`, `quantity`, `created_at`) VALUES
(6, 1, 2, 7, '2025-01-20 13:29:55'),
(7, 1, 6, 6, '2025-01-20 13:30:05'),
(8, 1, 10, 5, '2025-01-20 13:30:18'),
(9, 1, 7, 6, '2025-01-20 13:49:17'),
(10, 1, 16, 5, '2025-01-20 13:49:34'),
(11, 1, 18, 6, '2025-01-20 13:49:43'),
(12, 1, 3, 5, '2025-01-20 13:51:58'),
(13, 1, 4, 6.5, '2025-01-20 13:52:11'),
(14, 1, 11, 4.5, '2025-01-20 13:54:50'),
(15, 1, 19, 2.5, '2025-01-20 14:06:36'),
(16, 1, 8, 1, '2025-01-20 14:09:55'),
(17, 1, 1, 1, '2025-01-20 14:20:13'),
(18, 1, 20, 1.5, '2025-01-20 14:36:56'),
(19, 1, 12, 2, '2025-01-20 14:37:04'),
(20, 1, 17, 3, '2025-01-20 14:54:21'),
(21, 1, 13, 3, '2025-01-20 14:54:32'),
(77, 1, 1, 1, '2025-01-25 15:19:02'),
(78, 1, 5, 1, '2025-01-25 15:19:09'),
(79, 1, 6, 1, '2025-01-25 15:19:12'),
(80, 1, 8, 1, '2025-01-25 15:19:14'),
(81, 1, 1, 1, '2025-01-25 15:29:23'),
(82, 1, 4, 1, '2025-01-25 15:29:26'),
(83, 1, 6, 1, '2025-01-25 15:29:30'),
(136, 5, 2, 2.5, '2025-01-27 03:30:39'),
(137, 5, 6, 3, '2025-01-27 03:30:46');

-- --------------------------------------------------------

--
-- Table structure for table `customer`
--

CREATE TABLE `customer` (
  `customer_id` int(11) NOT NULL,
  `cst_user_id` int(11) NOT NULL,
  `cst_full_name` varchar(255) NOT NULL,
  `cst_status` varchar(20) NOT NULL DEFAULT 'ACTIVE',
  `cst_created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `cst_updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `customer`
--

INSERT INTO `customer` (`customer_id`, `cst_user_id`, `cst_full_name`, `cst_status`, `cst_created_at`, `cst_updated_at`) VALUES
(1, 7, 'tester5', 'ACTIVE', '2025-01-17 23:22:44', '2025-01-17 23:22:44'),
(2, 10, 'tester002', 'ACTIVE', '2025-01-18 15:16:48', '2025-01-26 22:37:24'),
(3, 13, 'tester001', 'ACTIVE', '2025-01-22 00:40:46', '2025-01-26 22:21:46'),
(4, 14, 'tester13', 'ACTIVE', '2025-01-22 00:45:41', '2025-01-22 00:45:41'),
(5, 25, 'test3 ', 'ACTIVE', '2025-01-27 02:59:37', '2025-01-27 02:59:37'),
(6, 30, 'test7', 'ACTIVE', '2025-01-28 00:57:54', '2025-01-28 00:57:54'),
(7, 33, 'customer002', 'ACTIVE', '2025-01-28 01:32:18', '2025-01-28 01:32:18');

-- --------------------------------------------------------

--
-- Table structure for table `driver`
--

CREATE TABLE `driver` (
  `driver_id` int(11) NOT NULL,
  `dr_user_id` int(11) NOT NULL,
  `dr_full_name` varchar(255) NOT NULL,
  `truck_registration` varchar(255) NOT NULL,
  `truck_capacity` int(11) NOT NULL,
  `driver_status` enum('Active','Inactive','busy') NOT NULL,
  `driver_created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `driver_updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `driver`
--

INSERT INTO `driver` (`driver_id`, `dr_user_id`, `dr_full_name`, `truck_registration`, `truck_capacity`, `driver_status`, `driver_created_at`, `driver_updated_at`) VALUES
(1, 6, 'tester4', '16727383', 200, 'Active', '2025-01-17 16:57:10', '2025-01-17 16:57:10'),
(2, 12, 'tester101', '739850', 101, 'Active', '2025-01-22 00:19:25', '2025-01-26 23:08:22'),
(3, 15, 'tester14', '1203637', 120, 'Inactive', '2025-01-26 18:25:12', '2025-01-26 18:25:13'),
(8, 28, 'Driver003', '12829', 100, 'Active', '2025-01-28 00:54:59', '2025-01-28 00:54:59'),
(10, 32, 'dark', '2893', 100, 'Active', '2025-01-28 01:29:14', '2025-01-28 01:29:14'),
(11, 34, 'light', '92920', 100, 'Active', '2025-01-28 01:42:05', '2025-01-28 01:42:05');

-- --------------------------------------------------------

--
-- Table structure for table `mission`
--

CREATE TABLE `mission` (
  `mission_id` int(11) NOT NULL,
  `msn_dr_id` int(11) NOT NULL,
  `msn_sch_id` int(11) NOT NULL,
  `mission_date` date NOT NULL,
  `mission_status` enum('Pending','In Progress','Completed','Cancelled') NOT NULL DEFAULT 'Pending',
  `mission_start_time` timestamp NULL DEFAULT NULL,
  `mission_end_time` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `mission`
--

INSERT INTO `mission` (`mission_id`, `msn_dr_id`, `msn_sch_id`, `mission_date`, `mission_status`, `mission_start_time`, `mission_end_time`, `created_at`, `updated_at`) VALUES
(2, 1, 1, '2025-01-21', 'Completed', NULL, NULL, '2025-01-21 13:54:05', '2025-01-22 15:05:32'),
(3, 1, 1, '2025-01-21', 'Completed', NULL, NULL, '2025-01-21 15:27:14', '2025-01-22 15:05:32'),
(4, 2, 1, '2025-01-22', 'Completed', NULL, '2025-01-26 00:52:04', '2025-01-22 00:49:24', '2025-01-26 00:52:04'),
(5, 1, 1, '2025-01-22', 'Pending', NULL, NULL, '2025-01-22 01:18:21', '2025-01-22 01:18:21'),
(6, 1, 1, '2025-01-22', 'Completed', NULL, '2025-01-24 23:41:34', '2025-01-22 01:41:48', '2025-01-24 23:41:34'),
(7, 1, 1, '2025-01-22', 'Pending', NULL, NULL, '2025-01-22 13:55:02', '2025-01-22 13:55:02'),
(8, 1, 1, '2025-01-22', 'Pending', NULL, NULL, '2025-01-22 14:08:58', '2025-01-22 14:08:58'),
(9, 2, 1, '2025-01-22', 'Completed', NULL, '2025-01-26 00:59:02', '2025-01-22 14:11:08', '2025-01-26 00:59:02'),
(10, 2, 1, '2025-01-22', 'Pending', NULL, NULL, '2025-01-22 14:15:47', '2025-01-22 14:15:47'),
(11, 1, 1, '2025-01-22', 'Completed', NULL, '2025-01-24 23:02:28', '2025-01-22 14:18:43', '2025-01-24 23:02:28'),
(12, 1, 1, '2025-01-22', 'Pending', NULL, NULL, '2025-01-22 14:26:24', '2025-01-22 14:26:24'),
(13, 2, 1, '2025-01-22', 'Pending', NULL, NULL, '2025-01-22 14:45:02', '2025-01-22 14:45:02'),
(15, 1, 2, '2025-01-25', 'Pending', NULL, NULL, '2025-01-25 22:03:07', '2025-01-25 22:03:07'),
(16, 2, 2, '2025-01-26', 'Completed', NULL, '2025-01-28 00:31:56', '2025-01-26 16:35:36', '2025-01-28 00:31:56'),
(17, 2, 2, '2025-01-26', 'Pending', NULL, NULL, '2025-01-26 16:49:28', '2025-01-26 16:49:28'),
(18, 2, 2, '2025-01-26', 'Pending', NULL, NULL, '2025-01-26 17:11:44', '2025-01-26 17:11:44'),
(19, 2, 2, '2025-01-26', 'Completed', NULL, '2025-01-28 00:31:56', '2025-01-26 18:08:57', '2025-01-28 00:31:56'),
(20, 2, 3, '2025-01-27', 'Completed', NULL, '2025-01-28 00:28:57', '2025-01-27 13:30:53', '2025-01-28 00:28:57'),
(21, 2, 3, '2025-01-27', 'Completed', NULL, '2025-01-28 00:28:57', '2025-01-27 13:54:27', '2025-01-28 00:28:57'),
(22, 2, 3, '2025-01-27', 'Completed', NULL, '2025-01-28 00:28:57', '2025-01-27 14:00:15', '2025-01-28 00:28:57'),
(23, 2, 3, '2025-01-27', 'Completed', NULL, '2025-01-28 00:31:56', '2025-01-27 14:16:33', '2025-01-28 00:31:56'),
(24, 2, 3, '2025-02-01', 'Pending', NULL, NULL, '2025-01-27 14:18:33', '2025-01-27 14:18:33'),
(25, 3, 3, '2025-02-01', 'Pending', NULL, NULL, '2025-01-27 15:52:06', '2025-01-27 15:52:06'),
(26, 2, 3, '2025-02-01', 'Pending', NULL, NULL, '2025-01-27 22:08:06', '2025-01-27 22:08:06'),
(27, 2, 3, '2025-02-02', 'Pending', NULL, NULL, '2025-01-28 00:24:38', '2025-01-28 00:24:38');

-- --------------------------------------------------------

--
-- Table structure for table `mission_order`
--

CREATE TABLE `mission_order` (
  `mord_mission_id` int(11) NOT NULL,
  `mord_order_id` int(11) NOT NULL,
  `sequence_number` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `mission_order`
--

INSERT INTO `mission_order` (`mord_mission_id`, `mord_order_id`, `sequence_number`) VALUES
(8, 16, 1),
(9, 15, 1),
(10, 10, 1),
(11, 12, 1),
(12, 13, 1),
(13, 17, 2),
(13, 19, 1),
(15, 18, 5),
(15, 23, 3),
(15, 24, 1),
(15, 25, 4),
(15, 26, 2),
(16, 27, 1),
(16, 28, 2),
(16, 29, 3),
(17, 30, 2),
(17, 31, 1),
(18, 32, 1),
(18, 33, 2),
(19, 34, 2),
(19, 35, 1),
(20, 36, 1),
(20, 37, 2),
(21, 38, 1),
(22, 39, 1),
(23, 40, 1),
(24, 41, 1),
(25, 42, 1),
(26, 43, 1),
(26, 44, 2),
(27, 45, 1),
(27, 46, 3),
(27, 47, 2);

-- --------------------------------------------------------

--
-- Table structure for table `order`
--

CREATE TABLE `order` (
  `order_id` int(11) NOT NULL,
  `order_cst_id` int(11) NOT NULL,
  `delivery_date` date NOT NULL,
  `delivery_address` text NOT NULL,
  `order_status` enum('Pending','Assigned','In Transit','Delivered','Cancelled') NOT NULL DEFAULT 'Pending',
  `order_created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `order`
--

INSERT INTO `order` (`order_id`, `order_cst_id`, `delivery_date`, `delivery_address`, `order_status`, `order_created_at`) VALUES
(10, 2, '2025-01-22', '001 Rue Saint-Julien', 'Assigned', '2025-01-22 00:44:20'),
(11, 4, '2025-01-22', '004 Rue Saint-Julien', 'Assigned', '2025-01-22 00:47:20'),
(12, 2, '2025-01-22', '003 Rue Saint-Julien', 'Assigned', '2025-01-22 01:14:04'),
(13, 3, '2025-01-22', '004 Rue Saint-Julien', 'Assigned', '2025-01-22 01:16:17'),
(14, 4, '2025-01-22', '005 Rue Saint-Julien', 'Assigned', '2025-01-22 01:17:36'),
(15, 3, '2025-01-22', '005 rue saint', 'Delivered', '2025-01-22 01:51:55'),
(16, 2, '2025-01-22', '007 check', 'Assigned', '2025-01-22 14:07:57'),
(17, 4, '2025-01-22', '005 Saint', 'Assigned', '2025-01-22 14:34:05'),
(18, 2, '2025-01-26', '006 Shake', 'Assigned', '2025-01-22 14:35:03'),
(19, 3, '2025-01-22', '008 Serve', 'Assigned', '2025-01-22 14:40:31'),
(23, 3, '2025-01-29', '001 Street', 'Assigned', '2025-01-25 20:08:57'),
(24, 3, '2025-01-30', '002 Street', 'Assigned', '2025-01-25 21:19:28'),
(25, 3, '2025-01-29', '003 street', 'Assigned', '2025-01-25 21:41:51'),
(26, 2, '2025-01-29', '004 Street', 'Assigned', '2025-01-25 21:43:39'),
(27, 2, '2025-01-30', '002 Stress', 'Delivered', '2025-01-26 16:19:12'),
(28, 3, '2025-01-30', '003 Stress', 'Delivered', '2025-01-26 16:19:55'),
(29, 4, '2025-01-30', '004 stress', 'Delivered', '2025-01-26 16:20:58'),
(30, 4, '2025-01-31', '003 trying', 'Assigned', '2025-01-26 16:47:38'),
(31, 2, '2025-01-31', '004 trying', 'Assigned', '2025-01-26 16:48:20'),
(32, 2, '2025-01-30', 'Testing1', 'Assigned', '2025-01-26 17:09:47'),
(33, 4, '2025-01-30', 'Testing2', 'Assigned', '2025-01-26 17:10:48'),
(34, 3, '2025-01-30', '002 test', 'Delivered', '2025-01-26 18:07:48'),
(35, 2, '2025-01-30', '007 Check', 'Delivered', '2025-01-26 18:08:22'),
(36, 2, '2025-01-30', '009 Shake', 'Delivered', '2025-01-27 10:36:45'),
(37, 2, '2025-01-30', '010 Shake', 'Delivered', '2025-01-27 10:40:20'),
(38, 2, '2025-01-30', 'address', 'Delivered', '2025-01-27 13:50:05'),
(39, 2, '2025-02-01', '87 rue Saint Sever', 'Delivered', '2025-01-27 13:59:20'),
(40, 2, '2025-02-01', 'uhaihsujka', 'Delivered', '2025-01-27 14:16:09'),
(41, 2, '2025-02-01', 'jwdhnljwn', 'Assigned', '2025-01-27 14:18:10'),
(42, 2, '2025-02-01', '6 ruin', 'Assigned', '2025-01-27 15:50:52'),
(43, 2, '2025-02-01', 'tee 1', 'Assigned', '2025-01-27 22:05:28'),
(44, 3, '2025-02-01', 'tay', 'Assigned', '2025-01-27 22:06:31'),
(45, 3, '2025-02-02', 'new one', 'Assigned', '2025-01-27 23:03:38'),
(46, 2, '2025-02-02', 'new two', 'Assigned', '2025-01-27 23:04:31'),
(47, 4, '2025-02-02', 'rush hour', 'Assigned', '2025-01-28 00:23:24'),
(48, 2, '2025-01-01', 'paramore', 'Pending', '2025-01-28 01:56:47'),
(49, 2, '2025-01-01', 'jhddo', 'Pending', '2025-01-28 02:17:04');

-- --------------------------------------------------------

--
-- Table structure for table `order_item`
--

CREATE TABLE `order_item` (
  `item_id` int(11) NOT NULL,
  `ordIt_ord_id` int(11) NOT NULL,
  `ordIt_prd_id` int(11) NOT NULL,
  `ordIt_prd_name` varchar(50) NOT NULL,
  `quantity_kg` int(11) NOT NULL,
  `total_price` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `order_item`
--

INSERT INTO `order_item` (`item_id`, `ordIt_ord_id`, `ordIt_prd_id`, `ordIt_prd_name`, `quantity_kg`, `total_price`) VALUES
(26, 10, 8, 'Grapes', 10, 32),
(27, 10, 1, 'Milk', 15, 18),
(28, 10, 2, 'Cheese', 5, 42.5),
(29, 11, 12, 'Cola', 9, 13.5),
(30, 11, 10, 'Mangoes', 10, 45),
(31, 11, 16, 'Pasta', 8, 20),
(32, 12, 7, 'Bananas', 3, 5.4),
(33, 12, 12, 'Cola', 4, 6),
(34, 12, 2, 'Cheese', 5, 42.5),
(35, 13, 9, 'Oranges', 3, 6),
(36, 13, 19, 'Instant Noodles', 4, 12),
(37, 13, 2, 'Cheese', 5, 42.5),
(38, 14, 7, 'Bananas', 3, 5.4),
(39, 14, 10, 'Mangoes', 4, 18),
(40, 14, 2, 'Cheese', 5, 42.5),
(41, 15, 11, 'Orange Juice', 7, 21),
(42, 15, 4, 'Butter', 8, 48),
(43, 15, 20, 'Cereal', 3, 18),
(44, 16, 4, 'Butter', 4, 24),
(45, 16, 12, 'Cola', 5, 7.5),
(46, 16, 5, 'Cream', 6, 30),
(47, 17, 6, 'Apples', 2, 5),
(48, 17, 5, 'Cream', 3, 15),
(49, 17, 2, 'Cheese', 1, 4.25),
(50, 18, 15, 'Lemonade', 2, 4),
(51, 18, 7, 'Bananas', 3, 5.4),
(52, 18, 11, 'Orange Juice', 4, 12),
(53, 19, 13, 'Green Tea', 4, 40),
(54, 19, 2, 'Cheese', 5, 42.5),
(55, 19, 20, 'Cereal', 6, 36),
(57, 23, 4, 'Butter', 1, 6),
(58, 23, 5, 'Cream', 1, 5),
(59, 23, 2, 'Cheese', 1, 8.5),
(60, 23, 1, 'Milk', 1, 1.2),
(61, 23, 5, 'Cream', 1, 5),
(62, 23, 6, 'Apples', 1, 2.5),
(63, 23, 2, 'Cheese', 1, 8.5),
(64, 23, 5, 'Cream', 1, 5),
(65, 23, 3, 'Yogurt', 1, 3.5),
(66, 23, 6, 'Apples', 1, 2.5),
(67, 23, 5, 'Cream', 1, 5),
(68, 23, 6, 'Apples', 1, 2.5),
(69, 23, 2, 'Cheese', 1, 8.5),
(70, 23, 1, 'Milk', 1, 1.2),
(71, 23, 2, 'Cheese', 1, 8.5),
(72, 23, 1, 'Milk', 1, 1.2),
(73, 23, 1, 'Milk', 1, 1.2),
(74, 23, 1, 'Milk', 1, 1.2),
(75, 24, 4, 'Butter', 1, 6),
(76, 24, 5, 'Cream', 1, 5),
(77, 24, 2, 'Cheese', 1, 8.5),
(78, 24, 1, 'Milk', 1, 1.2),
(79, 24, 5, 'Cream', 1, 5),
(80, 24, 6, 'Apples', 1, 2.5),
(81, 24, 2, 'Cheese', 1, 8.5),
(82, 24, 5, 'Cream', 1, 5),
(83, 24, 3, 'Yogurt', 1, 3.5),
(84, 24, 6, 'Apples', 1, 2.5),
(85, 24, 5, 'Cream', 1, 5),
(86, 24, 6, 'Apples', 1, 2.5),
(87, 24, 2, 'Cheese', 1, 8.5),
(88, 24, 1, 'Milk', 1, 1.2),
(89, 24, 2, 'Cheese', 1, 8.5),
(90, 24, 1, 'Milk', 1, 1.2),
(91, 24, 1, 'Milk', 1, 1.2),
(92, 24, 1, 'Milk', 1, 1.2),
(93, 24, 9, 'Oranges', 1, 2),
(94, 24, 8, 'Grapes', 1, 3.2),
(95, 24, 12, 'Cola', 1, 1.5),
(96, 24, 4, 'Butter', 1, 6),
(97, 24, 3, 'Yogurt', 1, 3.5),
(98, 25, 5, 'Cream', 1, 5),
(99, 25, 5, 'Cream', 1, 5),
(100, 26, 2, 'Cheese', 1, 8.5),
(101, 26, 6, 'Apples', 1, 2.5),
(102, 26, 10, 'Mangoes', 1, 4.5),
(103, 27, 8, 'Grapes', 1, 3.2),
(104, 27, 7, 'Bananas', 1, 1.8),
(105, 27, 6, 'Apples', 1, 2.5),
(106, 28, 6, 'Apples', 1, 2.5),
(107, 28, 5, 'Cream', 1, 5),
(108, 28, 7, 'Bananas', 1, 1.8),
(109, 29, 8, 'Grapes', 1, 3.2),
(110, 29, 7, 'Bananas', 1, 1.8),
(111, 29, 9, 'Oranges', 1, 2),
(112, 30, 11, 'Orange Juice', 1, 3),
(113, 30, 12, 'Cola', 1, 1.5),
(114, 31, 7, 'Bananas', 1, 1.8),
(115, 31, 4, 'Butter', 1, 6),
(116, 32, 5, 'Cream', 1, 5),
(117, 33, 7, 'Bananas', 1, 1.8),
(118, 34, 8, 'Grapes', 1, 3.2),
(119, 35, 7, 'Bananas', 1, 1.8),
(120, 36, 4, 'Butter', 2, 15),
(121, 37, 4, 'Butter', 2, 12),
(122, 37, 8, 'Grapes', 2, 8),
(123, 38, 1, 'Milk', 2, 2.4),
(124, 38, 2, 'Cheese', 2, 17),
(125, 39, 6, 'Apples', 2, 5),
(126, 39, 7, 'Bananas', 2, 3.6),
(127, 39, 8, 'Grapes', 3, 9.6),
(128, 40, 4, 'Butter', 1, 9),
(129, 41, 9, 'Oranges', 3, 6),
(130, 42, 3, 'Yogurt', 2, 7),
(131, 42, 5, 'Cream', 2, 10),
(132, 43, 2, 'Cheese', 1, 8.5),
(133, 43, 4, 'Butter', 3, 18),
(134, 43, 6, 'Apples', 2, 6.25),
(135, 44, 2, 'Cheese', 2, 17),
(136, 44, 6, 'Apples', 2, 6.25),
(137, 45, 3, 'Yogurt', 3, 10.5),
(138, 45, 14, 'Coffee Beans', 3, 45),
(139, 46, 2, 'Cheese', 2, 17),
(140, 46, 4, 'Butter', 2, 15),
(141, 47, 1, 'Milk', 3, 3.6),
(142, 47, 9, 'Oranges', 3, 6),
(143, 47, 7, 'Bananas', 2, 4.5),
(144, 48, 1, 'Milk', 2, 3),
(145, 49, 2, 'Cheese', 2, 17);

-- --------------------------------------------------------

--
-- Table structure for table `product`
--

CREATE TABLE `product` (
  `product_id` int(11) NOT NULL,
  `product_name` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `price_per_kg` float(10,2) NOT NULL,
  `stock_quantity` int(11) NOT NULL DEFAULT 0,
  `image_url` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `category` varchar(50) DEFAULT 'General'
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `product`
--

INSERT INTO `product` (`product_id`, `product_name`, `description`, `price_per_kg`, `stock_quantity`, `image_url`, `created_at`, `category`) VALUES
(1, 'Milk', 'Fresh whole milk', 1.20, 482, 'fresh-whole-milk.jpg', '2025-01-18 03:59:25', 'General'),
(2, 'Cheese', 'Cheddar cheese block', 8.50, 173, 'chedder-cheese.jpg', '2025-01-18 03:59:25', 'General'),
(3, 'Yogurt', 'Low-fat plain yogurt', 3.50, 144, 'yoghurt.jpg', '2025-01-18 03:59:25', 'General'),
(4, 'Butter', 'Salted butter', 6.00, 281, 'salted-butter.jpg', '2025-01-18 03:59:25', 'General'),
(5, 'Cream', 'Heavy cream for cooking', 5.00, 104, 'cream.jpg', '2025-01-18 03:59:25', 'General'),
(6, 'Apples', 'Fresh red apples', 2.50, 596, 'apples.jpg', '2025-01-18 03:59:25', 'General'),
(7, 'Bananas', 'Organic bananas', 1.80, 389, 'bananas.jpg', '2025-01-18 03:59:25', 'General'),
(8, 'Grapes', 'Seedless green grapes', 3.20, 340, 'grapes.jpg', '2025-01-18 03:59:25', 'General'),
(9, 'Oranges', 'Juicy navel oranges', 2.00, 494, 'oranges.jpg', '2025-01-18 03:59:25', 'General'),
(10, 'Mangoes', 'Ripe Alphonso mangoes', 4.50, 84, 'mangoes.jpg', '2025-01-18 03:59:25', 'General'),
(11, 'Orange Juice', 'Freshly squeezed orange juice', 3.00, 239, 'orange-juice.jpg', '2025-01-18 03:59:25', 'General'),
(12, 'Cola', 'Carbonated soft drink', 1.50, 671, 'cola.jfif', '2025-01-18 03:59:25', 'General'),
(13, 'Green Tea', 'Organic green tea leaves', 10.00, 43, 'green-tea.jpg', '2025-01-18 03:59:25', 'General'),
(14, 'Coffee Beans', 'Premium roasted coffee beans', 15.00, 98, 'coffee-beans.jpg', '2025-01-18 03:59:25', 'General'),
(15, 'Lemonade', 'Refreshing lemon-flavored drink', 2.00, 395, 'lemonade.jpg', '2025-01-18 03:59:25', 'General'),
(16, 'Pasta', 'Durum wheat pasta', 2.50, 289, 'pasta.jpg', '2025-01-18 03:59:25', 'General'),
(17, 'Biscuits', 'Chocolate chip cookies', 5.00, 440, 'foxies.jpg', '2025-01-18 03:59:25', 'General'),
(18, 'Rice', 'Basmati long-grain rice', 1.20, 794, 'rice.jpg', '2025-01-18 03:59:25', 'General'),
(19, 'Instant Noodles', 'Spicy chicken-flavored noodles', 3.00, 592, 'noodles.jpg', '2025-01-18 03:59:25', 'General'),
(20, 'Cereal', 'Whole grain breakfast cereal', 6.00, 226, 'cereal.jpg', '2025-01-18 03:59:25', 'General');

-- --------------------------------------------------------

--
-- Table structure for table `product_backup`
--

CREATE TABLE `product_backup` (
  `product_id` int(11) NOT NULL DEFAULT 0,
  `product_name` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `price_per_kg` float(10,2) NOT NULL,
  `stock_quantity` int(11) NOT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `product_backup`
--

INSERT INTO `product_backup` (`product_id`, `product_name`, `description`, `price_per_kg`, `stock_quantity`, `image_url`, `created_at`) VALUES
(1, 'Milk', 'Fresh whole milk', 1.20, 482, 'fresh-whole-milk.jpg', '2025-01-18 03:59:25'),
(2, 'Cheese', 'Cheddar cheese block', 8.50, 173, 'chedder-cheese.jpg', '2025-01-18 03:59:25'),
(3, 'Yogurt', 'Low-fat plain yogurt', 3.50, 144, 'yoghurt.jpg', '2025-01-18 03:59:25'),
(4, 'Butter', 'Salted butter', 6.00, 281, 'salted-butter.jpg', '2025-01-18 03:59:25'),
(5, 'Cream', 'Heavy cream for cooking', 5.00, 104, 'cream.jpg', '2025-01-18 03:59:25'),
(6, 'Apples', 'Fresh red apples', 2.50, 596, 'apples.jpg', '2025-01-18 03:59:25'),
(7, 'Bananas', 'Organic bananas', 1.80, 389, 'bananas.jpg', '2025-01-18 03:59:25'),
(8, 'Grapes', 'Seedless green grapes', 3.20, 340, 'grapes.jpg', '2025-01-18 03:59:25'),
(9, 'Oranges', 'Juicy navel oranges', 2.00, 494, 'oranges.jpg', '2025-01-18 03:59:25'),
(10, 'Mangoes', 'Ripe Alphonso mangoes', 4.50, 84, 'mangoes.jpg', '2025-01-18 03:59:25'),
(11, 'Orange Juice', 'Freshly squeezed orange juice', 3.00, 239, 'orange-juice.jpg', '2025-01-18 03:59:25'),
(12, 'Cola', 'Carbonated soft drink', 1.50, 671, 'cola.jfif', '2025-01-18 03:59:25'),
(13, 'Green Tea', 'Organic green tea leaves', 10.00, 43, 'green-tea.jpg', '2025-01-18 03:59:25'),
(14, 'Coffee Beans', 'Premium roasted coffee beans', 15.00, 98, 'coffee-beans.jpg', '2025-01-18 03:59:25'),
(15, 'Lemonade', 'Refreshing lemon-flavored drink', 2.00, 395, 'lemonade.jpg', '2025-01-18 03:59:25'),
(16, 'Pasta', 'Durum wheat pasta', 2.50, 289, 'pasta.jpg', '2025-01-18 03:59:25'),
(17, 'Biscuits', 'Chocolate chip cookies', 5.00, 440, 'foxies.jpg', '2025-01-18 03:59:25'),
(18, 'Rice', 'Basmati long-grain rice', 1.20, 794, 'rice.jpg', '2025-01-18 03:59:25'),
(19, 'Instant Noodles', 'Spicy chicken-flavored noodles', 3.00, 592, 'noodles.jpg', '2025-01-18 03:59:25'),
(20, 'Cereal', 'Whole grain breakfast cereal', 6.00, 226, 'cereal.jpg', '2025-01-18 03:59:25');

-- --------------------------------------------------------

--
-- Table structure for table `scheduler`
--

CREATE TABLE `scheduler` (
  `scheduler_id` int(11) NOT NULL,
  `sch_user_id` int(11) NOT NULL,
  `sch_full_name` varchar(255) NOT NULL,
  `sch_email` varchar(255) NOT NULL,
  `sch_status` varchar(20) NOT NULL DEFAULT 'ACTIVE',
  `sch_created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `scheduler`
--

INSERT INTO `scheduler` (`scheduler_id`, `sch_user_id`, `sch_full_name`, `sch_email`, `sch_status`, `sch_created_at`) VALUES
(1, 9, 'tester7', 'h.h@h.com', 'ACTIVE', '2025-01-17 23:54:17'),
(2, 11, 'tester006', 'k.k@k.com', 'ACTIVE', '2025-01-20 02:41:38'),
(3, 23, 'test2', 'ez@ez.com', 'ACTIVE', '2025-01-27 02:32:25'),
(4, 23, 'test2', 'ez@ez.com', 'ACTIVE', '2025-01-27 02:32:28'),
(5, 29, 'test5', 'ra@ra.com', 'ACTIVE', '2025-01-28 00:57:08'),
(6, 35, 'ral1', 'ral@ral.com', 'ACTIVE', '2025-01-28 01:43:39');

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `user_id` int(11) NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `user_email` varchar(255) NOT NULL,
  `user_password` varchar(255) NOT NULL,
  `phone_no` varchar(15) NOT NULL,
  `role` enum('driver','scheduler','customer') NOT NULL DEFAULT 'customer',
  `user_created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`user_id`, `user_name`, `user_email`, `user_password`, `phone_no`, `role`, `user_created_at`) VALUES
(6, 'tester4', 'e.e@e.com', 'eeeeeeee', '1723949', 'driver', '2025-01-17 16:57:10'),
(7, 'tester5', 'f.f@f.com', 'ffffffff', '283949', 'customer', '2025-01-17 23:22:44'),
(8, 'tester6', 'g.g@g.com', 'gggggggg', '2536378', 'scheduler', '2025-01-17 23:41:20'),
(9, 'tester7', 'h.h@h.com', 'hhhhhhhh', '3829303', 'scheduler', '2025-01-17 23:54:17'),
(10, 'tester8', 'j.j@j.com', 'Ll7ESZCmw2fqsixTtFcfbXX3hUe6lmVM/ZC8HMA/Bkc=', '2840294', 'customer', '2025-01-18 15:16:48'),
(11, 'tester006', 'k.k@k.com', '+gQi1QJez6lrn1iiItx0IvK9+hhOCICVTihWOXTjk6Y=', '1837915787', 'scheduler', '2025-01-20 02:41:38'),
(12, 'tester11', 'l.l@l.com', 'NMk4kqz2S/+YTTABa4SR35KinAVKbxhw9/JrjyCs4mI=', '2480924', 'driver', '2025-01-22 00:19:25'),
(13, 'tester12', 'm.m@m.com', 'TGeqCG0rLJ3ry46iVx0A+dTwCHNQiCjp+AiQedgK+LI=', '83748285', 'customer', '2025-01-22 00:40:46'),
(14, 'tester13', 'ja.ja@ja.com', 'Ll7ESZCmw2fqsixTtFcfbXX3hUe6lmVM/ZC8HMA/Bkc=', '4713058', 'customer', '2025-01-22 00:45:41'),
(15, 'tester14', 'la.la@la.com', 'NMk4kqz2S/+YTTABa4SR35KinAVKbxhw9/JrjyCs4mI=', '832303', 'driver', '2025-01-26 18:25:12'),
(16, 'tester14', 'la.la@la.com', 'NMk4kqz2S/+YTTABa4SR35KinAVKbxhw9/JrjyCs4mI=', '832303', 'driver', '2025-01-26 18:25:14'),
(19, 'test100', 't.t@t.com', 'Ugjc6N4P7Yb5YRnoJZ5/mbn92mB4ENs8VONqpqwWNGk=', '21828394008', 'scheduler', '2025-01-27 02:15:08'),
(20, 'test100', 't.t@t.com', 'Ugjc6N4P7Yb5YRnoJZ5/mbn92mB4ENs8VONqpqwWNGk=', '21828394008', 'scheduler', '2025-01-27 02:15:10'),
(23, 'test2', 'ez@ez.com', 'nFKNvMWJVmyvxPAESxCgHlJAB/A1GG7hGl0aPmc3y30=', '172893930', 'scheduler', '2025-01-27 02:32:25'),
(24, 'test2', 'ez@ez.com', 'nFKNvMWJVmyvxPAESxCgHlJAB/A1GG7hGl0aPmc3y30=', '172893930', 'scheduler', '2025-01-27 02:32:28'),
(25, 'test3 ', 'ze@ze.com', 'wSnbi+iQS0CsIcnPXZ9cDiTvRV0denu/1wSfxtydJCk=', '27844874', 'customer', '2025-01-27 02:59:37'),
(28, 'Driver003', 'de@de.com', 'sykUzWIAh/pQZFu7qCaPw7ydkq60J7xrmFR3ubSmWDA=', '8383939', 'driver', '2025-01-28 00:54:59'),
(29, 'test5', 'ra@ra.com', 'QHFXJhLQHAH4m2G9BMiJqzVbYHHQLWxClpVRz/UYchE=', '8283939', 'scheduler', '2025-01-28 00:57:08'),
(30, 'test7', 'cu@cu.com', 'n6EPyHnRfCcRhyyd2y2bS8eStfMm0OzrlPgeYpyvEiI=', '7283939', 'customer', '2025-01-28 00:57:54'),
(31, 'driver09', 'day@day.com', 'sykUzWIAh/pQZFu7qCaPw7ydkq60J7xrmFR3ubSmWDA=', '829303', 'driver', '2025-01-28 01:15:09'),
(32, 'dark', 'dar@dar.com', 'sykUzWIAh/pQZFu7qCaPw7ydkq60J7xrmFR3ubSmWDA=', '2783939', 'customer', '2025-01-28 01:29:14'),
(33, 'customer002', 'cus@cus.com', 'n6EPyHnRfCcRhyyd2y2bS8eStfMm0OzrlPgeYpyvEiI=', '833903', 'customer', '2025-01-28 01:32:18'),
(34, 'light', 'li@li.com', 'NMk4kqz2S/+YTTABa4SR35KinAVKbxhw9/JrjyCs4mI=', '883939', 'driver', '2025-01-28 01:42:05'),
(35, 'ral1', 'ral@ral.com', 'QHFXJhLQHAH4m2G9BMiJqzVbYHHQLWxClpVRz/UYchE=', '73839393', 'scheduler', '2025-01-28 01:43:39');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `cart`
--
ALTER TABLE `cart`
  ADD PRIMARY KEY (`cart_id`),
  ADD KEY `cart_cst_id` (`cart_cst_id`),
  ADD KEY `cart_prd_id` (`cart_prd_id`);

--
-- Indexes for table `customer`
--
ALTER TABLE `customer`
  ADD PRIMARY KEY (`customer_id`),
  ADD KEY `cst_user_id` (`cst_user_id`);

--
-- Indexes for table `driver`
--
ALTER TABLE `driver`
  ADD PRIMARY KEY (`driver_id`),
  ADD KEY `dr_user_id` (`dr_user_id`);

--
-- Indexes for table `mission`
--
ALTER TABLE `mission`
  ADD PRIMARY KEY (`mission_id`),
  ADD KEY `msn_dr_id` (`msn_dr_id`),
  ADD KEY `msn_sch_id` (`msn_sch_id`),
  ADD KEY `idx_mission_date` (`mission_date`),
  ADD KEY `idx_mission_status` (`mission_status`);

--
-- Indexes for table `mission_order`
--
ALTER TABLE `mission_order`
  ADD PRIMARY KEY (`mord_mission_id`,`mord_order_id`),
  ADD KEY `mord_order_id` (`mord_order_id`);

--
-- Indexes for table `order`
--
ALTER TABLE `order`
  ADD PRIMARY KEY (`order_id`),
  ADD KEY `order_cst_id` (`order_cst_id`);

--
-- Indexes for table `order_item`
--
ALTER TABLE `order_item`
  ADD PRIMARY KEY (`item_id`),
  ADD KEY `ordIt_ord_id` (`ordIt_ord_id`),
  ADD KEY `ordIt_prd_id` (`ordIt_prd_id`);

--
-- Indexes for table `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`product_id`),
  ADD KEY `idx_product_category` (`category`),
  ADD KEY `idx_product_name` (`product_name`);

--
-- Indexes for table `scheduler`
--
ALTER TABLE `scheduler`
  ADD PRIMARY KEY (`scheduler_id`),
  ADD KEY `sch_user_id` (`sch_user_id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `cart`
--
ALTER TABLE `cart`
  MODIFY `cart_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=164;

--
-- AUTO_INCREMENT for table `customer`
--
ALTER TABLE `customer`
  MODIFY `customer_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `driver`
--
ALTER TABLE `driver`
  MODIFY `driver_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `mission`
--
ALTER TABLE `mission`
  MODIFY `mission_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=28;

--
-- AUTO_INCREMENT for table `order`
--
ALTER TABLE `order`
  MODIFY `order_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=50;

--
-- AUTO_INCREMENT for table `order_item`
--
ALTER TABLE `order_item`
  MODIFY `item_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=146;

--
-- AUTO_INCREMENT for table `product`
--
ALTER TABLE `product`
  MODIFY `product_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT for table `scheduler`
--
ALTER TABLE `scheduler`
  MODIFY `scheduler_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=36;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `cart`
--
ALTER TABLE `cart`
  ADD CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`cart_cst_id`) REFERENCES `customer` (`customer_id`),
  ADD CONSTRAINT `cart_ibfk_2` FOREIGN KEY (`cart_prd_id`) REFERENCES `product` (`product_id`);

--
-- Constraints for table `customer`
--
ALTER TABLE `customer`
  ADD CONSTRAINT `customer_ibfk_1` FOREIGN KEY (`cst_user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `driver`
--
ALTER TABLE `driver`
  ADD CONSTRAINT `driver_ibfk_1` FOREIGN KEY (`dr_user_id`) REFERENCES `user` (`user_id`);

--
-- Constraints for table `mission`
--
ALTER TABLE `mission`
  ADD CONSTRAINT `mission_ibfk_1` FOREIGN KEY (`msn_dr_id`) REFERENCES `driver` (`driver_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `mission_ibfk_2` FOREIGN KEY (`msn_sch_id`) REFERENCES `scheduler` (`scheduler_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `mission_order`
--
ALTER TABLE `mission_order`
  ADD CONSTRAINT `mission_order_ibfk_1` FOREIGN KEY (`mord_mission_id`) REFERENCES `mission` (`mission_id`),
  ADD CONSTRAINT `mission_order_ibfk_2` FOREIGN KEY (`mord_order_id`) REFERENCES `order` (`order_id`);

--
-- Constraints for table `order`
--
ALTER TABLE `order`
  ADD CONSTRAINT `order_ibfk_1` FOREIGN KEY (`order_cst_id`) REFERENCES `customer` (`customer_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `order_item`
--
ALTER TABLE `order_item`
  ADD CONSTRAINT `order_item_ibfk_1` FOREIGN KEY (`ordIt_ord_id`) REFERENCES `order` (`order_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `order_item_ibfk_2` FOREIGN KEY (`ordIt_prd_id`) REFERENCES `product` (`product_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `scheduler`
--
ALTER TABLE `scheduler`
  ADD CONSTRAINT `scheduler_ibfk_1` FOREIGN KEY (`sch_user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
