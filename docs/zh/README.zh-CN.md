# Dict-I18n

[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/Silwings-git/dict-i18n)

`dict-i18n` 是一套基于字典实现领域数据编码（如订单状态、商品类型等）国际化（i18n）的解决方案，深度集成 Spring Boot
生态，支持多数据源加载字典、自动注入本地化文本到 Java 对象及 REST 响应，简化多语言描述管理，适配固定或低频变更的编码数据场景。

## 🌟 核心价值

聚焦**领域编码数据国际化**痛点，填补传统资源束（Resource Bundle）在编码映射场景的空白：

- **统一字典管理**：替代硬编码多语言描述，通过枚举/Java Bean 规范字典定义，支持文件、数据库、Redis 等多源加载
- **自动国际化增强**：借助 `@DictDesc` 注解，自动在 JSON 响应注入编码对应的本地化文本，无需手动处理映射
- **灵活扩展适配**：模块化设计（核心、加载器、自动配置等），支持自定义加载器、语言提供器，适配复杂业务需求
- **严格校验保障**：启动时校验字典名称唯一性，递归扫描对象支持循环检测，规避运行时冲突

## 📚 文档导航

| 分类     | 内容说明                 | 文档地址                           |  
|--------|----------------------|--------------------------------|  
| 特性介绍   | 框架核心特性               | [首页.md](./首页.md)               |  
| 快速上手   | 框架核心能力、最简集成流程        | [快速开始.md](./guide/快速开始.md)     |  
| 加载器架构  | 多源加载器设计、扩展机制         | [加载器说明.md](loader/加载器说明.md)    |  
| 自动配置详解 | Starter 自动装配流程、配置项说明 | [启动器说明.md](./starter/启动器说明.md) |  
| 配置指南   | 字典扫描、端点、增强器等配置项      | [配置说明.md](./config/配置说明.md)    |  
| 常见问题   | 集成与使用中的典型问题及解决方案     | [常见问题.md](./faq/常见问题.md)       |  

## ✨ 核心特性

### 1. 字典定义灵活

支持通过**枚举/Java Bean 实现 `Dict` 接口**定义字典，覆盖静态编码、动态加载等场景：

```java
// 枚举示例
public enum OrderStatus implements Dict {
    PENDING("pending"),
    COMPLETED("completed");

    private final String code;

    @Override
    public String dictName() {
        // 字典唯一标识
        return "order_status";
    }

    @Override
    public String code() {
        return this.code;
    }
}
```

### 2. 多源加载与优先级

内置**文件（YAML/Properties）、数据库、Redis、声明式**等加载器，支持通过 `loader-order` 配置加载优先级：

```yaml
dict-i18n:
  loader-order:
    - redis  # Redis 加载器优先
    - sql    # 数据库加载器次之
    - file   # 文件加载器兜底
```

### 3. 自动响应增强

通过 `@DictDesc` 注解，自动在 JSON 响应注入编码对应的本地化描述，无需手动编写转换逻辑：

```java
public class OrderDTO {
    private String status;

    // 自动注入 status 对应的国际化描述
    @DictDesc(OrderStatus.class)
    private String statusDesc;
}

// 响应示例
{
    "status":"pending",
    "statusDesc":"Pending" // 或根据语言配置返回对应语言描述
}
```

### 4. 严格启动校验

启动时自动校验**字典名称唯一性**，避免多枚举复用相同字典名导致冲突：

```java
// 冲突示例（启动时报错）
public enum OrderState implements Dict {
    PENDING("pending");

    @Override
    public String dictName() {
        return "order_status"; // 与 OrderStatus 字典名冲突
    }
}
```

## 🚀 快速开始

### 1. 引入依赖

在 `pom.xml` 中添加 `dict-i18n-spring-boot-starter`（自动集成核心、加载器等模块）：

```xml  

<dependency>
    <groupId>cn.silwings.dicti18n</groupId>
    <artifactId>dict-i18n-spring-boot-starter</artifactId>
    <version>1.0.5</version>
</dependency>

<!-- 按需添加加载器（至少选一个） -->
<!-- 静态声明加载器 -->
<dependency>
    <groupId>cn.silwings.dicti18n</groupId>
    <artifactId>dict-i18n-loader-declared</artifactId>
    <version>1.0.5</version>
</dependency>

<!-- Redis 加载器 -->
<dependency>
    <groupId>cn.silwings.dicti18n</groupId>
    <artifactId>dict-i18n-loader-redis</artifactId>
    <version>1.0.5</version>
</dependency>

<!-- 数据库加载器 -->
<dependency>
    <groupId>cn.silwings.dicti18n</groupId>
    <artifactId>dict-i18n-loader-sql</artifactId>
    <version>1.0.5</version>
</dependency>

<!-- 文件加载器（YAML/Properties） -->
<dependency>
    <groupId>cn.silwings.dicti18n</groupId>
    <artifactId>dict-i18n-loader-file</artifactId>
    <version>1.0.5</version>
</dependency>  
```  

### 2. 定义字典枚举

通过枚举实现 `Dict` 接口，声明**字典名称（`dictName`）**和**编码（`code`）**：

```java  
public enum OrderStatus implements Dict {
    PENDING("pending"),
    COMPLETED("completed");

    private final String code;

    @Override
    public String dictName() {
        return "order_status"; // 字典唯一标识，关联多语言配置  
    }

    @Override
    public String code() {
        return this.code;
    }

    // 构造方法、Getter 省略...  
}  
```  

### 3. 启用国际化增强

在 DTO 中通过 `@DictDesc` 标记需要注入描述的字段，指定关联的字典枚举：

```java  
public class OrderDTO {
    private String status;

    // 自动注入 status 对应的国际化描述  
    @DictDesc(OrderStatus.class)
    private String statusDesc;

    // Getter/Setter 省略...  
}  
```  

### 4. 配置字典源（以文件加载器为例）

在 `application.yml` 中配置加载优先级(可选)：

```yaml  
dict-i18n:
  loader-order:
    - file  # 文件加载器优先  

# 字典内容示例（dict-i18n/order_status.yml）  
order_status:
  pending: 待处理
  completed: 已完成  
```  

### 5. 验证效果

通过 Controller 返回 `OrderDTO`，JSON 响应会自动注入编码对应的本地化描述：

```json  
{
  "status": "pending",
  "statusDesc": "待处理"
}  
```  

## 📦 模块结构

| 模块名称                               | 功能说明                           |  
|------------------------------------|--------------------------------|  
| `dict-i18n-core`                   | 核心接口（`Dict`/`Loader`）、处理器、基础逻辑 |  
| `dict-i18n-loader`                 | 多源加载器实现（文件、数据库、Redis 等）        |  
| `dict-i18n-generator-maven-plugin` | Maven 插件，支持字典文件生成、同步           |  
| `dict-i18n-spring-boot-starter`    | Spring Boot 自动配置、REST 端点、响应增强  |  
| `dict-i18n-demo`                   | 示例项目，覆盖枚举定义、多源加载、响应增强等场景       |  

## 🔗 更多细节

完整文档可查阅项目 `docs` 目录：

- 加载器扩展（自定义数据源）、动态字典更新 → [加载器说明](./loader/加载器说明.md)
- Spring Boot 自动配置原理、REST 端点使用 → [启动器说明](./starter/启动器说明.md)
- 配置项全解（字典扫描、端点、增强器） → [配置说明](./config/配置说明.md)

如有问题可提交 [Issue](https://github.com/Silwings-git/dict-i18n/issues) 或联系维护者。