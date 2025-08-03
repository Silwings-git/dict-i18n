# 📘 Dict-I18n Quick Start Guide

Welcome to **`dict-i18n`** — a lightweight, extensible dictionary internationalization framework designed for *
*multilingual descriptions** of dictionary fields in Java applications, such as enumeration values, status codes, and
type identifiers. It automatically scans dictionary definitions, generates resource files during build, and enhances
HTTP responses at runtime.

## ✨ Core Features

* ✅ **Annotation-based notation-based dictionary definition**: Use `@DictDesc` to add internationalized descriptions to
  fields
* 🛠 **Maven plugin for template generation**: Automatically generates multilingual configuration files
* 🔌 **Multiple loader support**: Supports file, database, Redis, and other loading methods
* 🔄 **Flexible loading order**: Configurable priority for multi-source merging and overriding
* ⚙️ **Seamless Spring Boot integration**: Out-of-the-box with automatic field description injection into responses
* 🌐 **Multi-language switching and fallback**: Supports dynamic language switching and fallback logic
* 🔍 **Automatic recursive scanning**: Recognizes annotation definitions even in deep nested structures
* 🚀 **High-performance caching**: Reduces reduced redundant loading and improved runtime efficiency
* 🧩 **Highly extensible**: Supports custom loaders, language context resolvers, and other components

## 🧱 Environment Requirements

| Item        | Requirement                 |  
|-------------|-----------------------------|  
| Java        | JDK 1.8 or higher           |  
| Build Tool  | Maven 3.6+                  |  
| Spring Boot | Spring Boot 2.x recommended |  

> ⚠️ The current version is based on `HttpServletRequest` and **is not compatible with Spring Boot 3's Jakarta Servlet
stack**. If using Spring Boot 3, disable endpoint functionality:

```yaml  
dict-i18n:
  starter:
    endpoint:
      enabled: false  
```  

## 🚀 Quick Integration Guide

### 1️⃣ Add Dependencies

```xml  
<!-- Core starter dependency -->
<dependency>
    <groupId>cn.silwings.dicti18n</groupId>
    <artifactId>dict-i18n-spring-boot-starter</artifactId>
    <version>1.0.3</version>
</dependency>

<!-- Loader dependency: example using file loader -->
<dependency>
    <groupId>cn.silwings.dicti18n</groupId>
    <artifactId>dict-i18n-loader-file</artifactId>
    <version>1.0.3</version>
</dependency>

<!-- Maven plugin: generates internationalization resource files -->
<plugin>
    <groupId>cn.silwings.dicti18n</groupId>
    <artifactId>dict-i18n-generator-maven-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>yml</goal> <!-- or use <goal>properties</goal> -->
            </goals>
        </execution>
    </executions>
    <configuration>
        <languages>
            <!-- Configure required languages (can use region-free formats like zh, en) -->
            <language>zh-CN</language>
            <language>en-US</language>
        </languages>
        <verbose>true</verbose> <!-- Output detailed logs -->
    </configuration>
</plugin>  
```  

### 2️⃣ Define Dictionary Enumeration Class

```java  
public enum OrderStatus implements Dict {
    PENDING, PAID, SHIPPED, COMPLETED, CANCELED;

    @Override
    public String dictName() {
        return "order_status";
    }

    @Override
    public String code() {
        return this.name();
    }
}  
```  

### 3️⃣ Generate Internationalization Resource Files

Run in the project root directory:

```bash  
mvn compile  
```  

By default, the following templates will be generated in `resources/dict_i18n/`:

**dict\_i18n/dict\_zh-CN.yml**

```yaml  
order_status:
  CANCELED: ''
  COMPLETED: ''
  PAID: ''
  PENDING: ''
  SHIPPED: ''  
```  

Edit and populate description content:

```yaml  
order_status:
  CANCELED: 已取消
  COMPLETED: 已完成
  PAID: 已支付
  PENDING: 待处理
  SHIPPED: 已发货  
```  

### 4️⃣ Automatically Enhance HTTP responses Response

#### Example Entity Class and Controller:

```java  
public class OrderVO {
    private String
    private String orderStatus;

    @DictDesc(OrderStatus.class)
    private String orderStatusDesc;
    // getter & setter ...  
}

@RestController
public class OrderController {
    @GetMapping("/order")
    public ResponseEntity<OrderVO> getOrder() {
        final OrderVO vo = new OrderVO();
        vo.setOrderStatus(OrderStatus.COMPleted.code());
        return ResponseEntity.ok(vo);
    }
}  
```  

#### Example Response (with request header: Accept-Language: zh-CN)

```json  
{
  "orderStatus": "COMPLETED",
  "orderStatusDesc": "已完成"
}  
```  

## 🧩 Supported Loader Types

| Type       | Description                  | artifactId                  |  
|------------|------------------------------|-----------------------------|  
| `declared` | Dynamically loaded from code | `dict-i18n-loader-declared` |  
| `file`     | Loaded from resource files   | `dict-i18n-loader-file`     |  
| `sql`      | Loaded from database tables  | `dict-i18n-loader-sql`      |  
| `redis`    | Loaded from Redis            | `dict-i18n-loader-redis`    |  

> 🛠 You can also implement the `DictI18nLoader` interface and register it as a Bean to connect custom data sources.

## ⏭️ Next Steps

Now that you've completed the basic setup, you can:

- Explore other loader options like Redis or SQL
- Configure advanced options such as custom language providers
- Check the demo module for more comprehensive examples

## 🧪 Example Project

View the demo project: [dict-i18n-demo](../../../dict-i18n-demo)

## 📚 Further Reading

- 🔌 [Starter Instructions](../starter/Starter_Instructions.md)
- 🧩 [Loader Instructions](../loader/Loader_Instructions.md)
- 🛠 [Configuration Instructions](../config/Configuration_Instructions.md)
- ❓ [FAQs](../faq/FAQs.md)

| [< Documentation Home](../Home.md) | [Starter Instructions >](../starter/Starter_Instructions.md) |  
|:-----------------------------------|-------------------------------------------------------------:|