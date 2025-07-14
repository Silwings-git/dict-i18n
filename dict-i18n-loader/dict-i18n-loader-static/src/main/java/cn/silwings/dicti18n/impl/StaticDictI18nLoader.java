package cn.silwings.dicti18n.impl;


import cn.silwings.dicti18n.config.DictI18nStaticProperties;
import cn.silwings.dicti18n.dict.Dict;
import cn.silwings.dicti18n.loader.DictI18nLoader;
import cn.silwings.dicti18n.loader.scan.DictScanner;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class StaticDictI18nLoader implements DictI18nLoader, ApplicationContextAware, ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(StaticDictI18nLoader.class);
    private final DictI18nStaticProperties dictI18nStaticProperties;
    private ApplicationContext applicationContext;

    public StaticDictI18nLoader(final DictI18nStaticProperties dictI18nStaticProperties) {
        this.dictI18nStaticProperties = dictI18nStaticProperties;
    }

    @Override
    public String loaderName() {
        return "static";
    }

    private final Map<String, String> dictData = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(final ApplicationArguments args) {
        this.init();
    }

    public void init() {
        // Determine base packages to scan
        final List<String> basePackages = this.getScanPackages();

        new DictScanner()
                .scan(basePackages)
                .stream()
                .filter(clazz -> clazz.isAssignableFrom(Dict.class))
                .forEach(clazz -> {
                    if (clazz.isEnum()) {
                        final Dict[] enumConstants = clazz.getEnumConstants();
                        if (ArrayUtils.isNotEmpty(enumConstants)) {
                            for (final Dict constant : enumConstants) {
                                this.dictData.put(this.processKey(String.format("%s.%s", constant.dictName(), constant.code())), this.processValue(constant));
                            }
                        }
                    } else {
                        try {
                            final Constructor<?> constructor = clazz.getDeclaredConstructor();
                            constructor.setAccessible(true);
                            final Dict instance = (Dict) constructor.newInstance();
                            this.dictData.put(this.processKey(String.format("%s.%s", instance.dictName(), instance.code())), this.processValue(instance));
                        } catch (Exception e) {
                            throw new IllegalStateException("JavaBean Dict class must have a no-arg constructor: " + clazz.getName(), e);
                        }
                    }
                });
        log.info("{} Dict instances have been loaded: {}", this.dictData.size(), this.dictData.keySet().stream().collect(Collectors.joining(",")));
    }

    private String processKey(final String key) {
        if (null == key || key.isEmpty()) {
            return key;
        }
        return this.dictI18nStaticProperties.isIgnoreCase() ? key.toLowerCase() : key;
    }

    private String processValue(final Dict dict) {
        // TODO_Silwings: 2025/7/15 处理dict的静态描述
        return null;
    }

    private List<String> getScanPackages() {
        return this.dictI18nStaticProperties.getScanPackages().isEmpty()
                ? AutoConfigurationPackages.get(this.applicationContext)
                : this.dictI18nStaticProperties.getScanPackages();
    }

    @Override
    public Optional<String> get(final String lang, final String key) {
        // Static dictionaries are language-incentive
        return Optional.ofNullable(this.dictData.get(this.processKey(key)));
    }
}