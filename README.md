# Singer Config System (分布式配置中心原型)

这是一个基于 Spring Boot 的轻量级配置管理系统，展示了分布式环境下高性能配置分发与同步的解决方案。

## 🌟 核心特性
* **多级缓存机制**：集成本地缓存 (Caffeine) 与分布式缓存 (Redis)，实现微秒级响应。
* **二方包 SDK 设计**：抽象出 `config-client` 模块，业务方只需引入依赖即可通过注解获得配置能力。
* **实时一致性**：基于 Redis Pub/Sub 模式实现配置变更通知，秒级清理各节点本地缓存。
* **可视化管理**：提供基于 Vue3 + Element Plus 的管理后台。

## 🏗️ 技术栈
* Backend: Spring Boot 3.x, MyBatis Plus, Redis, Caffeine
* Frontend: Vue 3, Element Plus, Axios
* Messaging: Redis Pub/Sub

## 🚀 启动说明
1. 启动 Redis 服务。
2. 运行 `config-admin` 模块 (Port: 8080)。
3. 运行 `config-demo` 模块 (Port: 8081)。
4. 访问 `http://localhost:8080/index.html` 进行配置管理。
