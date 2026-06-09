CREATE DATABASE IF NOT EXISTS baopu_learning DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE baopu_learning;

CREATE TABLE IF NOT EXISTS bp_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  tenant_id BIGINT NOT NULL DEFAULT 1,
  dingtalk_user_id VARCHAR(64) NOT NULL,
  name VARCHAR(100) NOT NULL,
  mobile VARCHAR(30) NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_tenant_ding_user (tenant_id, dingtalk_user_id)
);

CREATE TABLE IF NOT EXISTS bp_course (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  tenant_id BIGINT NOT NULL DEFAULT 1,
  title VARCHAR(255) NOT NULL,
  description TEXT NULL,
  cover_url VARCHAR(500) NULL,
  category VARCHAR(64) NOT NULL DEFAULT '通用课程',
  lecturer VARCHAR(100) NOT NULL DEFAULT '抱朴学院',
  duration_minutes INT NOT NULL DEFAULT 30,
  price DECIMAL(10,2) NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS bp_enrollment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  course_id BIGINT NOT NULL,
  progress_percent INT NOT NULL DEFAULT 0,
  completed TINYINT NOT NULL DEFAULT 0,
  enrolled_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  completed_at DATETIME NULL,
  UNIQUE KEY uk_user_course (user_id, course_id),
  KEY idx_course_id (course_id),
  CONSTRAINT fk_enrollment_user FOREIGN KEY (user_id) REFERENCES bp_user(id),
  CONSTRAINT fk_enrollment_course FOREIGN KEY (course_id) REFERENCES bp_course(id)
);

INSERT INTO bp_course (id, tenant_id, title, description, cover_url, category, lecturer, duration_minutes, price, status)
VALUES
  (1, 1, '新员工入职必修课', '覆盖企业文化、制度规范、信息安全和协作流程，帮助员工快速进入工作状态。', 'https://images.unsplash.com/photo-1521737604893-d14cc237f11d?auto=format&fit=crop&w=1200&q=80', '入职培训', '人力资源部', 45, 0, 1),
  (2, 1, '钉钉组织协同实战', '围绕消息、审批、日程、文档和群协同设计高频工作场景，让团队协同更透明。', 'https://images.unsplash.com/photo-1552664730-d307ca884978?auto=format&fit=crop&w=1200&q=80', '数字化办公', '数字化运营组', 60, 0, 1),
  (3, 1, '一线管理者沟通训练', '通过目标拆解、反馈对话和复盘模板，提高管理者的日常带教和团队推进能力。', 'https://images.unsplash.com/photo-1556761175-b413da4baf72?auto=format&fit=crop&w=1200&q=80', '管理能力', '抱朴学院讲师团', 90, 199, 1),
  (4, 1, '客户服务标准化流程', '沉淀从咨询接待、问题分级、闭环跟进到满意度回访的服务流程。', 'https://images.unsplash.com/photo-1556745757-8d76bdb6984b?auto=format&fit=crop&w=1200&q=80', '客户服务', '服务运营部', 50, 99, 1)
ON DUPLICATE KEY UPDATE
  title = VALUES(title),
  description = VALUES(description),
  cover_url = VALUES(cover_url),
  category = VALUES(category),
  lecturer = VALUES(lecturer),
  duration_minutes = VALUES(duration_minutes),
  price = VALUES(price),
  status = VALUES(status);
