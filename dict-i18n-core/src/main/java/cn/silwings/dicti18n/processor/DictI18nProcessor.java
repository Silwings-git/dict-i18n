package cn.silwings.dicti18n.processor;

import cn.silwings.dicti18n.annotation.DictDesc;
import cn.silwings.dicti18n.annotation.DictModel;
import cn.silwings.dicti18n.config.DictI18nProperties;
import cn.silwings.dicti18n.dict.Dict;
import cn.silwings.dicti18n.provider.DictI18nProvider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DictI18nProcessor {

    private static final Logger log = LoggerFactory.getLogger(DictI18nProcessor.class);

    private final DictI18nProvider i18nProvider;
    // Whether the cache type should be handled to improve performance
    private final Map<Class<?>, Boolean> processableCache = new ConcurrentHashMap<>();
    private final DictI18nProperties dictI18nProperties;

    public DictI18nProcessor(final DictI18nProvider i18nProvider, final DictI18nProperties dictI18nProperties) {
        this.i18nProvider = i18nProvider;
        this.dictI18nProperties = dictI18nProperties;
    }

    public int getMaxRecursionDepth() {
        return this.dictI18nProperties.getMaxRecursionDepth();
    }

    public void process(final Object body, final String language) {

        if (Objects.isNull(body)) {
            return;
        }

        // IdentityHashMap is used to ensure that objects are judged by address and to prevent false positives caused by overriding equals().
        final Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());

        if (body instanceof Collection<?>) {
            ((Collection<?>) body).forEach(e -> this.processObject(e, language, 0, visited));
        } else if (body instanceof Map<?, ?>) {
            ((Map<?, ?>) body).values().forEach(e -> this.processObject(e, language, 0, visited));
        } else {
            this.processObject(body, language, 0, visited);
        }
    }

    private void processObject(final Object target, final String language, final int depth, final Set<Object> visited) {
        if (target == null || this.isJavaBasicType(target.getClass()) || depth > this.getMaxRecursionDepth()) {
            if (depth > this.getMaxRecursionDepth()) {
                log.debug("When looking up a field, the recursion exceeds the maximum recursion depth");
            }
            return;
        }

        if (!visited.add(target)) {
            return;
        }

        final Class<?> clazz = target.getClass();

        for (final Field descField : this.getAllFields(clazz)) {
            try {
                this.processField(target, descField, clazz, language, depth, visited);
            } catch (Exception e) {
                log.debug("The processing field failed and the reason for the failure: {}", e.getMessage(), e);
            }
        }
    }

    private List<Field> getAllFields(Class<?> type) {
        final List<Field> fields = new ArrayList<>();
        Class<?> current = type;
        while (null != current && current != Object.class) {
            Collections.addAll(fields, current.getDeclaredFields());
            current = current.getSuperclass();
        }
        return fields;
    }

    private void processField(final Object target, final Field descField, final Class<?> clazz, final String language, final int depth, final Set<Object> visited) throws Exception {
        descField.setAccessible(true);

        // Prioritize fields annotated with @DictDesc
        final DictDesc annotation = descField.getAnnotation(DictDesc.class);
        if (null != annotation) {
            this.setDictDescToField(target, descField, annotation, clazz, language);
        } else {
            final Object nestedValue = descField.get(target);
            if (null != nestedValue) {
                final Class<?> fieldType = nestedValue.getClass();
                // Supports Collection, Map, and objects (provided they are annotated with @DictModel)
                if (nestedValue instanceof Collection<?>) {
                    ((Collection<?>) nestedValue).forEach(e -> this.processObject(e, language, depth + 1, visited));
                } else if (nestedValue instanceof Map<?, ?>) {
                    ((Map<?, ?>) nestedValue).values().forEach(e -> this.processObject(e, language, depth + 1, visited));
                } else if (this.shouldProcessType(fieldType)) {
                    this.processObject(nestedValue, language, depth + 1, visited);
                }
            }
        }
    }

    private void setDictDescToField(final Object target,
                                    final Field descField,
                                    final DictDesc annotation,
                                    final Class<?> clazz,
                                    final String language) {
        final String dictName = getDictName(annotation);

        final String baseFieldName = this.getBaseFieldName(annotation, descField);

        if (StringUtils.isNotBlank(baseFieldName)) {
            try {
                final Field baseField = this.getFieldRecursively(clazz, baseFieldName);
                baseField.setAccessible(true);
                final Object baseFieldValue = baseField.get(target);

                if (baseFieldValue instanceof String) {
                    final String text = this.i18nProvider.getText(language, dictName, (String) baseFieldValue).orElse("");
                    descField.set(target, text);
                }
            } catch (Exception e) {
                log.debug("setDictDescToField failed: {}", e.getMessage(), e);
            }
        }
    }

    private static String getDictName(final DictDesc annotation) {
        final Dict[] enums = annotation.value().getEnumConstants();
        if (enums.length > 0) {
            return enums[0].dictName();
        } else {
            try {
                return annotation.value().getDeclaredConstructor().newInstance().dictName();
            } catch (IllegalAccessException
                     | NoSuchMethodException
                     | InvocationTargetException
                     | InstantiationException e) {
                log.error("Failed to instantiate Dict enum class '{}' via no-arg constructor. Please check class design.", annotation.value().getName(), e);
            }
        }
        return null;
    }

    private Field getFieldRecursively(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        while (null != clazz && clazz != Object.class) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field '" + fieldName + "' not found in class hierarchy");
    }

    private boolean isJavaBasicType(final Class<?> clazz) {
        return clazz == String.class
                || clazz == Date.class
                || clazz.isPrimitive()
                || clazz.isEnum()
                || Number.class.isAssignableFrom(clazz)
                || Boolean.class.isAssignableFrom(clazz)
                || Character.class.isAssignableFrom(clazz);
    }

    private String getBaseFieldName(final DictDesc annotation, final Field descField) {

        if (StringUtils.isNotBlank(annotation.field())) {
            return annotation.field();
        }

        final String descFieldName = descField.getName();
        if (descFieldName.endsWith("Desc") && descFieldName.length() > 4) {
            return descFieldName.substring(0, descFieldName.length() - 4);
        }
        return null;
    }

    private boolean shouldProcessType(final Class<?> clazz) {
        // The base type is not processed directly
        if (this.isJavaBasicType(clazz)) {
            return false;
        }

        return this.processableCache.computeIfAbsent(clazz, key -> this.deepScanForDictDesc(key, 0));
    }

    /**
     * Deep recursion determines whether the type needs to be processed (whether it contains a @DictDesc field or a nested field)
     */
    private boolean deepScanForDictDesc(final Class<?> clazz, final int depth) {

        if (depth > this.getMaxRecursionDepth()) {
            log.debug("Recursion depth exceeds maximum limit: {}", this.getMaxRecursionDepth());
            return false;
        }

        if (Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz)) {
            return true;
        }

        if (clazz.isAnnotationPresent(DictModel.class)) {
            return true;
        }

        for (final Field field : this.getAllFields(clazz)) {
            if (field.isAnnotationPresent(DictDesc.class)) {
                return true;
            }

            // Avoid dealing with primitive types and avoid infinite recursion
            final Class<?> fieldType = field.getType();
            if (!this.isJavaBasicType(fieldType) && this.deepScanForDictDesc(fieldType, depth + 1)) {
                return true;
            }
        }

        return false;
    }
}