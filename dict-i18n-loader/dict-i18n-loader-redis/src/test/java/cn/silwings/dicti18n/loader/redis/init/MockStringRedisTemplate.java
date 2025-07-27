package cn.silwings.dicti18n.loader.redis.init;

import cn.silwings.dicti18n.loader.enums.PreLoadMode;
import cn.silwings.dicti18n.loader.redis.config.RedisDictI18nLoaderProperties;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
public class MockStringRedisTemplate extends org.springframework.data.redis.core.StringRedisTemplate {

    private final RedisDictI18nLoaderProperties redisDictI18nLoaderProperties;
    private final List<MockRedisScript> scripts;
    private final Map<String, String> cache;

    public MockStringRedisTemplate(final RedisDictI18nLoaderProperties redisDictI18nLoaderProperties) {
        this.redisDictI18nLoaderProperties = redisDictI18nLoaderProperties;
        this.scripts = new CopyOnWriteArrayList<>();
        this.cache = new ConcurrentHashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T execute(final RedisScript<T> script, final List<String> keys, final Object... args) {
        this.scripts.add(new MockRedisScript(script.getScriptAsString(), keys, Arrays.asList(args)));
        final PreLoadMode preLoadMode = PreLoadMode.valueOf(args[0].toString());
        final List<String> kvList = Arrays.stream(args).skip(1).map(Object::toString).collect(Collectors.toList());

        if (PreLoadMode.FULL.equals(preLoadMode)) {
            IntStream.range(0, kvList.size() / 2)
                    .mapToObj(i -> kvList.subList(i * 2, i * 2 + 2))
                    .forEach(kv -> {
                        final String key = kv.get(0);
                        final String value = kv.get(1);
                        this.cache.put(this.redisDictI18nLoaderProperties.processKey((String) key), value);
                    });
        } else if (PreLoadMode.INCREMENTAL.equals(preLoadMode)) {
            IntStream.range(0, kvList.size() / 2)
                    .mapToObj(i -> kvList.subList(i * 2, i * 2 + 2))
                    .filter(kv -> !this.cache.containsKey(kv.get(0)))
                    .forEach(kv -> {
                        final String key = kv.get(0);
                        final String value = kv.get(1);
                        this.cache.put(this.redisDictI18nLoaderProperties.processKey((String) key), value);
                    });
        } else {
            throw new IllegalArgumentException("Invalid PreLoadMode: " + preLoadMode);
        }

        return (T) "OK";
    }

    public String get(final Object key) {
        if (key instanceof String) {
            return this.cache.get((String) key);
        }
        return null;
    }

    @Data
    public static class MockRedisScript {
        private String script;
        private List<String> keys;
        private List<Object> args;

        public MockRedisScript(String script, List<String> keys, List<Object> args) {
            this.script = script;
            this.keys = keys;
            this.args = args;
        }
    }

    @Override
    public ValueOperations<String, String> opsForValue() {
        return new MockValueOperations(this);
    }

}
