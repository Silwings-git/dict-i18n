# ⚙️ Dict-I18n Configuration Documentation

This document provides a detailed explanation of all configurable YAML settings for `dict-i18n`, intended for
integration into a Spring Boot project's `application.yml` or `application.properties`.

## 📌 Top-Level Configuration Structure

```yaml  
dict-i18n:
  loader-order:               # Priority order of loaders  
  max-nesting-depth:          # Maximum nesting depth for resolution  
  return-key-if-empty:        # Whether to return the dictionary key if the description is empty  
  default-lang:               # Default language  
  loader:                     # Configurations for each loader  
  starter:                    # Starter-related configurations  
```  

## 🔄 `loader-order` — Loader Priority

**Type**: `List<String>`  
**Description**: Specifies the loading order of dictionary loaders (higher priority for earlier loaders).  
**Example**:

```yaml  
loader-order:
  - redis
  - sql
  - file
  - declared  
```  

## 🔍 `max-nesting-depth` — Maximum Nesting Depth

**Type**: `int`  
**Default**: `10`  
**Description**: The maximum recursive depth for resolving nested dictionary fields in objects (prevents infinite
loops).

## 🔁 `return-key-if-empty` — Return Dictionary Key (When Description Is Empty)

**Type**: `boolean`  
**Default**: `true`  
**Description**: Whether to return the dictionary key itself as a fallback if no translation description is found.

## 🌐 `default-lang` — Default Language

**Type**: `String`  
**Default**: `""`  
**Description**: The default language to use if the requested language has no corresponding translation. The default
value corresponds to the `dict.yml` file.

## 🧩 `loader` — Configurations for Each Loader

Supported loaders: `declared` (code-declared), `file` (file-based), `sql` (database-based), `redis` (cache-based).

### 1️⃣ `declared` — Code-Declared Loader

| Configuration   | Type         | Default | Description                     |  
|-----------------|--------------|---------|---------------------------------|  
| `enabled`       | boolean      | `true`  | Whether to enable this loader   |  
| `ignore-case`   | boolean      | `true`  | Whether to ignore case for keys |  
| `scan-packages` | List<String> | `[]`    | Base packages to scan           |  

### 2️⃣ `file` — File-Based Loader

| Configuration       | Type         | Default                                                                                                      | Description                     |  
|---------------------|--------------|--------------------------------------------------------------------------------------------------------------|---------------------------------|  
| `enabled`           | boolean      | `true`                                                                                                       | Whether to enable this loader   |  
| `ignore-case`       | boolean      | `true`                                                                                                       | Whether to ignore case for keys |  
| `location-patterns` | List<String> | `["dict_i18n/dict_*.yml", "dict_i18n/dict_*.properties", "dict_i18n/dict.yml", "dict_i18n/dict.properties"]` | Paths to translation files      |  

### 3️⃣ `redis` — Redis-Based Loader

