package cn.silwings.dicti18n.processor;

import cn.silwings.dicti18n.annotation.DictDesc;
import cn.silwings.dicti18n.annotation.DictModel;
import cn.silwings.dicti18n.config.DictI18nProperties;
import cn.silwings.dicti18n.dict.Dict;
import cn.silwings.dicti18n.provider.DictI18nProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * 测试DictI18nProcessor类中的方法
 * Test methods in DictI18nProcessor class
 */
@Slf4j
public class DictI18nProcessorTest {

    @Test
    public void testProcessNullBody() {
        // 测试处理null对象不做任何操作
        // Test processing null object does nothing
        final DictI18nProvider provider = mock(DictI18nProvider.class);
        final DictI18nProperties properties = new DictI18nProperties();
        final DictI18nProcessor processor = new DictI18nProcessor(provider, properties);

        processor.process(null, "zh-CN");
        verifyZeroInteractions(provider);
    }

    @Test
    public void testProcessCollection() {
        // 测试处理集合对象
        // Test processing collection objects
        final DictI18nProvider provider = mock(DictI18nProvider.class);
        final DictI18nProperties properties = new DictI18nProperties();
        final DictI18nProcessor processor = new DictI18nProcessor(provider, properties);

        final List<TestModel> models = Arrays.asList(new TestModel().setOrderStatus(OrderStatus.SHIPPED.name()), new TestModel().setOrderStatus(OrderStatus.PROCESSING.name()));
        processor.process(models, "zh-CN");

        verify(provider, times(2)).getText(eq("zh-CN"), any(), any(), any());
    }

    @Test
    public void testProcessMap() {
        // 测试处理Map对象
        // Test processing Map objects
        final DictI18nProvider provider = mock(DictI18nProvider.class);
        final DictI18nProperties properties = new DictI18nProperties();
        final DictI18nProcessor processor = new DictI18nProcessor(provider, properties);

        final Map<String, TestModel> modelMap = new HashMap<>();
        modelMap.put("key1", new TestModel().setOrderStatus(OrderStatus.SHIPPED.name()));
        modelMap.put("key2", new TestModel().setOrderStatus(OrderStatus.PROCESSING.name()));
        processor.process(modelMap, "zh-CN");

        verify(provider, times(2)).getText(eq("zh-CN"), any(), any(), any());
    }

    @Test
    public void testProcessObjectWithDictDesc() {
        // 测试处理带有DictDesc注解的对象
        // Test processing object with DictDesc annotation
        final DictI18nProvider provider = mock(DictI18nProvider.class);
        when(provider.getText(any(), any(), any(), any())).thenReturn(Optional.of("测试值"));

        final DictI18nProperties properties = new DictI18nProperties();
        final DictI18nProcessor processor = new DictI18nProcessor(provider, properties);

        final TestModel model = new TestModel();
        model.setOrderStatus(OrderStatus.PENDING.name());
        processor.process(model, "zh-CN");

        assertEquals("测试值", model.getOrderStatusDesc());
    }

    @Test
    public void testProcessObjectWithNestedObject() {
        // 测试处理嵌套对象
        // Test processing nested objects
        final DictI18nProvider provider = mock(DictI18nProvider.class);
        final DictI18nProperties properties = new DictI18nProperties();
        final DictI18nProcessor processor = new DictI18nProcessor(provider, properties);

        final TestNestedModel nestedModel = new TestNestedModel();
        nestedModel.setModel(new TestModel().setOrderStatus(OrderStatus.PENDING.name()));
        processor.process(nestedModel, "zh-CN");

        verify(provider, atLeastOnce()).getText(eq("zh-CN"), any(), any(), any());
    }

    @Test
    public void testProcessObjectWithRecursionLimit() {
        // 测试递归深度限制
        // Test recursion depth limit
        final DictI18nProvider provider = mock(DictI18nProvider.class);
        final DictI18nProperties properties = new DictI18nProperties();
        final DictI18nProcessor processor = new DictI18nProcessor(provider, properties);

        final TestRecursiveModel recursiveModel = new TestRecursiveModel();
        initRecursiveModel(recursiveModel, 100_000);

        // 短时间内必须完成，否则测试失败
        // It must be completed within a short time, otherwise the test will fail
        assertTimeoutPreemptively(
                Duration.ofMillis(3),
                () -> processor.process(recursiveModel, "zh-CN")
        );
    }

