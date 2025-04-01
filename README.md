# spring-ai-demo

## 背景介绍

AI 正在不断改变我们的工作方式和生活体验。为了让更多开发者能够轻松集成 AI 能力到他们的应用中，Spring 团队推出了 `spring-ai`，这是一个基于 Spring 框架的 AI 集成工具包。

`spring-ai` 提供了简洁的 API 和灵活的配置，使得开发者能够在 Spring Boot 项目中轻松接入不同的 AI 模型和服务，如对话模型、嵌入模型等。通过这一版本（`spring-ai.version 1.0.0-M6`），开发者能够在应用中快速实现自然语言处理和 AI 驱动的智能对话功能。

##### 本 demo 演示了如何使用 `spring-ai` 集成和调用 AI 模型，以及如何结合 Redis 和 PostgreSQL 来进行数据存储和Rag检索。此外，本项目还开发了两个 MCP（Model-Controller-Processor）工具，以便于处理 AI 相关的计算和优化任务，进一步提升系统性能和响应速度。通过本 demo，你可以快速了解如何将 AI 技术与 Spring 项目结合，实现智能对话、知识库检索等功能。

###### 相关文档如下：

- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/1.0/index.html)
- [一文彻底搞懂大模型 - RAG（检索、增强、生成）-CSDN博客](https://blog.csdn.net/a2875254060/article/details/142468037)
- [最近智能体Agent 圈很热的MCP是什么？_agent mcp-CSDN博客](https://blog.csdn.net/m0_63171455/article/details/146421129?ops_request_misc=%7B%22request%5Fid%22%3A%22366e012711942477353e80961a91944f%22%2C%22scm%22%3A%2220140713.130102334.pc%5Fall.%22%7D&request_id=366e012711942477353e80961a91944f&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~first_rank_ecpm_v1~hot_rank-8-146421129-null-null.142^v102^pc_search_result_base1&utm_term=mcp是什么&spm=1018.2226.3001.4187)

话不多说，我们直接开始。

## 技术栈

- **后端框架**: Spring Boot 3.x & Spring AI 1.0.0-M6
- **数据库**: MySQL 8x & PgVector & Redis
- **API 文档**: Knife4j
- **构建工具**: Maven

## 项目结构

```
+---src
|   \---main
|       +---java
|       |   \---com
|       |       \---shelly
|       |           \---ai
|       |               +---api
|       |               +---common
|       |               +---config
|       |               +---controller
|       |               +---exception
|       |               +---handler
|       |               +---model
|       |               \---tool
|       \---resources
|               application.yml
```

## 环境要求

- JDK 17+
- Maven 3.8+
- MySQL & PgVector
- Git

## 快速开始

### 1.克隆仓库

```git
git clone https://github.com/shelly1227/spring-ai-demo.git
```
### 2.完成配置

```yaml
spring:
  application:
    name: spring-ai-demo
  datasource:
    driver-class-name: org.postgresql.Driver
    username: ${DB_USERNAME}  # 替换为数据库用户名环境变量
    password: ${DB_PASSWORD}  # 替换为数据库密码环境变量
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/ai-rag-knowledge  # 替换为数据库主机和端口环境变量
    type: com.zaxxer.hikari.HikariDataSource
```

##### 注意：这一步如果不做Rag也可忽略。PgSql以及Vector插件自行准备，这里我用的是PgAdmin进行图形化管理，运行下面的SQL语句，完成初始化：

```sql
 DROP TABLE IF EXISTS public.vector_store_ollama_deepseek;
      -- 创建新的表，使用UUID作为主键
      CREATE TABLE public.vector_store_ollama_deepseek (
          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
          content TEXT NOT NULL,
          metadata JSONB,
          embedding VECTOR(768)
 );
```

##### 下面是AI相关的配置，在本项目中，为了避免申请openai官方apikey的困难，选择了一种较为繁琐但申请简单的方式：

- [DeepSeek 开放平台](https://platform.deepseek.com/api_keys)

##### 申请的key填在下方：

```yaml
  ai:
    openai:
      base-url: https://api.deepseek.com
      api-key: ${OPENAI_API_KEY}
```

##### 这是我们的聊天模型，用来实现对话以及mcp相关就已经足够了，如果要体验rag相关的功能，则还需要一种模型——嵌入模型。用于将信息转成向量存储在向量库，这里使用Docker部署Ollama，用其提供的`nomic-embed-text`，这一步操作简单，可自行查阅。然后将url填入：

```yaml
    ollama:
      base-url: ${OLLAMA_BASE_URL}  # 替换为Ollama主机URL环境变量
      vectorTableName: vector_store_ollama_deepseek
      chat:
        # 不使用其聊天模型
        # options:
        # model: deepseek-r1:1.5b
        enabled: false
      embedding:
        options:
          model: nomic-embed-text
```

##### 最后配置好Redis（不用也行，记得删除相关代码）。

### 3.启动项目

##### 直接运行，因为这里用到了Knife4j接口文档，可访问`http://localhost:8090/doc.html`进行访问，下面来演示一下。

### 4.功能演示

- #### 对话功能

​						![image](https://github.com/user-attachments/assets/a38b6afb-7846-4f64-a6a5-7e4b8107810e)

##### 流式对话

![image](https://github.com/user-attachments/assets/55f42a49-1959-447f-8d18-f98326ae14ae)


- #### Rag相关功能

    - ##### 写一个txt文件

        -![image](https://github.com/user-attachments/assets/a394a869-743e-4758-afe5-798dfdf50ac9)


    - ##### 上传至知识库

        - ![image](https://github.com/user-attachments/assets/fb438383-eb8d-4286-83d1-c72fd6a754ef)

    - ##### 在PgAdmin中可见

        -![image](https://github.com/user-attachments/assets/447c5292-6fa2-4b23-b12d-6062aaafe683)

    - ##### 问答测试

        - ![image](https://github.com/user-attachments/assets/f78d9a4e-f1ab-4504-bf6b-ad6f3da44239)

- #### Mcp测试

##### 		目前项目中开发了两个Tool可供调用。一个是电脑配置，一个是时间工具，测试如下：

![image](https://github.com/user-attachments/assets/bcf18206-0dd9-47a6-8224-b1ef2ff3d35a)
