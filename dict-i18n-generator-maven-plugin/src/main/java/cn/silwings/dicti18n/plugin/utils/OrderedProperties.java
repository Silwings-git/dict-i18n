package cn.silwings.dicti18n.plugin.utils;

import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 * Extend the Properties class to support natural sorting of keys
 */
public class OrderedProperties extends Properties {

    private final TreeSet<String> sortedKeys = new TreeSet<>();

    @Override
    public synchronized Object put(final Object key, final Object value) {
        if (null == key) {
            throw new NullPointerException("Key cannot be null");
        }
        if (key instanceof String) {
            this.sortedKeys.add((String) key);
        } else {
            throw new IllegalArgumentException("Key must be a String");
        }
        return super.put(key, value);
    }

    @Override
    public synchronized void putAll(final Map<?, ?> t) {
        for (Object key : t.keySet()) {
            if (!(key instanceof String)) {
                throw new IllegalArgumentException("Key must be a String");
            }
        }
        for (Object key : t.keySet()) {
            this.sortedKeys.add((String) key);
        }
        super.putAll(t);
    }

    @Override
    public synchronized Object remove(final Object key) {
        if (key instanceof String) {
            this.sortedKeys.remove(key);
        }
        return super.remove(key);
    }

    @Override
    public Set<String> stringPropertyNames() {
        final LinkedHashSet<String> orderedNames = new LinkedHashSet<>();
        for (String key : this.sortedKeys) {
            if (null != key) {
                orderedNames.add(key);
            }
        }
        return orderedNames;
    }

    @Override
    public Enumeration<?> propertyNames() {
        final Vector<String> orderedNames = new Vector<>();
        for (String key : this.sortedKeys) {
            if (null != key) {
                orderedNames.add(key);
            }
        }
        return orderedNames.elements();
    }

    @Override
    public Set<Object> keySet() {
        return new LinkedHashSet<>(this.sortedKeys);
    }

    @Override
    public Enumeration<Object> keys() {
        final Vector<Object> orderedKeys = new Vector<>(this.sortedKeys);
        return orderedKeys.elements();
    }
}