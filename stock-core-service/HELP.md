# Database Create

create database stock;
use stock;
create table stock_basic(
id int primary key not null auto_increment,
name varchar(20) not null,
code varchar(10) not null,
market varchar(5) not null,
modified_time bigint(20) not null,
create_time bigint(20) not null,
features_version int not null
);

create table stock_quotation(
id int primary key not null auto_increment,
modified_time bigint(20) not null,
create_time bigint(20) not null,
quotation_time bigint(20) not null,
last_closing_price decimal(10,4) not null,
closing_price decimal(10,4) not null,
opening_price decimal(10,4)  not null,
highest_price decimal(10,4)  not null,
lowest_price decimal(10,4) not null,
change_amount decimal(10,4)  not null,
change_range decimal(10,4) not null,
turn_over_rate decimal(10,4) not null,
turn_over_volume decimal(10,4) not null,
turn_over_amount decimal(10,4) not null,
code varchar(10) not null
);

CREATE TABLE `stock_trade_order` (
`id` int NOT NULL AUTO_INCREMENT,
`modified_time` bigint NOT NULL,
`create_time` bigint NOT NULL,
`status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
`user_id` bigint NOT NULL,
`trade_order_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
`stock_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
`exchange_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
`entrust_price` decimal(10,4) NOT NULL,
`entrust_bs` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
`entrust_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
`exchange` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
`trade_when_close` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
`ice_berg_display_size` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
`strategy_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
`features` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
`entrust_count` int NOT NULL DEFAULT '0',
`deal_price` decimal(10,4) DEFAULT NULL,
`deal_count` int DEFAULT NULL,
`deal_date_time` bigint DEFAULT NULL,
`outer_trade_no` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
`strategy_id` bigint NOT NULL DEFAULT '0',
`re_create` varchar(20) COLLATE utf8mb4_bin DEFAULT 'UN_NEEDED',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5921 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

CREATE TABLE `stock_trade_strategy` (
`id` bigint NOT NULL AUTO_INCREMENT,
`features` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
`modified_time` bigint NOT NULL,
`create_time` bigint NOT NULL,
`status` int NOT NULL,
`user_id` bigint NOT NULL,
`stock_id` bigint NOT NULL,
`strategy_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
`start_time` bigint NOT NULL,
`end_time` bigint NOT NULL,
`strategy_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;


CREATE TABLE `stock_user` (
`id` bigint NOT NULL,
`create_time` bigint NOT NULL,
`modified_time` bigint NOT NULL,
`fund_account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
`mobile_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
`login_pwd` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
`trade_pwd` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
`features` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE `stock_user_fund` (
`id` bigint NOT NULL AUTO_INCREMENT,
`modified_time` bigint DEFAULT NULL,
`create_time` bigint DEFAULT NULL,
`user_id` bigint NOT NULL,
`available_balance` decimal(20,4) NOT NULL,
`asset_balance` decimal(20,4) NOT NULL,
`holding_balance` decimal(20,4) NOT NULL,
`exchange_type` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
`asset_date` bigint NOT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `idx_u_a_e` (`asset_date`,`exchange_type`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=185 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;
