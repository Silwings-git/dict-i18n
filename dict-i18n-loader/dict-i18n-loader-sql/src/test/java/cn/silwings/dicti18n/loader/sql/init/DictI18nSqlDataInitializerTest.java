package cn.silwings.dicti18n.loader.sql.init;

import cn.silwings.dicti18n.loader.enums.PreLoadMode;
import cn.silwings.dicti18n.loader.parser.DictInfo;
import cn.silwings.dicti18n.loader.sql.OrderStatus;
import cn.silwings.dicti18n.loader.sql.PayType;
import cn.silwings.dicti18n.loader.sql.SwitchDesc;
import cn.silwings.dicti18n.loader.sql.TestApp;
import cn.silwings.dicti18n.loader.sql.init.data.DictI18nSqlDataInitializer;
import cn.silwings.dicti18n.loader.sql.init.mjdbc.MockJdbcTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest(classes = TestApp.class)
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class DictI18nSqlDataInitializerTest {

    @Autowired
    private DictI18nSqlDataInitializer dictI18nSqlDataInitializer;

    @Autowired
    private MockJdbcTemplate mockJdbcTemplate;

    @Test
    public void testFullInitialize() {
        final Map<String, List<DictInfo>> dictMap = this.initData();
        this.dictI18nSqlDataInitializer.initialize(dictMap, PreLoadMode.FULL);
        dictMap.forEach((lang, dictList) -> {
            for (final DictInfo dictInfo : dictList) {
                final String desc = this.mockJdbcTemplate.getCache().get(lang + "." + dictInfo.getDictKey());
                Assertions.assertEquals(dictInfo.getDictDesc(), desc);
            }
        });
    }

    @Test
    public void testIncrementalInitialize() {
        final Map<String, List<DictInfo>> dictMap = this.initData();
        this.dictI18nSqlDataInitializer.initialize(dictMap, PreLoadMode.INCREMENTAL);
        dictMap.forEach((lang, dictList) -> {
            for (final DictInfo dictInfo : dictList) {
                final String desc = this.mockJdbcTemplate.getCache().get(lang + "." + dictInfo.getDictKey());
                Assertions.assertEquals(dictInfo.getDictDesc(), desc);
            }
        });
    }

    private Map<String, List<DictInfo>> initData() {
        final HashMap<String, List<DictInfo>> map = new HashMap<>();
        final List<DictInfo> enDictInfoList = Stream.of(
                        OrderStatus.PENDING,
                        OrderStatus.PROCESSING,
                        OrderStatus.SHIPPED,
                        OrderStatus.DELIVERED,
                        OrderStatus.CANCELLED,
                        PayType.ALIPAY,
                        PayType.WECHAT
                )
                .map(e -> mapToDictInfo(e, "en-us"))
                .collect(Collectors.toList());
        map.put("en-us", enDictInfoList);

        final List<DictInfo> cnDictInfoList = Stream.of(
                        OrderStatus.PENDING,
                        OrderStatus.PROCESSING,
                        OrderStatus.SHIPPED,
                        OrderStatus.DELIVERED,
                        OrderStatus.CANCELLED,
                        PayType.ALIPAY,
                        PayType.WECHAT
                )
                .map(e -> mapToDictInfo(e, "zh-cn"))
                .collect(Collectors.toList());
        map.put("zh-cn", cnDictInfoList);

        return map;
    }

    private static DictInfo mapToDictInfo(final SwitchDesc e, final String language) {
        final DictInfo dictInfo = new DictInfo();
        dictInfo.setDictKey(e.dictName() + "." + e.code());
        dictInfo.setDictDesc(e.desc(language));
        return dictInfo;
    }

}
