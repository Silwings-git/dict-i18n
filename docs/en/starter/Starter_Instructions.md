# 🔌 Dict-I18n Starter (Spring Boot Starter) Instructions

## 📘 Overview

`dict-i18n` provides a dedicated Spring Boot Starter that simplifies framework integration in Spring Boot applications
through autoconfiguration. It centers around the `DictI18nAutoConfiguration` class, which automatically configures all
necessary beans when the starter is included, eliminating the need for manual initialization code.

The Starter implements core functionalities such as automatic loader registration, dictionary scanning, and REST
endpoint exposure. It supports flexible behavior adjustments through configuration, fully aligning with Spring Boot's "
convention over configuration" philosophy.

## 🔧 Auto-Configuration Architecture

### Core Auto-Configuration Class

The core of autoconfiguration is `DictI18nAutoConfiguration`, which handles:

- Registering key beans based on configuration conditions
- Coordinating initialization of components like loader sorting and dictionary scanning
- Registering REST endpoint handlers
- Integrating extension points such as language providers

### Auto-Configuration Bean Dependencies

The Starter dynamically registers beans using conditional annotations (`@Conditional`) based on configuration, ensuring
components load on demand. Key beans and their dependencies:

| Bean Name                  | Activation Condition                                    | Function                                                                     |  
|----------------------------|---------------------------------------------------------|------------------------------------------------------------------------------|  
| `DictLoaderConfigSorter`   | `dict-i18n.loader-order[0]` exists                      | Sorts loaders by priority based on configuration                             |  
| `SpringDictLoaderSorter`   | No existing `DictLoaderSorter` bean                     | Provides default loader sorting implementation                               |  
| `UniqueDictNameChecker`    | `dict-i18n.starter.check.unique-dict-name.enabled=true` | Verifies uniqueness of dictionary names to prevent conflicts                 |  
| `DictMapHolder`            | `dict-i18n.starter.endpoint.enabled=true`               | Acts as a central registry for dictionary enums, supporting endpoint queries |  
| `DictItemsEndpointHandler` | `dict-i18n.starter.endpoint.dict-items.enabled=true`    | Handles REST endpoint for dictionary item queries                            |  
| `DictNamesEndpointHandler` | `dict-i18n.starter.endpoint.dict-names.enabled=true`    | Handles REST endpoint for dictionary name queries                            |  

## ⚙️ Configuration Properties

The Starter offers extensive configuration options, all prefixed with `dict-i18n.starter`, configurable in
`application.yml` or `application.properties`.

### Core Configuration

```yaml  
dict-i18n:  
  starter:  
    scan-packages: [ ]  # Package paths to scan for Dict implementations  
    check:  
      unique-dict-name:  
        enabled: true  # Enable uniqueness check for dictionary names  
    endpoint:  
      enabled: true  # Enable REST endpoints  
      dict-items:  
        enabled: true  # Enable dictionary items endpoint  
        path: /dict-i18n/dict-items  # Path for dictionary items endpoint  
      dict-names:  
        enabled: true  # Enable dictionary names endpoint  
        path: /dict-i18n/dict-names  # Path for dictionary names endpoint  
    enhancer:  
      enabled: true  # Enable response enhancement  
      include-packages: [ ]  # Package paths to include for enhancement  
      exclude-annotations: [ ]  # Annotations to exclude from enhancement  
```  

### Configuration Details

| Property Path                    | Type           | Default Value           | Description                                                                    |  
|----------------------------------|----------------|-------------------------|--------------------------------------------------------------------------------|  
| `scan-packages`                  | `List<String>` | Empty list              | Specifies package paths to scan for `Dict` interface implementations           |  
| `check.unique-dict-name.enabled` | `boolean`      | `true`                  | Whether to validate uniqueness of all dictionary names at startup              |  
| `endpoint.enabled`               | `boolean`      | `true`                  | Whether to enable REST endpoint functionality                                  |  
| `endpoint.dict-names.enabled`    | `boolean`      | `true`                  | Whether to enable the dictionary names query endpoint                          |  
| `endpoint.dict-names.path`       | `String`       | `/dict-i18n/dict-names` | Access path for the dictionary names endpoint                                  |  
| `endpoint.dict-items.enabled`    | `boolean`      | `true`                  | Whether to enable the dictionary items query endpoint                          |  
| `endpoint.dict-items.path`       | `String`       | `/dict-i18n/dict-items` | Access path for the dictionary items endpoint                                  |  
| `enhancer.enabled`               | `boolean`      | `true`                  | Whether to enable response enhancement (auto-replaces codes with descriptions) |  
| `enhancer.include-packages`      | `List<String>` | []                      | Package paths to include for response enhancement (applies to all if empty)    |  
| `enhancer.exclude-annotations`   | `List<String>` | []                      | Annotations to exclude from enhancement (classes with these are not enhanced)  |  

## 🌐 REST Endpoints

The Starter includes two built-in REST endpoints for programmatic dictionary queries, facilitating frontend or service
access to internationalization data.

