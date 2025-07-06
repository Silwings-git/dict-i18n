# dict-i18n-spring-boot-starter

A Spring Boot starter for dictionary-based internationalization (i18n) of domain data codes such as order statuses,
product types, etc. This starter simplifies managing multi-language descriptions for fixed or rarely changing code data
by automatically injecting localized text into Java objects, REST responses, and import/export scenarios.

## Features

* Support for dictionary definitions as enums or JavaBeans implementing a `Dict` interface.
* Flexible loading of dictionary data from YAML, database, Redis, or custom loaders.
* Auto-injection of localized descriptions into response bodies via Spring MVC `ResponseBodyAdvice`.
* Recursive object scanning with cycle detection and configurable max recursion depth.
* Support for multi-language keys with fallback and customizable loader priority.
* Tools for exporting dictionary enums to YAML for easier configuration.
* Starter modularized into core, loader, and auto-configuration modules to facilitate extensibility.
* Strict dictionary name uniqueness checking on startup to avoid conflicts.

## Why Use This Starter?

Many enterprise applications require internationalized display of coded data like order statuses or product categories.
While resource bundles manage UI text, dictionary codes often lack automated mapping to localized descriptions. This
starter fills that gap with an easy-to-use, extensible solution fully integrated into the Spring Boot ecosystem.

## Quick Start

Add the starter dependency to your Spring Boot project and define your dictionaries as enums implementing `Dict`.
Configure loader priorities and language preferences via application properties. The system automatically populates
description fields annotated with `@DictDesc` during JSON response serialization.

```java
public enum OrderStatus implements Dict {
    PENDING("pending"),
    COMPLETED("completed");

    private final String code;

    @Override
    public String dictName() {
        return "order_status";
    }

    @Override
    public String code() {
        return this.code;
    }

    // constructor, getters ...
}
```

```java
public class OrderDTO {
    private String status;
    @DictDesc(OrderStatus.class)
    private String statusDesc;
    // getters/setters ...
}
```

```yaml
order_status:
  pending: Pending
  completed: Completed
```

```json
{
  "status": "pending",
  "statusDesc": "Pending"
}
```

## Modules

* **dict-i18n-core**: Core interfaces, properties, and processor logic.
* **dict-i18n-loader-xxx**: Dictionary data loaders (YAML, DB, Redis).
* **dict-i18n-spring-boot-starter**: Spring Boot autoconfiguration and integration.
* **dict-i18n-demo**: Sample project demonstrating typical usage.

## Configuration

```yaml
dict-i18n:
  # Specify the priority of the Loader name, for example: ["redis", "mysql", "yaml"]
  loader-order:
    - yml
  # The maximum recursion depth when looking up a field
  max-recursion-depth: 10
  # The default language to use when a translation is not found
  default-lang: zh-cn
  check:
    # Specifies whether to enable the uniqueness check of dictName.
    enable-dict-name-unique-check: true
    # A list of package paths to scan to find the cn.silwings.dicti18n.dict.Dict implementation class.
    scan-packages:
      - cn.silwings.dicti18n.demo
  response-enhancer:
    # Whether to enable global response enhancements
    enabled: true
    # The package to which the included return class belongs (return type)
    include-packages:
      - cn.silwings.dicti18n.demo
    # Specifies which annotation classes or methods do not need to be enhanced
    exclude-annotations:
      - org.springframework.web.bind.annotation.GetMapping
      - org.springframework.web.bind.annotation.PostMapping
  loader:
    yml:
      # Specify the resource path, and support Spring Resource path formats such as classpath: file:.
      location-pattern: classpath:dict/dict_*.yml
```