# ❓ Frequently Asked Questions

### Q1: No translation description is retrieved?

* Verify that the language in the request header (`Accept-Language`) matches the resource files (e.g., `en-US` vs
  `en-GB`)
* Check if dictionary resource files exist, have the correct format, and contain content
* Ensure the `@DictDesc` annotation is used correctly
* Confirm that response enhancement is enabled: `dict-i18n.starter.enhancer`

### Q2: How to ignore regions and identify only by language?

* Use filenames like `dict_zh.yml` — the framework will automatically fall back to broader matches (e.g., zh-CN → zh)

### Q3: No internationalization files are generated?

* Run the `mvn compile` command
* Check the `languages` configuration of the Maven plugin
* Ensure at least one enum class implementing `Dict` is defined

### Q4: Want to integrate a custom data source?

* Implement the `DictI18nLoader` interface and register it as a Spring Bean

### Q5: How to customize the language source instead of using the default `Accept-Language`?

* Implement the `LanguageProvider` interface to replace the default language resolution logic

| [< Configuration Guide](../config/Configuration_Instructions.md) | [Documentation Home >](../Home.md) |  
|:-----------------------------------------------------------------|-----------------------------------:|