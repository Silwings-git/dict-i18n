package cn.silwings.dicti18n.loader.parser.strategy;

import cn.silwings.dicti18n.loader.parser.DictInfo;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * Dictionary file parsing policy interface
 * <p>
 * It defines the specification for dictionary file parsing.
 * Different types of dictionary files (e.g., .properties, .json, etc.) can provide their own parsing logic by implementing this interface, enabling multi-strategy adaptation for dictionary file parsing.
 */
public interface DictFileParseStrategy {

    /**
     * Determine whether the current parsing strategy supports the specified resource file.
     *
     * @param resource Resource files to be parsed (such as files in the classpath, local files, etc.)
     * @return Return true if the resource file can be parsed, otherwise return false.
     */
    boolean supports(Resource resource);

    /**
     * Parse the resource file into a list of dictionary information
     *
     * @param resource The resource file to be parsed must be verified as supported through the {@link #supports(Resource)} method.
     * @return The list of dictionary information obtained after parsing ({@link DictInfo}), which may return an empty list if parsing fails or the file is empty
     */
    List<DictInfo> parse(Resource resource);

}
