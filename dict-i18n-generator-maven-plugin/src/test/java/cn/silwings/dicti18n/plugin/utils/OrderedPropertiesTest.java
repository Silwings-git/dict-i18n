package cn.silwings.dicti18n.plugin.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 测试 OrderedProperties 类的有序属性功能和键类型验证
 * Test the ordered properties functionality and key type validation of OrderedProperties class
 */
public class OrderedPropertiesTest {
    private OrderedProperties props;

    @BeforeEach  // 替换 JUnit 4 的 @Before
    public void setUp() {
        this.props = new OrderedProperties();
    }

    @Test
    public void testPutAndOrderingWithStringKeys() {
        // 测试字符串键的插入和排序
        // Test string key insertion and ordering
        this.props.put("z_key", "value1");
        this.props.put("a_key", "value2");
        this.props.put("m_key", "value3");

        final Iterator<Object> iterator = this.props.keySet().iterator();
        assertEquals("a_key", iterator.next());
        assertEquals("m_key", iterator.next());
        assertEquals("z_key", iterator.next());
    }

    @Test  // JUnit 5 中使用 assertThrows 替代 expected 属性
    public void testPutWithNonStringKey() {
        // 测试非字符串键抛出异常
        // Test putting non-string key throws exception
        Executable executable = () -> this.props.put(123, "numeric");
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    public void testPutAllWithStringKeys() {
        // 测试批量插入字符串键
        // Test putAll with string keys
        final Map<String, String> map = new HashMap<>();
        map.put("b_key", "value1");
        map.put("a_key", "value2");

        this.props.putAll(map);

        assertTrue(this.props.containsKey("a_key"));
        assertTrue(this.props.containsKey("b_key"));
        assertEquals(2, this.props.size());
    }

    @Test
    public void testPutAllWithNonStringKeys() {
        // 测试批量插入包含非字符串键抛出异常
        // Test putAll with non-string keys throws exception
        final Map<Object, Object> map = new HashMap<>();
        map.put(123, "numeric");
        map.put("a_key", "string");

        Executable executable = () -> this.props.putAll(map);
        assertThrows(IllegalArgumentException.class, executable);

        // 验证异常后属性为空
        assertEquals(0, this.props.size());
    }

    @Test
    public void testPutNullKey() {
        // 测试插入null键抛出异常
        // The test throws an exception when inserting a null key
        Executable executable = () -> this.props.put(null, "value");
        assertThrows(NullPointerException.class, executable);
    }

    @Test
    public void testPutNullValue() {
        // 测试插入null值抛出异常
        // The test throws an exception by inserting a null value
        Executable executable = () -> this.props.put("1", null);
        assertThrows(NullPointerException.class, executable);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveStringKey() throws NoSuchFieldException, IllegalAccessException {
        // 测试移除字符串键
        // Test removing string key
        this.props.put("key1", "value1");
        this.props.put("key2", "value2");

        this.props.remove("key1");

        assertEquals(1, this.props.size());
        assertFalse(this.props.containsKey("key1"));
        assertTrue(this.props.containsKey("key2"));

        final Field sortedKeys = this.props.getClass().getDeclaredField("sortedKeys");
        sortedKeys.setAccessible(true);
        final TreeSet<String> sortKeysValue = (TreeSet<String>) sortedKeys.get(props);
        assertFalse(sortKeysValue.contains("key1"));
    }

    @Test
    public void testRemoveNonStringKey() {
        // 测试移除非字符串键(虽然不能put，但remove时应该不抛异常)
        // Test removing non-string key (shouldn't throw exception)
        final Object result = this.props.remove(123);
        assertNull(result);
    }

    @Test
    public void testStringPropertyNames() {
        // 测试获取有序的字符串属性名
        // Test getting ordered string property names
        this.props.put("3_key", "value1");
        this.props.put("1_key", "value2");
        this.props.put("2_key", "value3");

        final Iterator<String> iterator = this.props.stringPropertyNames().iterator();
        assertEquals("1_key", iterator.next());
        assertEquals("2_key", iterator.next());
        assertEquals("3_key", iterator.next());
    }

    @Test
    public void testPropertyNames() {
        // 测试获取有序的属性名枚举
        // Test getting ordered property names enumeration
        this.props.put("x_key", "value1");
        this.props.put("a_key", "value2");

        final Enumeration<?> enumeration = this.props.propertyNames();
        assertEquals("a_key", enumeration.nextElement());
        assertEquals("x_key", enumeration.nextElement());
        assertFalse(enumeration.hasMoreElements());
    }

    @Test
    public void testKeySet() {
        // 测试获取有序的键集合
        // Test getting ordered key set
        this.props.put("z_key", "value1");
        this.props.put("a_key", "value2");

        final Iterator<Object> iterator = this.props.keySet().iterator();
        assertEquals("a_key", iterator.next());
        assertEquals("z_key", iterator.next());
    }

    @Test
    public void testKeys() {
        // 测试获取有序的键枚举
        // Test getting ordered keys enumeration
        this.props.put("m_key", "value1");
        this.props.put("a_key", "value2");

        final Enumeration<Object> keys = this.props.keys();
        assertEquals("a_key", keys.nextElement());
        assertEquals("m_key", keys.nextElement());
        assertFalse(keys.hasMoreElements());
    }

    @Test
    public void testConcurrentModificationSafety() {
        // 测试并发修改安全性
        // Test concurrent modification safety
        this.props.put("key1", "value1");
        this.props.put("key2", "value2");

        // 测试迭代时修改
        // Test modification during iteration
        final Iterator<Object> iterator = this.props.keySet().iterator();
        iterator.next();
        this.props.put("key3", "value3"); // 这里不应该影响已开始的迭代

        assertEquals("key2", iterator.next());
        assertFalse(iterator.hasNext());
        assertEquals(3, this.props.size());
    }
}