package cn.silwings.dicti18n.loader.redis.init;

import cn.silwings.dicti18n.loader.redis.TestApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = TestApp.class)
public class DictI18nRedisDataInitializerTest {

    private static final String EXPECT_SCRIPT = "local mode = ARGV[1]\n" +
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

    private static final List<List<String>> EXPECT_ARGS = Arrays.asList(Arrays.asList(
                    "INCREMENTAL",
                    "dict_i18n:en-us:pay.pay_type.wechat",
                    "WeChat Pay",
                    "dict_i18n:en-us:order_status.cancelled",
                    "Cancelled",
                    "dict_i18n:en-us:order_status.processing",
                    "Processing",
                    "dict_i18n:en-us:order_status.delivered",
                    "Delivered",
                    "dict_i18n:en-us:pay.pay_type.alipay",
                    "Alipay",
                    "dict_i18n:en-us:order_status.shipped",
                    "Shipped",
                    "dict_i18n:en-us:order_status.pending",
                    "Pending"
            ),
            Arrays.asList(
                    "INCREMENTAL",
                    "dict_i18n:zh-cn:pay.pay_type.wechat",
                    "微信支付",
                    "dict_i18n:zh-cn:order_status.cancelled",
                    "已取消",
                    "dict_i18n:zh-cn:order_status.processing",
                    "处理中",
                    "dict_i18n:zh-cn:order_status.delivered",
                    "已送达",
                    "dict_i18n:zh-cn:pay.pay_type.alipay",
                    "支付宝",
                    "dict_i18n:zh-cn:order_status.shipped",
                    "已发货",
                    "dict_i18n:zh-cn:order_status.pending",
                    "待处理"
            ));

    @Autowired
    private DictI18nRedisDataInitializer dictI18nRedisDataInitializer;

    @Autowired
    private MockStringRedisTemplate stringRedisTemplate;

    @Test
    public void testPreload() {
        this.dictI18nRedisDataInitializer.preload();
        for (int i = 0; i < this.stringRedisTemplate.getScripts().size(); i++) {
            final MockStringRedisTemplate.MockRedisScript script = this.stringRedisTemplate.getScripts().get(i);
            assertEquals(EXPECT_SCRIPT, script.getScript());
            assertTrue(script.getKeys().isEmpty());
            assertEquals(EXPECT_ARGS.get(i % 2), script.getArgs());
        }
    }
}
