package cn.silwings.dicti18n.loader.redis;


import cn.silwings.dicti18n.loader.ClassPathDictI18nLoader;
import cn.silwings.dicti18n.loader.parser.DictFileParser;
import cn.silwings.dicti18n.loader.parser.DictInfo;
import cn.silwings.dicti18n.loader.redis.config.RedisDictI18nLoaderProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RedisDictI18nLoader implements ClassPathDictI18nLoader {

    private static final String SCRIPT = "local mode = ARGV[1]\n" +
            "for i = 2, #ARGV, 2 do\n" +
            "    local key = ARGV[i]\n" +
            "    local value = ARGV[i + 1]\n" +
            "    if mode == \"FULL\" then\n" +
            "        redis.call(\"SET\", key, value)\n" +
            "    elseif mode == \"INCREMENTAL\" then\n" +
            "        if redis.call(\"EXISTS\", key) == 0 then\n" +
            "            redis.call(\"SET\", key, value)\n" +
            "        end\n" +
            "    else\n" +
            "        return redis.error_reply(\"Invalid mode: \" .. mode)\n" +
            "    end\n" +
            "end\n" +
            "return \"OK\"";

    private final Logger log = LoggerFactory.getLogger(RedisDictI18nLoader.class);

    private final RedisDictI18nLoaderProperties redisDictI18nLoaderProperties;

    private final StringRedisTemplate redisTemplate;

    private final DictFileParser dictFileParser;

    public RedisDictI18nLoader(final RedisDictI18nLoaderProperties redisDictI18nLoaderProperties, final StringRedisTemplate redisTemplate, final DictFileParser dictFileParser) {
        this.redisDictI18nLoaderProperties = redisDictI18nLoaderProperties;
        this.redisTemplate = redisTemplate;
        this.dictFileParser = dictFileParser;
    }

    @Override
    public Logger getLog() {
        return log;
    }

    @PostConstruct
    public void preload() {

        if (!this.redisDictI18nLoaderProperties.getPreload().isEnabled()) {
            log.info("[DictI18n] Redis preload is disabled.");
            return;
        }

        final Resource[] resources = this.loadResourcesFromPattern(this.redisDictI18nLoaderProperties.getLocationPatterns());

        for (Resource resource : resources) {
            final String lang = this.extractLangFromFilename(resource);
            if (null == lang) {
                continue;
            }
            final List<DictInfo> dictInfoList = this.dictFileParser.parse(resource)
                    .stream()
                    .filter(DictInfo::isValid)
                    .collect(Collectors.toList());
            if (dictInfoList.isEmpty()) {
                continue;
            }

            final List<String> args = new ArrayList<>();
            args.add(this.redisDictI18nLoaderProperties.getPreload().getPreloadMode().name());
            dictInfoList.forEach(dictInfo -> {
                final String dictKey = this.processKey(lang, dictInfo);
                args.add(dictKey);
                args.add(dictInfo.getDictDesc());
            });

            final RedisScript<String> redisScript = RedisScript.of(SCRIPT, String.class);
            final String result;
            try {
                result = this.redisTemplate.execute(redisScript, Collections.emptyList(), args.toArray());
            } catch (Exception e) {
                if (this.redisDictI18nLoaderProperties.getPreload().isFailFast()) {
                    throw new RedisSystemException("[DictI18n] Failed to execute Redis script due to connection error.", e);
                } else {
                    log.warn("[DictI18n] Redis connection failed: {}", e.getMessage(), e);
                    return;
                }
            }
            if ("OK".equals(result)) {
                log.info("[DictI18n] Preloaded {} entries for language: {}", dictInfoList.size(), lang.isEmpty() ? "default" : lang);
            } else {
                final String errorMsg = "Redis script execution failed. PreloadMode: " + this.redisDictI18nLoaderProperties.getPreload().getPreloadMode().name() + ", Result: " + result + ".";
                if (this.redisDictI18nLoaderProperties.getPreload().isFailFast()) {
                    throw new RedisSystemException(errorMsg, new RuntimeException("[DictI18n] Redis script execution failed."));
                } else {
                    log.warn(errorMsg);
                }
            }
        }
    }

    private String processKey(String lang, DictInfo dictInfo) {
        return this.processKey(lang, dictInfo.getDictKey());
    }

    private String processKey(String lang, String dictKey) {
        return this.redisDictI18nLoaderProperties.getKeyPrefix() + lang.toLowerCase() + ":" + this.redisDictI18nLoaderProperties.processKey(dictKey);
    }

    @Override
    public String loaderName() {
        return "redis";
    }

    @Override
    public Optional<String> get(final String lang, final String dictKey) {
        final String redisKey = this.processKey(lang, dictKey);
        return Optional.ofNullable(this.redisTemplate.opsForValue().get(redisKey));
    }

}