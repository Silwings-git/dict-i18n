# Dict-I18n Documentation

Welcome to **dict-i18n** — an internationalization dictionary enhancement framework! This project provides flexible,
configurable support for multilingual field descriptions, with capabilities to load dictionary entries from multiple
sources such as declarations, files, databases, and Redis.

## 📚 Documentation Table of Contents

- 🚀 [Quick Start](./guide/Quick_Start.md)
- 🔌 [Starter Instructions](./starter/Starter_Instructions.md)
- 🧩 [Loader Instructions](loader/Loader_Instructions.md)
- 🛠 [Configuration Instructions](./config/Configuration_Instructions.md)
- ❓ [FAQs](./faq/FAQs.md)

## ✨ Core Features

This project aims to provide a flexible, high-performance solution for dictionary internationalization in multilingual
systems. Through annotation-driven design, plugin support, and multi-source merging mechanisms, developers can easily
inject localized descriptions into system fields, enhancing the system's globalization capabilities.

* ✅ **Annotation-Based Dictionary Definition**  
  Use the `@DictDesc` annotation to effortlessly add internationalized descriptions to fields, with zero intrusive
  development and cost-free integration.

* 🛠 **Maven Plugin Support**  
  Offers a plugin to automatically generate dictionary template files, initializing multilingual configurations with one
  click—no manual maintenance required.

* 🔌 **Multiple Loaders Supported**  
  Built-in loaders for files, databases, Redis, etc., with support for custom extensions to meet diverse project needs.

* 🔄 **Flexible Loading Order Control**  
  Configure loading priorities to implement multi-source merging and overriding strategies, ensuring data consistency.

* ⚙️ **Seamless Spring Boot Integration**  
  Supports autoconfiguration and field injection for quick integration into existing systems—sensible defaults for
  out-of-the-box usability.

* 🌐 **Comprehensive Multilingual Mechanism**  
  Defaults to language parsing from request headers, with support for custom language resolvers and built-in fallback
  logic to ensure user experience.

* 🔍 **Automatic Recursive Scanning**  
  Automatically identifies annotated definitions in deeply nested fields, improving development efficiency and reducing
  omissions.

* 🚀 **High-Performance Caching**  
  Built-in caching reduces redundant parsing and loading overhead, optimizing system performance.

* 🧩 **Highly Extensible Architecture**  
  Supports extensions for loaders, language context resolvers, response enhancers, and more to adapt to complex business
  scenarios.

## 📞 Contact Us

For issues, please submit an [Issue](https://github.com/Silwings-git/dict-i18n/issues) or contact the maintainers.

[Quick Start >](./guide/Quick_Start.md)