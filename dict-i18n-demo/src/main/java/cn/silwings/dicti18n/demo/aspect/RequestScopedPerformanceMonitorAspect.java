package cn.silwings.dicti18n.demo.aspect;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

@Aspect
@Component
public class RequestScopedPerformanceMonitorAspect {

    private static final Logger logger = LoggerFactory.getLogger(RequestScopedPerformanceMonitorAspect.class);

    // 存储每个请求的方法调用信息（RequestID → 调用栈）
    private final ThreadLocal<Stack<MethodCall>> methodCallStack = ThreadLocal.withInitial(Stack::new);
    // 存储每个请求的总耗时（RequestID → 总时间）
    private final ThreadLocal<Map<String, Long>> requestTotalTime = ThreadLocal.withInitial(HashMap::new);

    // 方法调用信息
    private static class MethodCall {
        String className;
        String methodName;
        long startTime;
        long duration;
        // 方法在请求中的调用顺序
        int methodIndex;
        // 嵌套深度
        int depth;
        // 父方法的methodIndex（即父ID）
        int parentIndex;
    }

    @Around("execution(* cn.silwings.dicti18n..*.*(..))")
    public Object monitor(ProceedingJoinPoint joinPoint) throws Throwable {
        String requestId = RequestContext.getRequestId();
        Stack<MethodCall> stack = methodCallStack.get();
        int depth = stack.size(); // 当前嵌套深度

        // 获取父方法信息（如果存在）
        final int parentIndex = stack.isEmpty() ? 0 : stack.peek().methodIndex;

        if (stack.isEmpty()) {
            logger.info("========== 请求开始 ==========");
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        // 创建方法调用记录
        MethodCall currentCall = new MethodCall();
        currentCall.className = className;
        currentCall.methodName = methodName;
        currentCall.startTime = System.currentTimeMillis();
        currentCall.methodIndex = StringUtils.isBlank(RequestContext.getRequestId()) ? 1 : RequestContext.nextMethodIndex();
        currentCall.depth = depth;
        currentCall.parentIndex = parentIndex; // 保存父ID

        stack.push(currentCall);

        // 打印进入方法日志
        String indent = this.repeatStr("  ", depth);
        logger.info("{}[{} >> {}] #{}.{}",
                indent, currentCall.parentIndex, currentCall.methodIndex, className, methodName);

        try {
            return joinPoint.proceed();
        } finally {
            // 计算方法耗时
            long endTime = System.currentTimeMillis();
            currentCall.duration = endTime - currentCall.startTime;

            // 更新请求总耗时
            Map<String, Long> totalTimeMap = requestTotalTime.get();
            totalTimeMap.put(requestId, totalTimeMap.getOrDefault(requestId, 0L) + currentCall.duration);

            // 打印退出方法日志
            logger.info("{}[{} << {}] #{}.{} - 耗时:{}ms",
                    indent, currentCall.parentIndex, currentCall.methodIndex, className, methodName, currentCall.duration);

            stack.pop();

            // 如果栈为空，说明当前请求的所有方法都已执行完毕
            if (stack.isEmpty()) {
                long totalTime = totalTimeMap.get(requestId);
                logger.info("========== 请求总耗时: {}ms ==========", totalTime);

                // 清理ThreadLocal
                methodCallStack.remove();
                requestTotalTime.remove();
            }
        }
    }

    private String repeatStr(final String str, final int depth) {
        // 生成缩进字符串
        StringBuilder indentBuilder = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indentBuilder.append(str);
        }
        return indentBuilder.toString();
    }
}