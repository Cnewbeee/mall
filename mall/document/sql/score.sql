DROP TABLE IF EXISTS `sms_score_mall_product`;
CREATE TABLE `sms_score_mall_product`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) NULL DEFAULT NULL,
  `product_name` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `recommend_status` int(1) NULL DEFAULT NULL,
  `sort` int(1) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '积分商城商品表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sms_score_mall_product
-- ----------------------------
INSERT INTO `sms_score_mall_product` VALUES (19, 37, 'Apple iPhone 14 (A2884) 128GB 支持移动联通电信5G 双卡双待手机', 1, 197);
INSERT INTO `sms_score_mall_product` VALUES (20, 38, 'Apple iPad 10.9英寸平板电脑 2022年款（64GB WLAN版/A14芯片/1200万像素/iPadOS MPQ03CH/A ）', 1, 0);
INSERT INTO `sms_score_mall_product` VALUES (21, 39, '小米 Xiaomi Book Pro 14 2022 锐龙版 2.8K超清大师屏 高端轻薄笔记本电脑(新R5-6600H标压 16G 512G win11)', 1, 198);
INSERT INTO `sms_score_mall_product` VALUES (22, 40, '小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充 12GB+256GB 黑色 5G手机', 1, 200);
INSERT INTO `sms_score_mall_product` VALUES (23, 41, 'Redmi K50 天玑8100 2K柔性直屏 OIS光学防抖 67W快充 5500mAh大电量 墨羽 12GB+256GB 5G智能手机 小米 红米', 1, 199);
INSERT INTO `sms_score_mall_product` VALUES (24, 42, 'HUAWEI Mate 50 直屏旗舰 超光变XMAGE影像 北斗卫星消息 低电量应急模式 128GB曜金黑华为鸿蒙手机', 1, 0);
INSERT INTO `sms_score_mall_product` VALUES (25, 44, '三星（SAMSUNG）500GB SSD固态硬盘 M.2接口(NVMe协议) 980（MZ-V8V500BW）', 1, 0);
INSERT INTO `sms_score_mall_product` VALUES (26, 45, 'OPPO Reno8 8GB+128GB 鸢尾紫 新配色上市 80W超级闪充 5000万水光人像三摄 3200万前置索尼镜头 5G手机', 1, 0);
INSERT INTO `sms_score_mall_product` VALUES (27, 43, '万和（Vanward)燃气热水器天然气家用四重防冻直流变频节能全新升级增压水伺服恒温高抗风 JSQ30-565W16【16升】【恒温旗舰款】', 1, 0);