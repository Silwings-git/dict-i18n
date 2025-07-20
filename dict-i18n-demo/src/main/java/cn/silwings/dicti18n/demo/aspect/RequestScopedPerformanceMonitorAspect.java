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

    // Store the method call information for each request (RequestID → call stack)
    private final ThreadLocal<Stack<MethodCall>> methodCallStack = ThreadLocal.withInitial(Stack::new);
    // Store the total duration of each request (RequestID → Total time)
    private final ThreadLocal<Map<String, Long>> requestTotalTime = ThreadLocal.withInitial(HashMap::new);

    // method invocation information
    private static class MethodCall {
        String className;
        String methodName;
        long startTime;
        long duration;
        // The calling order of methods in the request
        int methodIndex;
        // nested depth
        int depth;
        // the methodIndex of the parent method (i.e., the parent ID)
        int parentIndex;
    }

    @Around("execution(* cn.silwings.dicti18n..*.*(..))")
    public Object monitor(ProceedingJoinPoint joinPoint) throws Throwable {
        String requestId = RequestContext.getRequestId();
        Stack<MethodCall> stack = methodCallStack.get();
        int depth = stack.size();

        // Get the parent method information; if the stack is empty, it means this is the first method call.
        final int parentIndex = stack.isEmpty() ? 0 : stack.peek().methodIndex;

        if (stack.isEmpty()) {
            logger.debug("========== Request initiated ==========");
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        // Create method call record
        final MethodCall currentCall = new MethodCall();
        currentCall.className = className;
        currentCall.methodName = methodName;
        currentCall.startTime = System.currentTimeMillis();
        currentCall.methodIndex = StringUtils.isBlank(RequestContext.getRequestId()) ? 1 : RequestContext.nextMethodIndex();
        currentCall.depth = depth;
        currentCall.parentIndex = parentIndex;

        stack.push(currentCall);

        final String indent = this.repeatStr("  ", depth);
        logger.debug("{}[{} >> {}] #{}.{}",
                indent, currentCall.parentIndex, currentCall.methodIndex, className, methodName);

        try {
            return joinPoint.proceed();
        } finally {
            // Computation method time consumption
            long endTime = System.currentTimeMillis();
            currentCall.duration = endTime - currentCall.startTime;

            // Total time taken for update request
            Map<String, Long> totalTimeMap = requestTotalTime.get();
            totalTimeMap.put(requestId, totalTimeMap.getOrDefault(requestId, 0L) + currentCall.duration);

            logger.debug("{}[{} << {}] #{}.{} - 耗时:{}ms",
                    indent, currentCall.parentIndex, currentCall.methodIndex, className, methodName, currentCall.duration);

            stack.pop();

            // If the stack is empty, it means all methods in the current request have been executed.
            if (stack.isEmpty()) {
                long totalTime = totalTimeMap.get(requestId);
                logger.debug("========== Total request time: {}ms ==========", totalTime);

                // clean ThreadLocal
                methodCallStack.remove();
                requestTotalTime.remove();
            }
        }
    }

    private String repeatStr(final String str, final int depth) {
        // Generate indent string
        StringBuilder indentBuilder = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indentBuilder.append(str);
        }
        return indentBuilder.toString();
    }
}