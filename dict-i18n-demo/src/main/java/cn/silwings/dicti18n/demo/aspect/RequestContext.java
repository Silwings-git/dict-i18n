package cn.silwings.dicti18n.demo.aspect;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestContext {
    private static final ThreadLocal<String> REQUEST_ID = new ThreadLocal<>();
    private static final ThreadLocal<AtomicInteger> METHOD_COUNTER = ThreadLocal.withInitial(() -> new AtomicInteger(1));

    // 初始化请求ID（在请求入口调用）
    public static void initRequest() {
        REQUEST_ID.set(UUID.randomUUID().toString());
    }

    // 获取当前请求ID
    public static String getRequestId() {
        return REQUEST_ID.get();
    }

    // 生成方法调用序号（用于排序）
    public static int nextMethodIndex() {
        return METHOD_COUNTER.get().incrementAndGet();
    }

    // 清理请求上下文（在请求结束时调用）
    public static void clear() {
        REQUEST_ID.remove();
        METHOD_COUNTER.remove();
    }
}