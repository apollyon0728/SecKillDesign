use seckill;

DROP TABLE IF EXISTS user;
CREATE TABLE user(
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY comment "ID",
    username VARCHAR (20) UNIQUE NOT NULL comment "用户名",
    phone VARCHAR (20) NOT NULL comment "手机号码",
    createTime DATE NOT NULL comment "创建时间"
);

DROP TABLE IF EXISTS product;
CREATE TABLE `product` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `productName` varchar(20) NOT NULL COMMENT '产品名称',
  `price` decimal(16,3) NOT NULL COMMENT '价格',
  `stock` int(11) NOT NULL COMMENT '库存',
  `createTime` date NOT NULL COMMENT '创建时间',
  `version` int(12) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `productName` (`productName`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS record;
CREATE TABLE record(
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY comment "ID",
    userId INT NOT NULL comment "用户ID",
    productId INT NOT NULL comment "产品ID",
    state VARCHAR (6) NOT NULL comment "秒杀状态1秒杀成功,0秒杀失败,-1重复秒杀,-2系统异常",
    stateInfo VARCHAR (6) NOT NULL comment "状态的明文标识",
    createTime DATE NOT NULL comment "创建时间"
);

-- use seckill;
-- update product set stock=100 where id>=1;
-- delete from record where id>=0

-- update product set stock=100 and version=0 where id>=1 ;