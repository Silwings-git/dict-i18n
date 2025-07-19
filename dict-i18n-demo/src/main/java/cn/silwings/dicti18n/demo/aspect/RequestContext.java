package cn.silwings.dicti18n.demo.aspect;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestContext {
    private static final ThreadLocal<String> REQUEST_ID = new ThreadLocal<>();
    private static final ThreadLocal<AtomicInteger> METHOD_COUNTER = ThreadLocal.withInitial(() -> new AtomicInteger(1));

    public static void initRequest() {
        REQUEST_ID.set(UUID.randomUUID().toString());
    }

    public static String getRequestId() {
        return REQUEST_ID.get();
    }

    public static int nextMethodIndex() {
        return METHOD_COUNTER.get().incrementAndGet();
    }

    public static void clear() {
        REQUEST_ID.remove();
        METHOD_COUNTER.remove();
    }
}