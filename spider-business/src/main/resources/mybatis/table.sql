-- haohuo爬取的商品记录表
CREATE TABLE `t_haohuo_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `first_industry` varchar(30) DEFAULT NULL COMMENT '第一行业',
  `second_industry` varchar(30) DEFAULT NULL COMMENT '第二行业',
  `shop_name` varchar(100) DEFAULT NULL COMMENT '店铺名称',
  `product_id` varchar(100) DEFAULT NULL COMMENT '商品KEY',
  `name` varchar(100) DEFAULT NULL  COMMENT '商品名称',
  `sell_num` int(8) DEFAULT NULL COMMENT '销量',
  `sku_max_price` int(12) DEFAULT NULL  COMMENT '最高价格',
  `discount_price` int(12) DEFAULT NULL COMMENT '打折价格',
  `status` int(1) DEFAULT NULL  COMMENT '类型 0:不可用 1:可用',
  `product_link` varchar(256) DEFAULT NULL COMMENT '商品链接',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);


-- 好货爬取商品的定时销量表
CREATE TABLE `t_haohuo_product_sell` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_id` varchar(100) DEFAULT NULL COMMENT '商品KEY',
  `name` varchar(100) DEFAULT NULL  COMMENT '商品名称',
  `last_sell_num` int(8) DEFAULT NULL COMMENT '上一次爬取的销量',
  `add_sell_num` int(8) DEFAULT NULL COMMENT '销量增量',
  `total_sell_num` int(8) DEFAULT NULL COMMENT '总销量',
  `crawler_time` datetime NOT NULL COMMENT '爬取时间',
  `last_crawler_time` datetime NOT NULL COMMENT '上次爬取时间',
  PRIMARY KEY (`id`)
);

--爬虫任务表
CREATE TABLE `t_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_id` varchar(32) NOT NULL COMMENT '任务Id',
  `device_name` varchar(64) DEFAULT NULL COMMENT '设备名称',
  `root_url` varchar(100) DEFAULT NULL COMMENT '根url',
  `device_id` varchar(32) DEFAULT NULL COMMENT 'deviceId',
  `device_platform` varchar(24) DEFAULT NULL COMMENT '安卓/IOS',
  `device_type` varchar(24) DEFAULT NULL COMMENT '设备品牌',
  `device_brand` varchar(32) DEFAULT NULL COMMENT '型号',
  `params` text COMMENT '参数的json',
  `status` int(11) DEFAULT '0' COMMENT '任务执行状态 0:Init 1:Running  2:Stopped',
  `crawler_time` datetime DEFAULT NULL COMMENT '爬取开始时间',
  `last_crawler_time` datetime DEFAULT NULL COMMENT '上一次爬取时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `task_id_index` (`task_id`) COMMENT 'unique_task_id'
);