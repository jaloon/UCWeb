-----------------------------------------------------------------------
--                             会话信息表(SESSIONS)                                                                                                 
-----------------------------------------------------------------------
DROP TABLE IF EXISTS `sessions`;
CREATE TABLE sessions (
	uuid                varchar(32)                                        comment 'sessionID',
	oldUuid             varchar(32)                                        comment '上一个sessionID',
	`user`              int(10)           NOT NULL                         comment '用户',
	ip                  varchar(15)       NOT NULL                         comment '登录用户IP',
	loginDate           datetime                                           comment '登录时间',
	operateDate         datetime                                           comment '操作时间',
	constraint pkSessions PRIMARY KEY (uuid)
)comment='会话信息表';
-----------------------------------------------------------------------
--                             系统配置对象表(CONFIGS)                                                                                                 
-----------------------------------------------------------------------
DROP TABLE IF EXISTS `configs`;
CREATE TABLE configs (
	id                  int(10)           auto_increment NOT NULL          comment '主键Id',
    code                varchar(100)      NOT NULL                         comment '配置编码',
    value               longtext      	  NOT NULL                         comment '配置值',
    remark              varchar(300)                                       comment '备注',
    constraint pkConfigs PRIMARY KEY (id)
)comment='系统配置';
-----------------------------------------------------------------------
--                            权限模块对象表(PERMISSIONS)                                                                                                 
-----------------------------------------------------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE permissions (
	id                  int(10)           auto_increment NOT NULL          comment '主键Id',
    cname               varchar(100)      NOT NULL                         comment '权限中文名称',
    ename               varchar(100)      NOT NULL                         comment '权限英文名称',
    permissionType      int(1)            NOT NULL                         comment '权限类型{1:代表菜单,0:代表功能}',
    enable              int(1)            NOT NULL default 1               comment '是否启用{1:是,0:否}',
    parentId            int(10)           NOT NULL                         comment '父权限Id',
    description         varchar(200)                                       comment '权限描述',
    indexId             int(10)           NOT NULL default 1               comment '权限序列号，用于排序',
    gridUrl             varchar(200)                                       comment '列表页',
    constraint pkPermissions PRIMARY KEY (id)
)comment='权限表';
-----------------------------------------------------------------------
--                             部门模块对象表(DEPARTMENTS)                                                                                                 
-----------------------------------------------------------------------
DROP TABLE IF EXISTS `Departments`;
CREATE TABLE Departments (
	id                  int(10)           auto_increment NOT NULL          comment '主键Id',
	name                varchar(60)       NOT NULL                         comment '部门名称',
	parentId            int(10)           NOT NULL                         comment '父部门Id',
	code                varchar(60)       NOT NULL                         comment '部门编号',
	remark              varchar(300)                                       comment '备注',
	constraint pkDepartments PRIMARY KEY (id)
)comment='部门表';
-----------------------------------------------------------------------
--                             用户对象表(USERS)                                                                                                 
-----------------------------------------------------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE users (
	id                  int(10)           auto_increment NOT NULL          comment '主键Id',
	deptId              int(10)           NOT NULL                         comment '所属部门ID',
	roleId              int(10)           NOT NULL                         comment '拥有角色ID',
	account             varchar(60)       NOT NULL                         comment '账号',
	name                varchar(60)       NOT NULL                         comment '姓名',
	password            varchar(120)      NOT NULL                         comment '密码',
	email               varchar(100)                                       comment '邮箱',
	active              int(1)            NOT NULL DEFAULT 1               comment '是否启用{1:是,0:否}',
	headPic    			longtext	                              		   comment '头像图片',
	constraint pkUsers PRIMARY KEY (id)
)comment='用户表';
-----------------------------------------------------------------------
--                             角色对象表(ROLES)                                                                                                 
-----------------------------------------------------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE roles (
	id                  int(10)           auto_increment NOT NULL          comment '主键Id',
	name                varchar(60)       NOT NULL                         comment '角色名称',
	remark              varchar(300)                                       comment '备注',
	isSuper             int(1)            NOT NULL  default 0              comment '是否超级管理员{1:是,0:否}',
	permissions         varchar(600)                                       comment '功能权限，权限Id集合，多个权限用逗号相隔',
	constraint pkRoles PRIMARY KEY (id)
)comment='角色表';

