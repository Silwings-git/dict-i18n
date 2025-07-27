package cn.silwings.dicti18n.loader.scan.parser;

import cn.silwings.dicti18n.loader.parser.DictInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DictInfoTest {

    @Test
    void isValidShouldReturnTrueWhenKeyAndDescAreValid() {
        // 有效场景：key和desc都非空且desc不为空字符串
        final DictInfo dictInfo = new DictInfo("test.key", "测试描述");
        assertTrue(dictInfo.isValid());
    }

    @Test
    void isValidShouldReturnFalseWhenKeyIsNull() {
        // key为null
        final DictInfo dictInfo = new DictInfo(null, "测试描述");
        assertFalse(dictInfo.isValid());
    }

    @Test
    void isValidShouldReturnFalseWhenDescIsNull() {
        // desc为null
        DictInfo dictInfo = new DictInfo("test.key", null);
        assertFalse(dictInfo.isValid());
    }

    @Test
    void isValidShouldReturnFalseWhenDescIsEmpty() {
        // desc为空字符串
        final DictInfo dictInfo = new DictInfo("test.key", "");
        assertFalse(dictInfo.isValid());
    }
}