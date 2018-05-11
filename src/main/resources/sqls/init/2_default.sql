------------------------------------------初始化部门----------------------------------------------------
insert into Departments(id,name,parentId,code,remark)values(1,'根节点',0,'1.','');

------------------------------------------初始化角色----------------------------------------------------
INSERT INTO roles(id,name,Remark,isSuper) VALUES (1, '超级管理员', '拥有系统的所有权限', 1);
------------------------------------------初始化用户----------------------------------------------------
INSERT INTO `users`(deptId,roleId,account,name,password,email) 
	VALUES(1,1,'admin','Administrator','21232f297a57a5a743894a0e4a801fc3','');