    private void initRecursiveModel(final TestRecursiveModel rootModel, final int depth) {
        TestRecursiveModel current = rootModel;
        for (int i = 0; i < depth; i++) {
            TestRecursiveModel child = new TestRecursiveModel();
            current.setChild(child);
            current = child;
        }
    }

    @Test
    public void testIsJavaBasicType() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 测试基本类型判断
        // Test basic type detection
        final DictI18nProcessor processor = new DictI18nProcessor(mock(DictI18nProvider.class), new DictI18nProperties());
        final Method isJavaBasicType = processor.getClass().getDeclaredMethod("isJavaBasicType", Class.class);
        isJavaBasicType.setAccessible(true);

        assertTrue((Boolean) isJavaBasicType.invoke(processor, String.class));
        assertTrue((Boolean) isJavaBasicType.invoke(processor, Integer.class));
        assertTrue((Boolean) isJavaBasicType.invoke(processor, int.class));
        assertTrue((Boolean) isJavaBasicType.invoke(processor, Double.class));
        assertTrue((Boolean) isJavaBasicType.invoke(processor, Boolean.class));
        assertFalse((Boolean) isJavaBasicType.invoke(processor, Object.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetAllFields() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 测试获取所有字段
        // Test getting all fields
        final DictI18nProcessor processor = new DictI18nProcessor(mock(DictI18nProvider.class), new DictI18nProperties());
        final Method getAllFields = processor.getClass().getDeclaredMethod("getAllFields", Class.class);
        getAllFields.setAccessible(true);

        List<Field> fields = (List<Field>) getAllFields.invoke(processor, ChildModel.class);
        fields = fields.stream().filter(f -> !f.toGenericString().endsWith("__$hits$__")).collect(Collectors.toList());
        assertEquals(2, fields.size());
    }

    @Test
    public void testShouldProcessTypeWithAnnotation() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 测试带有DictModel注解的类型处理
        // Test processing type with DictModel annotation
        final DictI18nProcessor processor = new DictI18nProcessor(mock(DictI18nProvider.class), new DictI18nProperties());
        final Method shouldProcessType = processor.getClass().getDeclaredMethod("shouldProcessType", Class.class, int.class);
        shouldProcessType.setAccessible(true);

        assertTrue((boolean) shouldProcessType.invoke(processor, TestModel.class, 0));
        assertFalse((boolean) shouldProcessType.invoke(processor, String.class, 0));
    }

    @Test
    public void testShouldProcessTypeWithCollection() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 测试集合类型处理
        // Test processing collection type
        final DictI18nProcessor processor = new DictI18nProcessor(mock(DictI18nProvider.class), new DictI18nProperties());

        final Method shouldProcessType = processor.getClass().getDeclaredMethod("shouldProcessType", Class.class, int.class);
        shouldProcessType.setAccessible(true);

        assertTrue((boolean) shouldProcessType.invoke(processor, List.class, 0));
        assertTrue((boolean) shouldProcessType.invoke(processor, Map.class, 0));
    }

    @DictModel
    @Getter
    @Setter
    @Accessors(chain = true)
    static class TestModel {

        private String orderStatus;

        @DictDesc(value = OrderStatus.class)
        private String orderStatusDesc;
    }

    public enum OrderStatus implements Dict {
        PENDING,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED,
        RETURNED;

        @Override
        public String dictName() {
            return "order_status";
        }

        @Override
        public String code() {
            return this.name();
        }
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    static class TestNestedModel {
        private TestModel model;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    static class TestRecursiveModel {
        private TestRecursiveModel child;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    static class ParentModel {
        private String parentField;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    static class ChildModel extends ParentModel {
        private String childField;
    }
}
