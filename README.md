# 智享餐饮管理平台（Zhixiang Restaurant Platform）

面向商户的**全链路餐饮交易与智能运营管理系统**。在传统商品管理、订单流转、多维 BI 数据报表基础上，引入 **LangChain4j** 与本地 **RAG** 能力构建智能决策 Agent，支持多轮对话、记忆持久化，实现传统后台向智能化交互的全面升级。

> ## 🏗️ Spring Cloud 微服务架构（2026-08 重构）
>
> 后端已从单体 Spring Boot 重构为 **Spring Cloud Alibaba 微服务**，按业务域拆分为 5 个服务 + 1 个网关：
>
> | 服务 | 端口 | 职责 | 关键依赖 |
> |------|------|------|----------|
> | `gateway-service` | 8080 | Spring Cloud Gateway，统一路由 + CORS | Nacos Discovery |
> | `auth-service` | 8081 | 用户/鉴权/注册/后台用户管理 | MySQL, Nacos |
> | `trade-service` | 8082 | 商品/分类/订单/桌台/优惠券/营业状态/顾客端 | MySQL, Redis, Nacos |
> | `ops-service` | 8083 | 会员/库存/采购/供应商/员工/排班/报表 | MySQL, Nacos |
> | `ai-service` | 8084 | AI 对话/RAG/记忆/工具箱 | LangChain4j, Redis, Nacos, **OpenFeign→trade/ops**, **Sentinel 容错** |
> | `restaurant-common` | — | 共享：Result/JWT/UserContext/拦截器/自动配置 | — |
>
> **基础设施**：Nacos（注册中心 + 配置中心，默认 `127.0.0.1:8848`）、MySQL（共享库）、Redis（AI 记忆/营业状态机）。
> **服务间调用**：`ai-service` 通过 **OpenFeign** 调用 `trade-service` / `ops-service` 的 REST 接口（原 AI 工具箱依赖的本地 service 已改为跨服务调用），并接入 **Sentinel** 做熔断降级（`fallback` 返回安全默认值）。
> **前端**：无感知——网关在 8080 统一暴露 `/api/**`，静态资源仍由 `http.server:5173` 托管，原接口路径全部保持不变。
>
> 环境变量可在 `bootstrap.yml` / `application.yml` 中覆盖：`NACOS_ADDR`、`NACOS_NAMESPACE`、`DB_HOST/DB_PORT/DB_NAME/DB_USER/DB_PASSWORD`、`REDIS_HOST/REDIS_PORT`、`AI_API_KEY/AI_BASE_URL/AI_MODEL`。

## 技术栈

- **后端**：Spring Boot 3.2、Spring Cloud 2023.0、Spring Cloud Alibaba 2023.0.1、Spring Cloud Gateway、OpenFeign、Sentinel、Nacos、MySQL 8、MyBatis、Redis、JWT、LangChain4j、RAG
- **前端**：原生 HTML/CSS/JS（零构建，开箱即用）
- **AI**：兼容 OpenAI 协议的模型（默认对接本地 Ollama，可切换云端）

## 核心特性

| 能力 | 实现方式 |
|------|----------|
| 报表智能分析 | 将多表聚合报表封装为 AI 工具函数，大模型通过 **Function Calling** 动态调用后端方法 |
| 营业状态一键修改 | Redis 营业状态机 + MySQL 落地，AI 工具可直接切换 OPEN/CLOSED |
| 异常经营诊断 | 核心指标封装为工具，结合 Prompt 规则链对异常数据**交叉诊断**，输出营销调优建议 |
| 多轮对话记忆 | 基于 Redis 持久化存储，序列化会话上下文 + **TTL** 自动管理生命周期，降低接口成本 |
| 本地 RAG 知识库 | 内存向量库 + **递归文本切片**，本地客诉/运营规则检索，无外部向量库，规则问答准确率高 |
| 会员/桌台/库存/采购/优惠券/排班 | 完整业务域建模，AI 工具箱可查询库存预警、生成采购建议、评估排班、分析券效果 |

## 目录结构

```
rag/
├── backend/                 # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/zhixiang/restaurant/
│       │   ├── common/         # Result、JWT、UserContext
│       │   ├── config/         # JWT/RAG/LangChain 配置
│       │   ├── entity/         # 实体类
│       │   ├── mapper/         # MyBatis Mapper + XML
│       │   ├── service/        # 业务服务（含 Report/BusinessStatus/Order/Member/Inventory/Purchase/Coupon/Schedule）
│       │   ├── controller/     # REST 接口
│       │   ├── interceptor/    # JWT 拦截器
│       │   ├── ai/             # 对话服务、Redis 记忆、RAG 知识库
│       │   └── ai/tools/       # AI 工具箱（BusinessTools + OperationsTools，Function Calling）
│       └── resources/
│           ├── application.yml
│           ├── sql/schema.sql  # 建库建表 + 初始化数据
│           └── mapper/*.xml
└── frontend/                # 前端管理后台
    ├── index.html
    ├── css/style.css
    └── js/{api.js, app.js}
```

## 环境依赖

- JDK 17+
- Maven 3.8+
- MySQL 8.x
- Redis 7.x
- （可选）Ollama 本地模型：`ollama pull qwen2.5:7b` 与 `ollama pull bge-m3`

