package cn.silwings.dicti18n.demo.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

@Aspect
//@Component
public class PerformanceMonitorAspect {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitorAspect.class);

    // 每个线程的调用栈信息：存储当前调用深度、当前方法ID、父方法ID
    private static class CallStackInfo {
        int depth; // 嵌套深度（用于缩进）
        int currentId; // 当前方法ID
        Integer parentId; // 父方法ID（调用者ID）
    }

    // ThreadLocal存储每个线程的调用栈和ID生成器
    private final ThreadLocal<Stack<CallStackInfo>> callStack = ThreadLocal.withInitial(Stack::new);
    private final ThreadLocal<AtomicInteger> idGenerator = ThreadLocal.withInitial(() -> new AtomicInteger(1));

    // 监控指定包下的所有方法
    @Around("execution(* cn.silwings.dicti18n..*.*(..))")
    public Object monitor(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取当前线程的ID生成器和调用栈
        AtomicInteger idGen = idGenerator.get();
        Stack<CallStackInfo> stack = callStack.get();

        // 生成当前方法的唯一ID
        int currentId = idGen.getAndIncrement();
        // 父方法ID：如果栈不为空，父ID是栈顶元素的currentId
        Integer parentId = stack.isEmpty() ? null : stack.peek().currentId;
        // 嵌套深度：栈的大小（进入方法前，栈的大小就是当前深度）
        int depth = stack.size();

        // 构建缩进字符串（每个层级2个空格）
        String indent = this.repeatStr("  ", depth);

        // 获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        // 记录当前方法的调用信息并压入栈
        CallStackInfo currentCall = new CallStackInfo();
        currentCall.depth = depth;
        currentCall.currentId = currentId;
        currentCall.parentId = parentId;
        stack.push(currentCall);

        // 打印“进入方法”日志
        logger.info("{}[{} >> {}] {}.{}",
                indent, null == parentId ? 0 : parentId, currentId, className, methodName);

        // 计时并执行目标方法
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            return joinPoint.proceed(); // 执行目标方法
        } finally {
            stopWatch.stop();
            // 弹出栈（退出方法）
            stack.pop();

            // 打印“退出方法”日志（包含执行时间）
            logger.info("{}[{} << {}] {}.{} - 耗时:{}ms",
                    indent, null == parentId ? 0 : parentId, currentId, className, methodName, stopWatch.getTotalTimeMillis());

            // 清理ThreadLocal（如果栈为空，说明当前线程的所有方法调用已结束）
            if (stack.isEmpty()) {
                callStack.remove();
                idGenerator.remove();
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