| Configuration             | Type    | Default                                                                                                      | Description                                                |  
|---------------------------|---------|--------------------------------------------------------------------------------------------------------------|------------------------------------------------------------|  
| `enabled`                 | boolean | `true`                                                                                                       | Whether to enable this loader                              |  
| `ignore-case`             | boolean | `true`                                                                                                       | Whether to ignore case for keys                            |  
| `location-patterns`       | List    | `["dict_i18n/dict_*.yml", "dict_i18n/dict_*.properties", "dict_i18n/dict.yml", "dict_i18n/dict.properties"]` | Paths to translation files                                 |  
| `key-prefix`              | string  | `dict_i18n`                                                                                                  | Key prefix used in Redis                                   |  
| `preload`                 | object  | [preload](#redis-preload)                                                                                    | Preload configurations                                     |  
| `error-handling-strategy` | enum    | `FAIL`                                                                                                       | Error handling strategy: `FAIL` (throw) or `IGNORE` (skip) |  

#### 🔄 Redis Preload (`preload`)

<a id="redis-preload"></a>

| Configuration  | Type    | Default       | Description                                                |  
|----------------|---------|---------------|------------------------------------------------------------|  
| `enabled`      | boolean | `true`        | Whether to preload data from files to Redis on startup     |  
| `fail-fast`    | boolean | `true`        | Whether to fail quickly on preload failures                |  
| `preload-mode` | enum    | `INCREMENTAL` | Preload mode: `INCREMENTAL` (partial) or `FULL` (complete) |  

### 4️⃣ `sql` — Database-Based Loader

| Configuration             | Type         | Default                                                                                                      | Description                                 |  
|---------------------------|--------------|--------------------------------------------------------------------------------------------------------------|---------------------------------------------|  
| `enabled`                 | boolean      | `true`                                                                                                       | Whether to enable this loader               |  
| `ignore-case`             | boolean      | `true`                                                                                                       | Whether to ignore case for keys             |  
| `location-patterns`       | List<String> | `["dict_i18n/dict_*.yml", "dict_i18n/dict_*.properties", "dict_i18n/dict.yml", "dict_i18n/dict.properties"]` | Paths to translation files                  |  
| `preload`                 | object       | [preload](#sql-preload)                                                                                      | Preload configurations                      |  
| `schema`                  | object       | [schema](#sql-schema)                                                                                        | Database schema configurations              |  
| `cache`                   | object       | [cache](#sql-cache)                                                                                          | Caching configurations                      |  
| `error-handling-strategy` | enum         | `FAIL`                                                                                                       | Error handling strategy: `FAIL` or `IGNORE` |  

#### 🔄 Database Preload (`preload`)

<a id="sql-preload"></a>

| Configuration  | Type    | Default       | Description                                                   |  
|----------------|---------|---------------|---------------------------------------------------------------|  
| `enabled`      | boolean | `true`        | Whether to preload data from files to the database on startup |  
| `fail-fast`    | boolean | `true`        | Whether to fail quickly on preload failures                   |  
| `preload-mode` | enum    | `INCREMENTAL` | Preload mode: `INCREMENTAL` (partial) or `FULL` (complete)    |  

#### 🗃️ Schema Initialization (`schema`)

<a id="sql-schema"></a>

| Configuration | Type    | Default | Description                           |  
|---------------|---------|---------|---------------------------------------|  
| `enabled`     | boolean | `true`  | Whether to auto-create tables/indexes |  

#### 🧠 Cache Configuration (`cache`)

<a id="sql-cache"></a>

| Configuration                | Type    | Default               | Description                                        |  
|------------------------------|---------|-----------------------|----------------------------------------------------|  
| `enabled`                    | boolean | `true` (since v1.0.2) | Whether to enable local caching for the SQL loader |  
| `maximum-size`               | int     | `1000`                | Maximum number of cache entries                    |  
| `expire-after-write-seconds` | int     | `300`                 | Cache expiration time (seconds) after writing      |  

## 🚀 `starter` — Starter-Related Configurations

### 🔎 `scan-packages` — Package Scanning Paths

**Type**: `List<String>`  
**Description**: Configures package paths to scan for automatically discovering dictionary classes implementing the
`Dict` interface.

### 🌐 `endpoint` — Dictionary Management API Configurations

| Configuration | Type    | Default | Description                                                |  
|---------------|---------|---------|------------------------------------------------------------|  
| `enabled`     | boolean | `true`  | Whether to enable built-in management APIs (Servlet-based) |  

#### Sub-Configuration: `dict-items` API

| Configuration | Type    | Default                 | Description                                |  
|---------------|---------|-------------------------|--------------------------------------------|  
| `enabled`     | boolean | `true`                  | Whether to enable the dictionary items API |  
| `path`        | string  | `/dict-i18n/dict-items` | API endpoint path                          |  

#### Sub-Configuration: `dict-names` API

| Configuration | Type    | Default                 | Description                                |  
|---------------|---------|-------------------------|--------------------------------------------|  
| `enabled`     | boolean | `true`                  | Whether to enable the dictionary names API |  
| `path`        | string  | `/dict-i18n/dict-names` | API endpoint path                          |  

### ✨ `enhancer` — Response Enhancement Configurations

Automatically injects dictionary descriptions into HTTP responses.

| Configuration         | Type         | Description                                                                 |  
|-----------------------|--------------|-----------------------------------------------------------------------------|  
| `enabled`             | boolean      | Whether to enable global response enhancement                               |  
| `include-packages`    | List<String> | Packages to include for enhancement (scans Spring’s main packages if empty) |  
| `exclude-annotations` | List<String> | Annotations (full class names) to exclude from enhancement                  |  

### ✅ `check.unique-dict-name` — Dictionary Name Uniqueness Check

| Configuration | Type    | Default | Description                                             |  
|---------------|---------|---------|---------------------------------------------------------|  
| `enabled`     | boolean | `true`  | Whether to check for unique dictionary names at startup |  

## 📋 Full Configuration Example

```yaml  
dict-i18n:
  loader-order:
    - redis
    - sql
    - file
    - declared
  max-nesting-depth: 10
  return-key-if-empty: true
  default-lang: zh
  loader:
    declared:
      enabled: true
      ignore-case: true
    file:
      enabled: true
      ignore-case: true
    redis:
      enabled: true
      ignore-case: true
      key-prefix: 'dict_i18n'
      error-handling-strategy: FAIL
      preload:
        enabled: false
        fail-fast: true
        preload-mode: INCREMENTAL
    sql:
      enabled: true
      ignore-case: true
      error-handling-strategy: FAIL
      schema:
        enabled: false
      preload:
        enabled: false
        fail-fast: true
        preload-mode: INCREMENTAL
      cache:
        enabled: true
        maximum-size: 1000
        expire-after-write-seconds: 300
  starter:
    scan-packages:
    endpoint:
      enabled: true
      dict-items:
        enabled: true
        path: /dict-i18n/dict-items
      dict-names:
        enabled: true
        path: /dict-i18n/dict-names
    enhancer:
      enabled: true
      include-packages:
        - cn.silwings.dicti18n.demo
        - org.springframework.http
      exclude-annotations:
    check:
      unique-dict-name:
        enabled: true  
```  

| [< Loader Documentation](../loader/Loader_Instructions.md) | [FAQs >](../faq/FAQs.md) |  
|:-----------------------------------------------------------|-------------------------:|