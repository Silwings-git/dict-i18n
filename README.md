# Dict-I18n

[中文](./docs/zh/README.zh-CN.md) | [English](README.md)

[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/Silwings-git/dict-i18n)

`dict-i18n` is a solution for internationalizing (i18n) domain data codes (such as order statuses, product types, etc.)
based on dictionaries. It deeply integrates with the Spring Boot ecosystem, supports loading dictionaries from multiple
data sources, automatically injects localized text into Java objects and REST responses, simplifies the management of
multilingual descriptions, and is suitable for scenarios with fixed or infrequently changing coded data.

## 🌟 Core Values

Focuses on addressing the pain points of **internationalizing domain-coded data**, filling the gap in coding mapping
scenarios for traditional resource bundles:

- **Unified Dictionary Management**: Replaces hard-coded multilingual descriptions, standardizes dictionary definitions
  through enums/Java Beans, and supports loading from files, databases, Redis, etc.
- **Automatic Internationalization Enhancement**: Leverages the `@DictDesc` annotation to automatically inject localized
  text corresponding to codes into JSON responses, eliminating the need for manual mapping handling.
- **Flexible Extension and Adaptation**: Modular design (core, loaders, auto-configuration, etc.) supports custom
  loaders and language providers for complex business needs.
- **Strict Validation Assurance**: Validates the uniqueness of dictionary names at startup, supports cycle detection
  during recursive object scanning, and avoids runtime conflicts.

## 📚 Documentation Navigation

| Category                   | Description                                                             | Document Link                                                                   |  
|----------------------------|-------------------------------------------------------------------------|---------------------------------------------------------------------------------|  
| Feature Introduction       | Core features of the framework                                          | [Home.md](./docs/en/Home.md)                                                    |  
| Quick Start                | Core capabilities and minimal integration process                       | [Quick Start.md](./docs/en/guide/Quick_Start.md)                                |  
| Loader Architecture        | Design and extension mechanisms of multi-source loaders                 | [Loader Instructions.md](./docs/en/loader/Loader_Instructions.md)               |  
| Auto-Configuration Details | Starter auto-assembly process and configuration items                   | [Starter Instructions.md](./docs/en/starter/Starter_Instructions.md)            |  
| Configuration Guide        | Configuration items for dictionary scanning, endpoints, enhancers, etc. | [Configuration Instructions.md](./docs/en/config/Configuration_Instructions.md) |  
| Frequently Asked Questions | Typical issues and solutions during integration and use                 | [FAQs.md](./docs/en/faq/FAQs.md)                                                |  

## ✨ Core Features

### 1. Flexible Dictionary Definition

Supports defining dictionaries by **implementing the `Dict` interface with enums/Java Beans**, covering scenarios such
as static coding and dynamic loading:

```java  
// Enum example  
public enum OrderStatus implements Dict {
  PENDING("pending"),
  COMPLETED("completed");

  private final String code;

  @Override
  public String dictName() {
    // Unique identifier for the dictionary  
    return "order_status";
  }

  @Override
  public String code() {
    return this.code;
  }
}  
```  

### 2. Multi-source Loading and Priority

Built-in loaders for **files (YAML/Properties), databases, Redis, and declarative** sources. Supports configuring
loading priority via `loader-order`:

```yaml  
dict-i18n:
  loader-order:
    - redis  # Redis loader takes precedence  
    - sql    # Database loader comes next  
    - file   # File loader as a fallback  
```  

### 3. Automatic Response Enhancement

Use the `@DictDesc` annotation in DTOs to mark fields that need description injection, specifying the associated
dictionary enum:

```java  
public class OrderDTO {
  private String status;

  // Automatically inject the internationalized description corresponding to status  
  @DictDesc(OrderStatus.class)
  private String statusDesc;
}

// Response example  
{
        "status":"pending",
        "statusDesc":"Pending" // Or return the corresponding language description based on language configuration  
        }  
```  

### 4. Strict Startup Validation

Automatically validates the **uniqueness of dictionary names** at startup to avoid conflicts caused by multiple enums
reusing the same dictionary name:

```java  
// Conflict example (triggers an error at startup)  
public enum OrderState implements Dict {
  PENDING("pending");

  @Override
  public String dictName() {
    return "order_status"; // Conflicts with the dictionary name of OrderStatus  
  }
}  
```  

