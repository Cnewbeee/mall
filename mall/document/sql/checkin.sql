-- 会员签到记录表
DROP TABLE IF EXISTS `ums_member_checkin`;
CREATE TABLE `ums_member_checkin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `member_id` bigint(20) NOT NULL COMMENT '会员ID',
  `checkin_date` date NOT NULL COMMENT '签到日期',
  `integration` int(11) NOT NULL DEFAULT 10 COMMENT '签到获得积分',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_member_date` (`member_id`, `checkin_date`),
  KEY `idx_member_id` (`member_id`),
  KEY `idx_checkin_date` (`checkin_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员签到记录表';