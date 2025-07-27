package cn.silwings.dicti18n.loader.redis.init;

import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MockValueOperations implements ValueOperations<String, String> {

    private MockStringRedisTemplate mockStringRedisTemplate;

    public MockValueOperations(final MockStringRedisTemplate mockStringRedisTemplate) {
        this.mockStringRedisTemplate = mockStringRedisTemplate;
    }

    @Override
    public void set(final String key, final String value) {

    }

    @Override
    public void set(final String key, final String value, final long timeout, final TimeUnit unit) {

    }

    @Override
    public Boolean setIfAbsent(final String key, final String value) {
        return null;
    }

    @Override
    public Boolean setIfAbsent(final String key, final String value, final long timeout, final TimeUnit unit) {
        return null;
    }

    @Override
    public Boolean setIfPresent(final String key, final String value) {
        return null;
    }

    @Override
    public Boolean setIfPresent(final String key, final String value, final long timeout, final TimeUnit unit) {
        return null;
    }

    @Override
    public void multiSet(final Map<? extends String, ? extends String> map) {

    }

    @Override
    public Boolean multiSetIfAbsent(final Map<? extends String, ? extends String> map) {
        return null;
    }

    @Override
    public String get(final Object key) {
        return this.mockStringRedisTemplate.get(key);
    }

    @Override
    public String getAndDelete(final String key) {
        return "";
    }

    @Override
    public String getAndExpire(final String key, final long timeout, final TimeUnit unit) {
        return "";
    }

    @Override
    public String getAndExpire(final String key, final Duration timeout) {
        return "";
    }

    @Override
    public String getAndPersist(final String key) {
        return "";
    }

    @Override
    public String getAndSet(final String key, final String value) {
        return "";
    }

    @Override
    public List<String> multiGet(final Collection<String> keys) {
        return Collections.emptyList();
    }

    @Override
    public Long increment(final String key) {
        return 0L;
    }

    @Override
    public Long increment(final String key, final long delta) {
        return 0L;
    }

    @Override
    public Double increment(final String key, final double delta) {
        return 0.0;
    }

    @Override
    public Long decrement(final String key) {
        return 0L;
    }

    @Override
    public Long decrement(final String key, final long delta) {
        return 0L;
    }

    @Override
    public Integer append(final String key, final String value) {
        return 0;
    }

    @Override
    public String get(final String key, final long start, final long end) {
        return "";
    }

    @Override
    public void set(final String key, final String value, final long offset) {

    }

    @Override
    public Long size(final String key) {
        return 0L;
    }

    @Override
    public Boolean setBit(final String key, final long offset, final boolean value) {
        return null;
    }

    @Override
    public Boolean getBit(final String key, final long offset) {
        return null;
    }

    @Override
    public List<Long> bitField(final String key, final BitFieldSubCommands subCommands) {
        return Collections.emptyList();
    }

    @Override
    public RedisOperations<String, String> getOperations() {
        return null;
    }
}