## 快速启动

### 1. 数据库

库表由 MyBatis-Plus 实体自动映射，无需手动建表（库名 `zhixiang_restaurant`，默认账号 `root`/`123456`）。如需补充演示/订单数据（让经营看板、营收趋势、品类报表有连续真实数据），执行：

```bash
mysql -uroot -p123456 zhixiang_restaurant < backend/sql/seed_demo_orders.sql
```

脚本会先清理自身写入的 `SEED%` 前缀演示订单再插入，可重复执行；补的是最近 7 天（含少量 REFUNDED）的 PAID 订单及明细。

### 2. Redis

```bash
redis-server   # 默认 127.0.0.1:6379
```

### 3. 配置模型（application.yml）

默认对接本地 Ollama：

```yaml
ai:
  model:
    base-url: http://localhost:11434/v1
    api-key: sk-no-key-required
    chat-model: qwen2.5:7b
    embedding-model: bge-m3:latest
```

> 无 GPU/模型环境时，可改为任意 OpenAI 兼容端点（如云端 API），或修改 `AiChatService` 临时降级为规则应答，系统其余功能不受影响。

### 4. 启动后端（微服务）

确保 Nacos 已运行，然后构建并启动全部服务：

```bash
cd backend
mvn -DskipTests install          # 先 install common 供各服务依赖
mvn -pl gateway-service -DskipTests spring-boot:run
mvn -pl auth-service   -DskipTests spring-boot:run
mvn -pl trade-service  -DskipTests spring-boot:run
mvn -pl ops-service    -DskipTests spring-boot:run
mvn -pl ai-service     -DskipTests spring-boot:run
# 网关聚合接口前缀：http://localhost:8080/api
```

> 也可直接运行 `start-all.ps1`（Windows）一键拉起 Redis + 构建后端 + 5 个微服务 + 前端：
> ```powershell
> # 以【管理员或普通用户】右键「使用 PowerShell 运行」start-all.ps1
> # 脚本会：检查 Nacos(8848) -> 启动 Redis -> mvn install -> java -jar 启动各服务 -> 前端 http://localhost:5173
> ```
> 脚本启动前会校验 Nacos 已在 `127.0.0.1:8848` 监听，未启动会直接报错退出（请先 `start D:\nacos\bin\startup.cmd -m standalone`）。
> 各服务启动后会自动向 Nacos 注册，`gateway-service` 通过服务发现（`lb://auth-service` 等）转发请求。

### 5. 启动前端

直接用浏览器打开 `frontend/index.html`，或将 `frontend/` 作为静态资源托管。推荐用任意静态服务器：

```bash
cd frontend
python -m http.server 5173
# 访问 http://localhost:5173
```

> 登录账号：`admin` / `123456`

## 接口概览

| 模块 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 鉴权 | POST | `/api/auth/login` | 登录获取 JWT |
| 商品 | GET/POST/PUT/DELETE | `/api/products` | 商品增删改查、上下架 |
| 分类 | GET/POST/DELETE | `/api/categories` | 分类管理 |
| 订单 | POST/GET | `/api/orders` | 下单、支付、退款、查询 |
| 报表 | GET | `/api/report/today\|category\|trend\|top\|diagnose` | BI 多维报表与诊断 |
| 营业 | GET/POST | `/api/business/status` | 读取/切换营业状态 |
| 会员 | GET/POST/DELETE | `/api/members` | 会员增删改查、消费记账 |
| 桌台 | GET/POST/PUT/DELETE | `/api/tables` | 桌台管理与状态切换 |
| 库存 | GET/POST/DELETE | `/api/inventory` | 原料库存、出入库调整、预警 |
| 供应商 | GET/POST/DELETE | `/api/suppliers` | 供应商管理 |
| 采购 | POST/GET | `/api/purchases` | 新建采购单、入库、明细 |
| 优惠券 | GET/POST/PUT/DELETE | `/api/coupons` | 券管理、启停、发放 |
| 员工 | GET/POST/DELETE | `/api/employees` | 员工管理 |
| 排班 | GET/POST/DELETE | `/api/schedules` | 按日/区间排班 |
| AI | POST | `/api/ai/chat` | 智能对话（工具调用+RAG） |
| AI | POST | `/api/ai/memory/clear` | 清空对话记忆 |
| AI | POST | `/api/ai/knowledge/reload` | 重载 RAG 知识库 |

## 演示要点

1. **经营看板**：实时聚合营收/订单/客单价/退款率/会员/桌台/库存预警，AI 自动交叉诊断并给出营销建议。
2. **AI 智能助手**：自然语言对话，例如：
   - “分析今天的经营情况，有哪些异常？” → 调用 `diagnoseBusiness` 工具
   - “把营业状态改成打烊” → 调用 `setBusinessStatus` 工具修改 Redis 状态机
   - “哪些原料低于阈值，需要补货？” → 调用 `lowStock` 工具
   - “生成采购补货建议” → 调用 `purchaseAdvice` 工具（结合供应商匹配）
   - “今天排班人力够吗？” → 调用 `scheduleOf` 工具评估高峰人力
   - “客诉上菜慢该怎么处理？” → RAG 检索知识库给出标准话术
3. **多轮记忆**：对话上下文存入 Redis（TTL 1800s），刷新页面不丢失。
