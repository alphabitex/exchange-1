SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for exchange_user_wallet
-- ----------------------------
CREATE TABLE exchange_user_wallet
(
  id bigint(20) PRIMARY KEY NOT NULL COMMENT '主键ID' AUTO_INCREMENT,
  user_id int(11) NOT NULL COMMENT '用户ID',
  address varchar(80) DEFAULT '' NOT NULL COMMENT '区块链地址',
  private_key varchar(500) DEFAULT '' NOT NULL COMMENT '加密私钥',
  nonce decimal(30) DEFAULT '0' COMMENT 'nonce值',
  balance decimal(20,10) DEFAULT '0' NOT NULL COMMENT '资产余额',
  coin_name varchar(20) DEFAULT '' NOT NULL COMMENT '货币名称',
  coin_type varchar(20) DEFAULT '' NOT NULL COMMENT '货币类型'
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户钱包';


-- ----------------------------
-- Table structure for exchange_scan_record
-- ----------------------------
CREATE TABLE exchange_scan_record
(
  coin_name varchar(20) DEFAULT '' PRIMARY KEY NOT NULL COMMENT '货币类型',
  height int(11) DEFAULT 0 NOT NULL COMMENT '扫描高度',
  update_time datetime not null COMMENT '更新时间'
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='区块链扫块记录';


-- ----------------------------
-- Table structure for exchange_wallet_group
-- ----------------------------
DROP TABLE IF EXISTS `exchange_wallet_group`;
CREATE TABLE `exchange_wallet_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_type` int(11) DEFAULT '0' COMMENT '钱包类型  1：邮费钱包组   2：充值钱包组  3：提现钱包组',
  `active` int (11) default '1' COMMENT '账号是否禁用 1:激活状态  0：禁用状态',
  `address` varchar(100) DEFAULT '' COMMENT '钱包地址',
  `coin_name` varchar(255) DEFAULT '' COMMENT '货币名称:BTC,ETH',
  `coin_type` varchar(255) DEFAULT '' COMMENT '货币类型:BTC,ETH',
  `balance` decimal(20,10) DEFAULT '0' COMMENT '货币余额',
  `main_balance` decimal(20,10) DEFAULT '0' COMMENT '主币余额',
  `nonce` decimal(65,0) DEFAULT '0',
  `private_key` varchar(800) DEFAULT '' COMMENT '私钥',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  `updated_by` int(11) DEFAULT '0' COMMENT '更新人',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  `created_by` int(11) DEFAULT '0' COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='钱包组信息';


-- ----------------------------
-- Table structure for exchange_wallet_transaction_order
-- ----------------------------
drop table if exists `exchange_trs_order`;
CREATE TABLE `exchange_trs_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `hash` varchar(100) NOT NULL DEFAULT '' COMMENT '交易id',
  `user_id` int(20) DEFAULT '0' COMMENT '用户ID',
  `transaction_type` int(11) DEFAULT '0' COMMENT '订单类型:1-用户充值,2-充值gas,3-充值总账,4-用户体现',
  `coin_type` varchar(20) NOT NULL COMMENT '钱包类型：eth，btc',
  `coin_name` varchar(20)  DEFAULT '' COMMENT '代币名称',
  `status` int(20) DEFAULT '0' COMMENT '订单状态：1-pending,2-success,3-失败',
  `from_address` varchar(100) CHARACTER SET utf8mb4 DEFAULT '' COMMENT '汇币地址',
  `to_address` varchar(100) CHARACTER SET utf8mb4 DEFAULT '' COMMENT '收币地址',
  `amount` decimal(20,10) DEFAULT '0' COMMENT '代币数量',
  `fee` decimal(20,10) DEFAULT '0' COMMENT '旷工费',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='钱包组交易记录';


-- ----------------------------
-- Table structure for exchange_mock_coin
-- ----------------------------
create table exchange_mock_coin
(
  coin_name          varchar(20) default '' not null primary key,
  fee                decimal(20,10) default '0',
  contract_address    varchar(100) default '',
  coin_type    varchar(20) default '',
  unit          int default 0,
  min_collect_amount decimal(20,10) default '0'
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='模拟货币表';