**Note**: REST endpoints only handle enum-type `Dict` implementations!

### Dict Names Endpoint

#### Function

Queries all available dictionary names in the system.

#### Response Format

```json  
[  
  "order_status",  
  "product_type"  
]  
```  

### Dict Items Endpoint

Path: `/dict-i18n/dict-items`

#### Function

Queries all items of specified dictionaries and their translated texts.

#### Request Parameters

- `dictNames`: Required, comma-separated list of dictionary names (e.g., `order_status,product_type`)
- `language`: Optional, language code (e.g., `zh-cn`, `en`), defaults to the current context language

#### Response Format

```json  
{
  "items": {
    "order_status": [
      {
        "code": "pending",
        "desc": "待处理"
      },
      {
        "code": "completed",
        "desc": "已完成"
      }
    ],
    "product_type": [
      {
        "code": "physical",
        "desc": "实物商品"
      },
      {
        "code": "digital",
        "desc": "数字商品"
      }
    ]
  }
}  
```  

### Endpoint Registration Mechanism

Endpoints are registered as Spring MVC handlers via `SimpleUrlHandlerMapping`. Core code:

```java  
private HandlerMapping buildHandlerMapping(final String path, final HttpRequestHandler handler) {
    final Map<String, HttpRequestHandler> urlMap = new HashMap<>();
    urlMap.put(path, handler);
    final SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
    mapping.setUrlMap(urlMap);
    mapping.setOrder(0);
    return mapping;
}  
```  

This mechanism ensures endpoints can respond directly to HTTP requests without additional controller configuration.

## 🧩 Core Integration Components

### DictMapHolder

#### Function

Acts as a central registry for dictionary enums, collecting all enum implementations of the `Dict` interface at
application startup and providing query capabilities.

#### Core Responsibilities

- Scans specified package paths for enum implementations of the `Dict` interface
- Stores mappings between dictionary names and enum constants using a thread-safe `ConcurrentHashMap<String, Dict[]>`
- Provides dictionary data query interfaces for REST endpoints
- Implements the `ApplicationRunner` interface to initialize after the Spring context loads

#### Initialization Process

1. Determines scan paths based on configuration (prioritizes `scan-packages`, defaults to auto-configured packages)
2. Finds all enum classes implementing `Dict` via `DictScanner`
3. Registers enum constants in a mapping table using names returned by the `dictName()` method

### UniqueDictNameChecker

#### Function

Validates the uniqueness of all dictionary names at application startup to prevent runtime conflicts from duplicate
names across enums.

#### Validation Logic

1. Scans all `Dict` implementations using `DictScanner`
2. Collects dictionary names from all enum constants
3. Throws an exception immediately (fail-fast) if duplicates are found

### LanguageProvider Integration

The Starter provides a default `DefaultLanguageProvider` implementation to retrieve language information for the current
context. To customize language detection logic (e.g., from request headers or user settings), implement the
`LanguageProvider` interface and register it as a Spring Bean to override the default:

```java  

@Bean
@ConditionalOnMissingBean(LanguageProvider.class)
public LanguageProviderProvider defaultLangProvider() {
    return new DefaultLanguageProvider();
}  
```  

## 🔄 Startup Sequence Sequence

### Application Startup Flow

1. Spring Boot application starts, triggering `DictI18nAutoConfiguration`
2. Registers necessary beans conditionally (loader sorters, endpoint handlers, etc.)
3. `DictMapHolder` scans after Spring context initialization
4. Scans specified packages for `Dict` implementations and registers them in the mapping table
5. `UniqueDictNameChecker` validates dictionary name uniqueness
6. REST endpoints register with Spring MVC via `SimpleUrlHandlerMapping`
7. Application startup completes, with all components ready

### Scan Package Resolution Logic

The framework determines scan paths in this priority order:

1. Uses `dict-i18n.starter.scan-packages` configuration if specified
2. Defaults to Spring Boot's autoconfigured packages (`AutoConfigurationPackages.get(applicationContext)`) if no
   configuration

Core code:

```java  
private List<String> getScanPackages() {
    return this.dictI18nStarterProperties.getScanPackages().isEmpty()
            ? AutoConfigurationPackages.get(this.applicationContext)
            : this.dictI18nStarterProperties.getScanPackages();
}  
```  

## 🛡️ Error Handling

The Starter includes multiple safeguards for system stability:

- **Missing Dictionary Handling**: Returns empty arrays for non-existent dictionaries to avoid null pointer exceptions
- **Invalid Configuration Handling**: Automatically skips invalidly configured components via conditional bean
  registration
- **Startup Failure Handling**: `UniqueDictNameChecker` fails fast on duplicate names to prevent runtime errors
- **Endpoint Security**: Endpoints are enabled by default but can be disabled via configuration to prevent unauthorized
  access

| [< Quick Start](../guide/Quick_Start.md) | [Loader Instructions >](../loader/Loader_Instructions.md) |  
|:-----------------------------------------|----------------------------------------------------------:|