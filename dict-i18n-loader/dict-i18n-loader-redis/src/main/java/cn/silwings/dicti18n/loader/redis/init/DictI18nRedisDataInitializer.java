package cn.silwings.dicti18n.loader.redis.init;

import cn.silwings.dicti18n.loader.parser.DictFileParser;
import cn.silwings.dicti18n.loader.parser.DictInfo;
import cn.silwings.dicti18n.loader.redis.RedisDictI18nLoader;
import cn.silwings.dicti18n.loader.redis.config.RedisDictI18nLoaderProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DictI18nRedisDataInitializer {

    // Lua script supports both full and incremental modes for loading data into Redis
    private static final String SAVE_SCRIPT = "local mode = ARGV[1]\n" +
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

    private static final Logger log = LoggerFactory.getLogger(DictI18nRedisDataInitializer.class);
    private final RedisDictI18nLoaderProperties redisDictI18nLoaderProperties;
    private final RedisDictI18nLoader redisDictI18nLoader;
    private final DictFileParser dictFileParser;
    private final StringRedisTemplate redisTemplate;

    public DictI18nRedisDataInitializer(final RedisDictI18nLoaderProperties redisDictI18nLoaderProperties, final RedisDictI18nLoader redisDictI18nLoader, final DictFileParser dictFileParser, final StringRedisTemplate redisTemplate) {
        this.redisDictI18nLoaderProperties = redisDictI18nLoaderProperties;
        this.redisDictI18nLoader = redisDictI18nLoader;
        this.dictFileParser = dictFileParser;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Preload dictionary data into Redis when the application starts.
     */
    public void preload() {

        final Resource[] resources = this.redisDictI18nLoader.loadResourcesFromPattern(this.redisDictI18nLoaderProperties.getLocationPatterns());

        for (Resource resource : resources) {
            final String lang = this.redisDictI18nLoader.extractLangFromFilename(resource);
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

            final RedisScript<String> redisScript = RedisScript.of(SAVE_SCRIPT, String.class);
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
                final String errorMsg = "[DictInfo] Redis script execution failed. PreloadMode: " + this.redisDictI18nLoaderProperties.getPreload().getPreloadMode().name() + ", Result: " + result + ".";
                if (this.redisDictI18nLoaderProperties.getPreload().isFailFast()) {
                    throw new RedisSystemException(errorMsg, new RuntimeException("[DictI18n] Redis script execution failed."));
                } else {
                    log.warn(errorMsg);
                }
            }
        }
    }

    /**
     * Process dictionary keys to generate Redis storage keys
     */
    private String processKey(String lang, DictInfo dictInfo) {
        return this.redisDictI18nLoader.processKey(lang, dictInfo.getDictKey());
    }
}