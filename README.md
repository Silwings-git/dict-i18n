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
  #  Specify the priority of the Loader name, for example: ["declared", "file", "redis", "sql"]
  loader-order:
    - redis
    - sql
    - file
    - declared
  # The maximum nesting depth when looking up a field
  max-nesting-depth: 10
  # Whether to return dictKey when description is empty
  return-key-if-empty: true
  # The default language to use when a translation is not found
  default-lang: zh
  loader:
    declared:
      # Whether to enable the declared dict loader
      enabled: true
      # Whether to ignore the dict case
      ignore-case: true
    file:
      # Whether to enable the file dict loader
      enabled: true
      # Whether to ignore the dict case
      ignore-case: true
    redis:
      # Whether to enable the redis dict loader
      enabled: true
      # Whether to ignore the dict case
      ignore-case: true
      # the prefix of the key in the dict cache
      key-prefix: 'dict_i18n'
      # Error handling strategy when unexpected exceptions occur
      error-handling-strategy: FAIL
      preload:
        # Whether to load dict data from the resource file into Redis on startup.
        enabled: false
        # Whether to fail fast when loading dict data into Redis.
        fail-fast: true
        # Load mode when preloading to Redis.
        preload-mode: INCREMENTAL
    sql:
      # Whether to enable the sql dict loader
      enabled: true
      # Whether to ignore the dict case
      ignore-case: true
      # Error handling strategy when unexpected exceptions occur
      error-handling-strategy: FAIL
      schema:
        # Whether to enable schema initialization (create tables + create indexes)
        enabled: false
      preload:
        # Whether to load dict data from the resource file into database on startup.
        enabled: false
        # Whether to fail fast when loading dict data into database.
        fail-fast: true
        # Load mode when preloading to database.
        preload-mode: INCREMENTAL
      cache:
        # Whether to enable the sql dict loader cache
        enabled: false
        # Maximum number of cache items
        maximum-size: 1000
        # Cache expiration time (unit: seconds）
        expire-after-write-seconds: 300
  starter:
    # A list of package paths to scan to find the {@link cn.silwings.dicti18n.dict.Dict} implementation class.
    scan-packages:
    endpoint:
      # Whether to enable endpoint
      enabled: true
      dict-items:
        # Whether to enable dict items endpoint
        enabled: true
        # The path for the dict items endpoint.
        path: /dict-i18n/dict-items
      dict-names:
        # Whether to enable dict items endpoint
        enabled: true
        # he path for the dict names endpoint.
        path: /dict-i18n/dict-names
    enhancer:
      # Whether to enable global response enhancement.
      enabled: true
      # List of package names. Only responses with return types in these packages will be enhanced.If not specified, defaults to the Spring component scanning base packages.
      include-packages:
        - cn.silwings.dicti18n.demo
        - org.springframework.http
      # Fully qualified names of annotations.If a class or method is annotated with any of these, it will be excluded from enhancement.
      exclude-annotations:
    check:
      unique-dict-name:
        # Check if the dictionary name is unique at startup.
        enabled: true
```