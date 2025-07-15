package cn.silwings.dicti18n.starter.endpoint;

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
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DictItemsHandler implements EndpointHandler, ApplicationContextAware, ApplicationRunner {

    private static final Dict[] EMPTY_DICT_ARRAY = new Dict[0];
    private final DictScanner dictScanner;
    private final LanguageProvider languageProvider;
    private final CompositeDictI18nProvider compositeDictI18nProvider;
    private final DictI18nStarterProperties dictI18nStarterProperties;
    private final RequestMappingHandlerAdapter handlerAdapter;

    // <dictName,dictList>
    private final Map<String, Dict[]> dictMap;
    private ApplicationContext applicationContext;

    public DictItemsHandler(final DictScanner dictScanner, final LanguageProvider languageProvider, final CompositeDictI18nProvider compositeDictI18nProvider, final DictI18nStarterProperties dictI18nStarterProperties, final RequestMappingHandlerAdapter handlerAdapter) {
        this.dictScanner = dictScanner;
        this.languageProvider = languageProvider;
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

    @Override
    public void run(final ApplicationArguments args) {
        this.init();
    }

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

    private List<String> getScanPackages() {
        return this.dictI18nStarterProperties.getScanPackages().isEmpty()
                ? AutoConfigurationPackages.get(this.applicationContext)
                : this.dictI18nStarterProperties.getScanPackages();
    }

    @Override
    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final String dictNames = request.getParameter("dictNames");
        final String language = request.getParameter("language");
        final String includeBlankDescParam = request.getParameter("includeBlankDesc");
        final boolean includeBlankDesc = Boolean.parseBoolean(
                StringUtils.defaultIfBlank(includeBlankDescParam, "false")
        );

        final ResponseEntity<DictItemsResponse> result = this.getItems(dictNames, language, includeBlankDesc);

        this.writeWithMessageConverters(result, response);
    }

    private ResponseEntity<DictItemsResponse> getItems(final String dictNames, final String language, final boolean includeBlankDesc) {

        if (StringUtils.isBlank(dictNames)) {
            return ResponseEntity.ok(DictItemsResponse.empty());
        }

        final List<String> dictNameList = Arrays.stream(dictNames.split(","))
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());

        final String actualLanguage = StringUtils.isNotBlank(language) ? language : this.languageProvider.getCurrentLanguage();

        final Map<String, List<DictItemVO>> dictNameCodesMapping = new HashMap<>(dictNameList.size());
        for (String dictName : dictNameList) {
            final Dict[] dictArray = this.dictMap.getOrDefault(dictName, EMPTY_DICT_ARRAY);
            final List<DictItemVO> dictItemList = new ArrayList<>(dictArray.length);
            dictNameCodesMapping.put(dictName, dictItemList);
            for (final Dict dict : dictArray) {
                final Optional<String> text = this.compositeDictI18nProvider.getText(actualLanguage, dictName, dict.code());
                if (text.isPresent() || includeBlankDesc) {
                    dictItemList.add(new DictItemVO(dict.code(), text.orElse("")));
                }
            }
        }

        return ResponseEntity.ok(new DictItemsResponse(dictNameCodesMapping));
    }
}
