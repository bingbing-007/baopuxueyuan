# 部署手册

## 一、环境准备

1. 服务器：阿里云 ECS（Ubuntu 22.04），安装 Docker + Docker Compose
2. 域名：配置 A 记录指向 ECS 公网 IP
3. SSL：通过阿里云 CDN 或 Nginx 配置 HTTPS

## 二、钉钉配置

1. 登录 [钉钉开发者后台](https://open-dev.dingtalk.com)
2. 创建企业内部应用 → 获取 AppKey / AppSecret / AgentId
3. 配置应用权限：通讯录读取、消息通知、免登
4. 配置 H5 微应用首页地址：`https://你的域名/`
5. 配置工作台入口

## 三、部署步骤

```bash
# 1. 克隆代码
git clone https://github.com/bingbing-007/baopuxueyuan.git
cd baopuxueyuan

# 2. 配置环境变量
cp .env.example .env
vim .env  # 填入钉钉和 OSS 配置

# 3. 构建并启动
docker compose -f deploy/docker-compose.yml up -d --build

# 4. 验证
curl http://localhost/actuator/health
```

## 四、服务端口

| 服务 | 内部端口 | 说明 |
|------|---------|------|
| Nginx (web-portal) | 80 | 前端静态 + API 代理 |
| Gateway | 8000 | 统一网关 |
| Learning Service | 8080 | 学习业务 |
| IAM Service | 8081 | 组织权限 |
| DingTalk Service | 8082 | 钉钉集成 |
| MySQL | 3306 | 数据库 |
| Redis | 6379 | 缓存 |
| RabbitMQ | 5672 | 消息队列 |

## 五、钉钉工作台发布

1. 在开发者后台 → 应用发布 → 版本管理 → 创建版本
2. 填写版本号和更新说明
3. 提交审核 → 通过后员工可在工作台看到应用
