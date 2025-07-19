package cn.silwings.dicti18n.demo.aspect;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@Component
@WebFilter(urlPatterns = "/*")
public class AspectRequestContextFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            RequestContext.initRequest();
            chain.doFilter(request, response);
        } finally {
            RequestContext.clear();
        }
    }
}