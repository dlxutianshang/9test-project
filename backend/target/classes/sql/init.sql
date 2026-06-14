CREATE DATABASE IF NOT EXISTS project_test DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE project_test;

DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(255) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

DROP TABLE IF EXISTS sys_department;
CREATE TABLE sys_department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '部门ID',
    dept_name VARCHAR(50) NOT NULL COMMENT '部门名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父级部门ID',
    ancestors VARCHAR(500) DEFAULT '' COMMENT '祖级列表（逗号分隔）',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-停用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

DROP TABLE IF EXISTS sys_permission;
CREATE TABLE sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '权限ID',
    permission_name VARCHAR(50) NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码',
    description VARCHAR(255) COMMENT '权限描述',
    type TINYINT DEFAULT 1 COMMENT '类型：1-菜单，2-按钮，3-接口',
    parent_id BIGINT DEFAULT 0 COMMENT '父级ID',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

DROP TABLE IF EXISTS sys_role_permission;
CREATE TABLE sys_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

DROP TABLE IF EXISTS sys_operation_log;
CREATE TABLE sys_operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    operation VARCHAR(100) COMMENT '操作描述',
    method VARCHAR(200) COMMENT '请求方法',
    params TEXT COMMENT '请求参数',
    ip VARCHAR(50) COMMENT 'IP地址',
    duration BIGINT COMMENT '耗时（毫秒）',
    status TINYINT DEFAULT 1 COMMENT '状态：1-成功，0-失败',
    error_msg VARCHAR(500) COMMENT '错误信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

