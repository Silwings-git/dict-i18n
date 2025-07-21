package cn.silwings.dicti18n.starter.endpoint;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public interface EndpointHandler extends HttpRequestHandler {

    @SuppressWarnings("unchecked")
    default void writeWithMessageConverters(final ResponseEntity<?> responseEntity, final HttpServletResponse response) throws ServletException, IOException {

        response.setStatus(responseEntity.getStatusCodeValue());
        responseEntity.getHeaders().forEach((headerName, headerValues) -> {
            for (String value : headerValues) {
                response.addHeader(headerName, value);
            }
        });

        if (responseEntity.hasBody()) {
            final Object body = Objects.requireNonNull(responseEntity.getBody());
            for (HttpMessageConverter<?> converter : this.getHandlerAdapter().getMessageConverters()) {
                if (converter.canWrite(body.getClass(), MediaType.APPLICATION_JSON)) {
                    ((HttpMessageConverter<Object>) converter).write(body, MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));
                    return;
                }
            }
        }
        throw new HttpMediaTypeNotAcceptableException("[DictI18n] No suitable HttpMessageConverter found");
    }

    RequestMappingHandlerAdapter getHandlerAdapter();

}