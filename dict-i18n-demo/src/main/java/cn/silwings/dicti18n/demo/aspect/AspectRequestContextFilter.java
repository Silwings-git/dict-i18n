package cn.silwings.dicti18n.demo.aspect;

import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@Component
@WebFilter(urlPatterns = "/*")
public class AspectRequestContextFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            // 初始化请求上下文
            RequestContext.initRequest();
            chain.doFilter(request, response);
        } finally {
            // 清理请求上下文
            RequestContext.clear();
        }
    }
}