DROP TABLE IF EXISTS sys_password_reset;
CREATE TABLE sys_password_reset (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    reset_token VARCHAR(100) NOT NULL UNIQUE COMMENT '重置令牌',
    expire_time DATETIME NOT NULL COMMENT '过期时间',
    used TINYINT DEFAULT 0 COMMENT '是否已使用：0-否，1-是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_reset_token (reset_token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='密码重置表';

DROP TABLE IF EXISTS sys_post;
CREATE TABLE sys_post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '岗位ID',
    post_code VARCHAR(50) NOT NULL UNIQUE COMMENT '岗位编码',
    post_name VARCHAR(50) NOT NULL COMMENT '岗位名称',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-停用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_post_code (post_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位表';

DROP TABLE IF EXISTS sys_user_post;
CREATE TABLE sys_user_post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    post_id BIGINT NOT NULL COMMENT '岗位ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_post (user_id, post_id),
    INDEX idx_user_id (user_id),
    INDEX idx_post_id (post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户岗位关联表';

INSERT INTO sys_user (username, password, nickname, email, phone, status) VALUES
('admin', '$2a$10$KY1.wVVdBtmTirfBEPkAPeFwkLzN5OipEMP48uk/BdR1LsgKvbS3.', '超级管理员', 'admin@example.com', '13800138000', 1),
('test', '$2a$10$KY1.wVVdBtmTirfBEPkAPeFwkLzN5OipEMP48uk/BdR1LsgKvbS3.', '测试用户', 'test@example.com', '13900139000', 1);

INSERT INTO sys_role (role_name, role_code, description, status) VALUES
('超级管理员', 'ROLE_ADMIN', '拥有所有权限', 1),
('普通用户', 'ROLE_USER', '普通用户权限', 1),
('访客', 'ROLE_GUEST', '只读权限', 1);

INSERT INTO sys_department (dept_name, parent_id, ancestors, sort_order, status) VALUES
('若依科技', 0, '0', 0, 1),
('深圳总公司', 1, '0,1', 0, 1),
('研发部门', 2, '0,1,2', 0, 1),
('前端组', 3, '0,1,2,3', 0, 1),
('后端组', 3, '0,1,2,3', 1, 1),
('测试组', 3, '0,1,2,3', 2, 1),
('市场部门', 2, '0,1,2', 1, 1),
('财务部门', 2, '0,1,2', 2, 1),
('北京分公司', 1, '0,1', 1, 1),
('行政部', 9, '0,1,9', 0, 1);

INSERT INTO sys_post (post_code, post_name, sort_order, status) VALUES
('ceo', '董事长', 1, 1),
('gm', '总经理', 2, 1),
('vp', '副总经理', 3, 1),
('pm', '项目经理', 4, 1),
('hr', '人力资源', 5, 1),
('se', '软件工程师', 6, 1),
('qa', '测试工程师', 7, 1),
('fd', '前端开发', 8, 1),
('bd', '后端开发', 9, 1);

INSERT INTO sys_permission (permission_name, permission_code, description, type, parent_id, sort_order, status) VALUES
('用户管理', 'user:manage', '用户管理菜单', 1, 0, 1, 1),
('用户列表', 'user:list', '查看用户列表', 2, 1, 1, 1),
('新增用户', 'user:add', '新增用户', 2, 1, 2, 1),
('编辑用户', 'user:edit', '编辑用户', 2, 1, 3, 1),
('删除用户', 'user:delete', '删除用户', 2, 1, 4, 1),
('用户状态', 'user:status', '启用/禁用用户', 2, 1, 5, 1),
('重置密码', 'user:resetPassword', '重置用户密码', 2, 1, 6, 1),
('角色管理', 'role:manage', '角色管理菜单', 1, 0, 3, 1),
('角色列表', 'role:list', '查看角色列表', 2, 8, 1, 1),
('新增角色', 'role:add', '新增角色', 2, 8, 2, 1),
('编辑角色', 'role:edit', '编辑角色', 2, 8, 3, 1),
('删除角色', 'role:delete', '删除角色', 2, 8, 4, 1),
('分配权限', 'role:assignPermission', '分配权限给角色', 2, 8, 5, 1),
('权限管理', 'permission:manage', '权限管理菜单', 1, 0, 4, 1),
('权限列表', 'permission:list', '查看权限列表', 2, 14, 1, 1),
('新增权限', 'permission:add', '新增权限', 2, 14, 2, 1),
('编辑权限', 'permission:edit', '编辑权限', 2, 14, 3, 1),
('删除权限', 'permission:delete', '删除权限', 2, 14, 4, 1),
('部门管理', 'dept:manage', '部门管理菜单', 1, 0, 2, 1),
('部门列表', 'dept:list', '查看部门列表', 2, 19, 1, 1),
('新增部门', 'dept:add', '新增部门', 2, 19, 2, 1),
('编辑部门', 'dept:edit', '编辑部门', 2, 19, 3, 1),
('删除部门', 'dept:delete', '删除部门', 2, 19, 4, 1),
('岗位管理', 'post:manage', '岗位管理菜单', 1, 0, 2, 1),
('岗位列表', 'post:list', '查看岗位列表', 2, 24, 1, 1),
('新增岗位', 'post:add', '新增岗位', 2, 24, 2, 1),
('编辑岗位', 'post:edit', '编辑岗位', 2, 24, 3, 1),
('删除岗位', 'post:delete', '删除岗位', 2, 24, 4, 1),
('操作日志', 'log:manage', '操作日志菜单', 1, 0, 5, 1),
('日志列表', 'log:list', '查看日志列表', 2, 29, 1, 1),
('数据导出', 'export:data', '导出数据', 2, 0, 6, 1),
('数据导入', 'import:data', '导入数据', 2, 0, 7, 1);

INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 2);

INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7),
(1, 8), (1, 9), (1, 10), (1, 11), (1, 12), (1, 13),
(1, 14), (1, 15), (1, 16), (1, 17), (1, 18),
(1, 19), (1, 20), (1, 21), (1, 22), (1, 23),
(1, 24), (1, 25), (1, 26), (1, 27), (1, 28),
(1, 29), (1, 30), (1, 31), (1, 32), (1, 33),
(2, 1), (2, 2),
(2, 8), (2, 9),
(2, 14), (2, 15),
(2, 19), (2, 20),
(2, 24), (2, 25),
(2, 29), (2, 30),
(3, 2), (3, 9), (3, 15), (3, 20), (3, 25), (3, 30);