## 🚀 Quick Start

### 1. Add Dependencies

Add `dict-i18n-spring-boot-starter` to `pom.xml` (automatically integrates core, loaders, and other modules):

```xml  

<dependency>
    <groupId>cn.silwings.dicti18n</groupId>
    <artifactId>dict-i18n-spring-boot-starter</artifactId>
    <version>1.0.1</version>
</dependency>

<!-- Add loaders as needed (at least one) -->
<!-- Static declaration loader -->
<dependency>
    <groupId>cn.silwings.dicti18n</groupId>
    <artifactId>dict-i18n-loader-declared</artifactId>
    <version>1.0.1</version>
</dependency>

<!-- Redis loader -->
<dependency>
    <groupId>cn.silwings.dicti18n</groupId>
    <artifactId>dict-i18n-loader-redis</artifactId>
    <version>1.0.1</version>
</dependency>

<!-- Database loader -->
<dependency>
    <groupId>cn.silwings.dicti18n</groupId>
    <artifactId>dict-i18n-loader-sql</artifactId>
    <version>1.0.1</version>
</dependency>

<!-- File loader (YAML/Properties) -->
<dependency>
    <groupId>cn.silwings.dicti18n</groupId>
    <artifactId>dict-i18n-loader-file</artifactId>
    <version>1.0.1</version>
</dependency>  
```  

### 2. Define Dictionary Enums

Implement the `Dict` interface with an enum, declaring the **dictionary name (`dictName`)** and **code (`code`)**:

```java  
public enum OrderStatus implements Dict {
    PENDING("pending"),
    COMPLETED("completed");

    private final String code;

    @Override
    public String dictName() {
      return "order_status"; // Unique identifier for the dictionary, associated with multilingual configurations  
    }

    @Override
    public String code() {
        return this.code;
    }

  // Constructor and Getters are omitted...  
}  
```  

### 3. Enable Internationalization Enhancement

Mark fields in DTOs that need description injection with `@DictDesc`, specifying the associated dictionary enum:

```java  
public class OrderDTO {
    private String status;

  // Automatically inject the internationalized description corresponding to status  
    @DictDesc(OrderStatus.class)
    private String statusDesc;

  // Getters/Setters are omitted...  
}  
```  

### 4. Configure Dictionary Sources (Taking File Loader as an Example)

Optionally configure loading priority in `application.yml`:

```yaml  
dict-i18n:
  loader-order:
    - file  # File loader takes precedence  

# Dictionary content example (dict-i18n/order_status.yml)  
order_status:
  pending: Pending
  completed: Completed  
```  

### 5. Verify the Result

Return `OrderDTO` via a Controller, and the JSON response will automatically inject the localized description
corresponding to the code:

```json  
{
  "status": "pending",
  "statusDesc": "Pending"
}  
```  

## 📦 Module Structure

| Module Name                        | Description                                                                               |  
|------------------------------------|-------------------------------------------------------------------------------------------|  
| `dict-i18n-core`                   | Core interfaces (`Dict`/`Loader`), processors, and basic logic                            |  
| `dict-i18n-loader`                 | Implementations of multi-source loaders (files, databases, Redis, etc.)                   |  
| `dict-i18n-generator-maven-plugin` | Maven plugin, supports dictionary file generation and synchronization                     |  
| `dict-i18n-spring-boot-starter`    | Spring Boot auto-configuration, REST endpoints, and response enhancement                  |  
| `dict-i18n-demo`                   | Sample project covering enum definition, multi-source loading, response enhancement, etc. |  

## 🔗 More Details

The complete documentation can be found in the project’s `docs` directory:

- Loader extension (custom data sources), dynamic dictionary
  updates → [Loader Instructions](./docs/en/loader/Loader_Instructions.md)
- Spring Boot auto-configuration principles, using REST
  endpoints → [Starter Instructions](./docs/en/starter/Starter_Instructions.md)
- Complete configuration items (dictionary scanning, endpoints,
  enhancers) → [Configuration Instructions](./docs/en/config/Configuration_Instructions.md)

For issues, submit an [Issue](https://github.com/Silwings-git/dict-i18n/issues) or contact the maintainers.