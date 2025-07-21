package cn.silwings.dicti18n.starter.endpoint;

import cn.silwings.dicti18n.config.DictI18nProperties;
import cn.silwings.dicti18n.dict.Dict;
import cn.silwings.dicti18n.loader.scan.DictScanner;
import cn.silwings.dicti18n.provider.CompositeDictI18nProvider;
import cn.silwings.dicti18n.starter.config.DictI18nStarterProperties;
import cn.silwings.dicti18n.starter.config.LanguageProvider;
import cn.silwings.dicti18n.starter.endpoint.vo.DictItemVO;
import cn.silwings.dicti18n.starter.endpoint.vo.DictItemsResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Dictionary item endpoint handler, responsible for processing HTTP requests for querying dictionary data.
 * Scan the dictionary definitions of enumeration types and provide multilingual dictionary data query services.
 */
public class DictItemsEndpointHandler implements EndpointHandler, ApplicationContextAware, ApplicationRunner {

    private static final Dict[] EMPTY_DICT_ARRAY = new Dict[0];
    private final DictScanner dictScanner;
    private final LanguageProvider languageProvider;
    private final DictI18nProperties dictI18nProperties;
    private final CompositeDictI18nProvider compositeDictI18nProvider;
    private final DictI18nStarterProperties dictI18nStarterProperties;
    private final RequestMappingHandlerAdapter handlerAdapter;

    // A mapping of dictionary names to arrays of dictionary entries,
    // where the keys are lowercase dictionary names and the values are all entries of the corresponding dictionary.
    private final Map<String, Dict[]> dictMap;
    private ApplicationContext applicationContext;

    public DictItemsEndpointHandler(final DictScanner dictScanner, final LanguageProvider languageProvider, final DictI18nProperties dictI18nProperties, final CompositeDictI18nProvider compositeDictI18nProvider, final DictI18nStarterProperties dictI18nStarterProperties, final RequestMappingHandlerAdapter handlerAdapter) {
        this.dictScanner = dictScanner;
        this.languageProvider = languageProvider;
        this.dictI18nProperties = dictI18nProperties;
        this.compositeDictI18nProvider = compositeDictI18nProvider;
        this.dictI18nStarterProperties = dictI18nStarterProperties;
        this.handlerAdapter = handlerAdapter;
        this.dictMap = new ConcurrentHashMap<>();
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public RequestMappingHandlerAdapter getHandlerAdapter() {
        return this.handlerAdapter;
    }

    /**
     * Perform initialization when the application starts, scan and register all dictionaries.
     */
    @Override
    public void run(final ApplicationArguments args) {
        this.init();
    }

    /**
     * Initialization method, scans all enumeration classes under the specified package and registers them as dictionaries
     */
    public void init() {
        this.dictScanner
                .scan(this.getScanPackages())
                .stream()
                .filter(Class::isEnum)
                .forEach(clazz -> {
                    final Dict[] enumConstants = clazz.getEnumConstants();
                    if (ArrayUtils.isNotEmpty(enumConstants)) {
                        this.dictMap.put(enumConstants[0].dictName().toLowerCase(), enumConstants);
                    }
                });
    }

    /**
     * Get the list of scan package paths
     * If the scan package is configured, use the configured value; otherwise, use the auto-configured package path.
     */
    private List<String> getScanPackages() {
        return this.dictI18nStarterProperties.getScanPackages().isEmpty()
                ? AutoConfigurationPackages.get(this.applicationContext)
                : this.dictI18nStarterProperties.getScanPackages();
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
            final Dict[] dictArray = this.dictMap.getOrDefault(dictName, EMPTY_DICT_ARRAY);
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
