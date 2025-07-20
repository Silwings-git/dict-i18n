package cn.silwings.dicti18n.loader.parser.strategy;

import cn.silwings.dicti18n.loader.parser.DictInfo;
import org.springframework.core.io.Resource;

import java.util.List;

public interface DictFileParseStrategy {

    /**
     * Is this resource supported?
     */
    boolean supports(Resource resource);

    /**
     * Parse resources into a list of DictInfo
     */
    List<DictInfo> parse(Resource resource);

}
