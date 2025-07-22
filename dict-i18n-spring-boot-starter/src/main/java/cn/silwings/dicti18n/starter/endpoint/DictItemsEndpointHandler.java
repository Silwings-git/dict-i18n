package cn.silwings.dicti18n.starter.endpoint;

import cn.silwings.dicti18n.config.DictI18nProperties;
import cn.silwings.dicti18n.dict.Dict;
import cn.silwings.dicti18n.provider.CompositeDictI18nProvider;
import cn.silwings.dicti18n.starter.config.LanguageProvider;
import cn.silwings.dicti18n.starter.endpoint.vo.DictItemVO;
import cn.silwings.dicti18n.starter.endpoint.vo.DictItemsResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dictionary item endpoint handler, responsible for processing HTTP requests for querying dictionary data.
 * Scan the dictionary definitions of enumeration types and provide multilingual dictionary data query services.
 */
public class DictItemsEndpointHandler implements EndpointHandler {

    private final LanguageProvider languageProvider;
    private final DictI18nProperties dictI18nProperties;
    private final CompositeDictI18nProvider compositeDictI18nProvider;
    private final RequestMappingHandlerAdapter handlerAdapter;
    private final DictMapHolder dictMapHolder;

    public DictItemsEndpointHandler(final LanguageProvider languageProvider, final DictI18nProperties dictI18nProperties, final CompositeDictI18nProvider compositeDictI18nProvider, final RequestMappingHandlerAdapter handlerAdapter, final DictMapHolder dictMapHolder) {
        this.languageProvider = languageProvider;
        this.dictI18nProperties = dictI18nProperties;
        this.compositeDictI18nProvider = compositeDictI18nProvider;
        this.handlerAdapter = handlerAdapter;
        this.dictMapHolder = dictMapHolder;
    }

    @Override
    public RequestMappingHandlerAdapter getHandlerAdapter() {
        return this.handlerAdapter;
    }

    /**
     * Process HTTP requests and return dictionary data
     */
    @Override
    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final String dictNames = request.getParameter("dictNames");
        final String language = request.getParameter("language");

        final ResponseEntity<DictItemsResponse> result = this.getItems(dictNames, language);

        this.writeWithMessageConverters(result, response);
    }

    /**
     * Get the dictionary item list based on the dictionary name and language
     *
     * @param dictNames Comma-separated list of dictionary names
     * @param language  Requested language, can be empty
     * @return Response entity containing dictionary items
     */
    private ResponseEntity<DictItemsResponse> getItems(final String dictNames, final String language) {

        if (StringUtils.isBlank(dictNames)) {
            return ResponseEntity.ok(DictItemsResponse.empty());
        }

        // Parse and process the requested list of dictionary names
        final List<String> dictNameList = Arrays.stream(dictNames.split(","))
                .filter(StringUtils::isNotBlank)
                .distinct()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        // Determine the actual language to be used, prioritizing the request parameters; otherwise, use the current context language.
        final String actualLanguage = StringUtils.isNotBlank(language) ? language : this.languageProvider.getCurrentLanguage();

        final Map<String, List<DictItemVO>> dictNameCodesMapping = new HashMap<>(dictNameList.size());
        for (String dictName : dictNameList) {
            final Dict[] dictArray = this.dictMapHolder.getDictItems(dictName);
            final List<DictItemVO> dictItemList = new ArrayList<>(dictArray.length);
            dictNameCodesMapping.put(dictName, dictItemList);

            // Process each dictionary item to obtain the corresponding language's text description.
            for (final Dict dict : dictArray) {
                final String text = this.compositeDictI18nProvider.getText(actualLanguage, this.dictI18nProperties.getDefaultLang(), dictName, dict.code())
                        .filter(s -> !s.isEmpty())
                        .orElseGet(() -> this.dictI18nProperties.isReturnKeyIfEmpty() ? dict.dictName() + "." + dict.code() : null);
                dictItemList.add(new DictItemVO(dict.code(), text));
            }
        }

        return ResponseEntity.ok(new DictItemsResponse(dictNameCodesMapping));
    }
}
