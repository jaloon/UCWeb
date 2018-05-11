INSERT INTO `departments`(id,name,parentId,code,remark) VALUES (2,'研发中心', 1, '1.1.', '');
INSERT INTO `departments`(id,name,parentId,code,remark) VALUES (3,'测试部', 1, '1.2.', '');
INSERT INTO `departments`(id,name,parentId,code,remark) VALUES (4,'项目管理部', 1, '1.3.', '');
INSERT INTO `departments`(id,name,parentId,code,remark) VALUES (5,'产品支持部', 1, '1.4.', '');
INSERT INTO `departments`(id,name,parentId,code,remark) VALUES (6,'人力资源部', 1, '1.5.', '');
INSERT INTO `departments`(id,name,parentId,code,remark) VALUES (7,'销售管理部', 1, '1.6.', '');
INSERT INTO `departments`(id,name,parentId,code,remark) VALUES (8,'解决方案部', 1, '1.7.', '');
INSERT INTO `departments`(id,name,parentId,code,remark) VALUES (9,'营销中心', 1, '1.8.', '');
INSERT INTO `departments`(id,name,parentId,code,remark) VALUES (10,'总经办', 1, '1.9.', '');

INSERT INTO `users`(deptId,roleId,account,name,password,email) VALUES (2,1,'zhangs', '张三', '1bbd886460827015e5d605ed44252251', '');
INSERT INTO `users`(deptId,roleId,account,name,password,email) VALUES (2,1,'lis', '李四', '1bbd886460827015e5d605ed44252251', '');
INSERT INTO `users`(deptId,roleId,account,name,password,email) VALUES (2,1,'wangw', '王五', '1bbd886460827015e5d605ed44252251', '');
INSERT INTO `users`(deptId,roleId,account,name,password,email) VALUES (2,1,'zhaol', '赵六', '1bbd886460827015e5d605ed44252251', '');
