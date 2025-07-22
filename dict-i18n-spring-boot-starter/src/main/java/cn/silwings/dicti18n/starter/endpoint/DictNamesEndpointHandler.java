package cn.silwings.dicti18n.starter.endpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DictNamesEndpointHandler class is used to handle HTTP requests for retrieving all dictionary names.
 * It implements the EndpointHandler interface and is responsible for returning a list of names of all available dictionaries in the system.
 */
public class DictNamesEndpointHandler implements EndpointHandler {

    private final RequestMappingHandlerAdapter handlerAdapter;
    private final DictMapHolder dictMapHolder;

    public DictNamesEndpointHandler(final RequestMappingHandlerAdapter handlerAdapter, final DictMapHolder dictMapHolder) {
        this.handlerAdapter = handlerAdapter;
        this.dictMapHolder = dictMapHolder;
    }

    @Override
    public RequestMappingHandlerAdapter getHandlerAdapter() {
        return this.handlerAdapter;
    }

    /**
     * Process HTTP requests and return a list of all dictionary names in the system.
     */
    @Override
    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

        // Get all dictionary names from the dictionary mapper holder, sort them, and store in a list
        final List<String> dictNamsList = this.dictMapHolder.getAllDictNames()
                .stream()
                .sorted()
                .collect(Collectors.toList());

        this.writeWithMessageConverters(ResponseEntity.ok(dictNamsList), response);
    }
}
