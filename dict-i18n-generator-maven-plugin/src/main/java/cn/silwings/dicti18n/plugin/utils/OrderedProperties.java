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

    private final TreeSet<Object> sortedKeys = new TreeSet<>();

    @Override
    public synchronized Object put(final Object key, final Object value) {
        this.sortedKeys.add(key);
        return super.put(key, value);
    }

    @Override
    public synchronized void putAll(final Map<?, ?> t) {
        this.sortedKeys.addAll(t.keySet());
        super.putAll(t);
    }

    @Override
    public synchronized Object remove(final Object key) {
        this.sortedKeys.remove(key);
        return super.remove(key);
    }

    @Override
    public Set<String> stringPropertyNames() {
        final LinkedHashSet<String> orderedNames = new LinkedHashSet<>();
        for (Object key : this.sortedKeys) {
            if (key instanceof String) {
                orderedNames.add((String) key);
            }
        }
        return orderedNames;
    }

    @Override
    public Enumeration<?> propertyNames() {
        final Vector<String> orderedNames = new Vector<>();
        for (Object key : this.sortedKeys) {
            if (key instanceof String) {
                orderedNames.add((String) key);
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