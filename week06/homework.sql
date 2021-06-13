DROP TABLE IF EXISTS `members_list`;
CREATE TABLE `user_list` (
  `member_id` varchar(10) NOT NULL COMMENT '会员编号',
  `member_name` varchar(10) NOT NULL COMMENT '会员姓名',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `create_by` varchar(50) NOT NULL COMMENT '创建人',
  `last_modified_by` varchar(50) NOT NULL COMMENT '最后更新人',
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日期',
  `is_deleted` bit(1) NOT NULL COMMENT '删除标识',
  PRIMARY KEY (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户列表';

DROP TABLE IF EXISTS `md_commodity`;
CREATE TABLE `md_commodity` (
  `store_code` varchar(8) COLLATE utf8_unicode_ci NOT NULL COMMENT '门店编码',
  `cmmdty_code` varchar(18) COLLATE utf8_unicode_ci NOT NULL COMMENT '商品编码',
  `external_ean_code` varchar(18) COLLATE utf8_unicode_ci NOT NULL COMMENT '商品条码',
  `cmmdty_name` varchar(200) COLLATE utf8_unicode_ci NOT NULL COMMENT '商品名称',
  `measure_unit` char(3) COLLATE utf8_unicode_ci NOT NULL COMMENT '计量单位',
  `cmmdty_group` varchar(9) COLLATE utf8_unicode_ci NOT NULL COMMENT '商品组',
  `cmmdty_categ` char(4) COLLATE utf8_unicode_ci NOT NULL COMMENT '商品目录',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `create_by` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '创建人',
  `last_modified_by` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '最后更新人',
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新日期',
  `is_deleted` bit(1) NOT NULL COMMENT '删除标识',
  PRIMARY KEY (`store_code`,`cmmdty_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='常规商品信息';

DROP TABLE IF EXISTS `md_commodity_price`;
CREATE TABLE `md_commodity_price` (
  `store_code` varchar(8) COLLATE utf8_unicode_ci NOT NULL COMMENT '门店编码',
  `cmmdty_code` varchar(18) COLLATE utf8_unicode_ci NOT NULL COMMENT '商品编码',
  `cmmdty_price` decimal(14,4) NOT NULL COMMENT '商品价格',
  `start_date` char(8) COLLATE utf8_unicode_ci NOT NULL COMMENT '开始日期',
  `start_time` char(6) COLLATE utf8_unicode_ci NOT NULL COMMENT '开始时间',
  `end_date` char(8) COLLATE utf8_unicode_ci NOT NULL COMMENT '结束日期',
  `end_time` char(6) COLLATE utf8_unicode_ci NOT NULL COMMENT '结束时间',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `create_by` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '创建人',
  `last_modified_by` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '最后更新人',
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `is_deleted` bit(1) NOT NULL COMMENT '删除标识',
  PRIMARY KEY (`store_code`,`cmmdty_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='商品价格';

DROP TABLE IF EXISTS `order_head`;
CREATE TABLE `order_head` (
  `order_id` varchar(18) COLLATE utf8_unicode_ci NOT NULL COMMENT '订单编码',
  `store_code` varchar(6) COLLATE utf8_unicode_ci NOT NULL COMMENT '门店编码',
  `member_id` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT '会员ID',
  `member_code` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT '会员编码',
  `member_name` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT '会员姓名',
  `member_type` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT '会员类型',
  `sale_time` datetime NOT NULL COMMENT '销售时间',
  `pay_time` datetime NOT NULL COMMENT '支付时间',
  `transaction_value` decimal(14,4) NOT NULL COMMENT '交易金额',
  `discount` decimal(14,4) NOT NULL COMMENT '折扣',
  `pay_value_real` decimal(14,4) NOT NULL COMMENT '实收',
  `order_type` int(5) NOT NULL COMMENT '订单类型',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `create_by` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '创建人',
  `last_modified_by` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '最后更新人',
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `is_deleted` bit(1) NOT NULL COMMENT '删除标识',
  PRIMARY KEY (`order_id`,`store_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='订单头信息';

DROP TABLE IF EXISTS `order_detail`;
CREATE TABLE `order_detail` (
  `store_code` varchar(6) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '门店编码',
  `order_id` varchar(18) COLLATE utf8_unicode_ci NOT NULL COMMENT '订单号',
  `line_id` int(11) NOT NULL DEFAULT '0' COMMENT '行号',
  `cmmdty_code` varchar(18) COLLATE utf8_unicode_ci NOT NULL COMMENT '商品编码',
  `cmmdty_name` varchar(200) COLLATE utf8_unicode_ci NOT NULL COMMENT '商品名称',
  `external_ean_code` varchar(18) COLLATE utf8_unicode_ci NOT NULL COMMENT '商品条码',
  `normal_price` decimal(14,4) NOT NULL COMMENT '正常售价',
  `sale_price` decimal(14,4) NOT NULL COMMENT '销售价格',
  `quantity` decimal(14,4) NOT NULL COMMENT '数量',
  `sale_value` decimal(14,4) NOT NULL COMMENT '销售额',
  `discount` decimal(14,4) NOT NULL COMMENT '折扣',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `create_by` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '创建人',
  `last_modified_by` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '最后更新人',
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` bit(1) NOT NULL,
  PRIMARY KEY (`store_code`,`order_id`,`line_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='订单明细';

DROP TABLE IF EXISTS `order_pay`;
CREATE TABLE `order_pay` (
  `store_code` varchar(6) COLLATE utf8_unicode_ci NOT NULL COMMENT '门店编码',
  `order_id` varchar(18) COLLATE utf8_unicode_ci NOT NULL COMMENT '订单号',
  `line_id` int(11) NOT NULL COMMENT '支付行id',
  `pay_time` datetime NOT NULL COMMENT '支付时间',
  `pay_value` decimal(14,4) NOT NULL COMMENT '支付金额',
  `currency_code` varchar(5) COLLATE utf8_unicode_ci NOT NULL COMMENT '货币编码',
  `exchange_rate` decimal(14,4) NOT NULL COMMENT '找零',
  `lcocal_currency` decimal(14,4) NOT NULL COMMENT '本地货币编码',
  `pay_type` varchar(10) COLLATE utf8_unicode_ci NOT NULL COMMENT '支付类型',
  `pay_type_name` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT '支付类型名称',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `create_by` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '创建人',
  `last_modified_by` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '最后更新人',
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `is_deleted` bit(1) NOT NULL COMMENT '删除标识',
  PRIMARY KEY (`store_code`,`order_id`,`line_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='订单支付信息';

