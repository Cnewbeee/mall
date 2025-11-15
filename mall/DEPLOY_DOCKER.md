# Docker 一键部署指南

本指南提供在 Windows (PowerShell) 下将 mall 后端与依赖服务通过 Docker Compose 拉起的步骤。

## 先决条件
- 已安装 Docker Desktop，启用 Linux 容器
- 已安装 Git 与 Maven（可由 IDE 触发构建）

## 1. 构建后端 Jar
在仓库根目录执行，生成各模块的可执行 Jar：

```powershell
# 跳过测试加速
mvn -DskipTests package
```

构建后将生成：
- `mall-admin/target/*.jar`
- `mall-search/target/*.jar`
- `mall-portal/target/*.jar`

## 2. 配置环境变量（可选）
复制 `.env.example` 为 `.env` 并按需修改：

```powershell
Copy-Item .env.example .env
```

关键变量：
- `MYSQL_ROOT_PASSWORD`：MySQL root 密码
- `MYSQL_DATABASE`：初始化数据库名（可保持 mall）
- `TZ`：时区（默认 Asia/Shanghai）
- `ES_JAVA_XMS / ES_JAVA_XMX`：Elasticsearch 堆大小

## 3. 启动依赖与服务
在仓库根目录执行：

```powershell
# 首次启动（后台运行）
docker compose up -d --build

# 查看容器列表
docker ps

# 查看某服务日志（例如 mall-portal）
docker logs -f mall-portal
```

常用端口：
- mall-admin: http://localhost:8080
- mall-search: http://localhost:8081
- mall-portal: http://localhost:8085
- MySQL: localhost:3306（root / 来自 .env）
- Redis: localhost:6379
- RabbitMQ 管理台: http://localhost:15672
- Elasticsearch: http://localhost:9200
- Kibana: http://localhost:5601
- MongoDB: localhost:27017

## 4. 初始化数据库（可选）
`./sql` 目录会通过 `/docker-entrypoint-initdb.d` 自动执行初始化脚本，可将 `mall.sql`、`score.sql` 等放入其中。

## 5. 常见问题
- Windows 上尽量使用命名卷（本 compose 已采用），避免路径权限问题。
- 若 Jar 未生成，应用镜像构建会失败。请先执行第 1 步。
- 如果端口被占用，可在 `docker-compose.yml` 中调整宿主机端口映射。

## 6. 停止与清理
```powershell
# 停止容器
docker compose down

# 停止并清理所有数据卷（谨慎）
docker compose down -v
```

## 附：已有 compose 文件
项目中 `document/docker/docker-compose-env.yml` 与 `document/docker/docker-compose-app.yml` 也可单独使用；当前根目录 `docker-compose.yml` 将两者合并并支持本地构建后端镜像，推荐使用。
