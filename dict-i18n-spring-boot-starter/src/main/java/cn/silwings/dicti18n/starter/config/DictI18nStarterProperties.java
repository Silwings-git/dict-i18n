package cn.silwings.dicti18n.starter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "dict-i18n.starter")
public class DictI18nStarterProperties {

    /**
     * A list of package paths to scan to find the {@link cn.silwings.dicti18n.dict.Dict} implementation class.
     */
    private List<String> scanPackages = new ArrayList<>();

    /**
     * Dict check configuration.
     */
    private DictCheck check = new DictCheck();

    /**
     * Endpoint configuration.
     */
    private Endpoint endpoint = new Endpoint();

    /**
     * Response enhancement configuration.
     */
    private Enhancer enhancer = new Enhancer();

    @Getter
    @Setter
    public static class DictCheck {

        /**
         * Whether to enable dict name unique check
         */
        private UniqueDictName uniqueDictName = new UniqueDictName();

        @Getter
        @Setter
        public static class UniqueDictName {

            /**
             * Check if the dictionary name is unique at startup.
             */
            private boolean enabled = true;

        }

    }

    @Getter
    @Setter
    public static class Endpoint {

        /**
         * Whether to enable endpoint
         */
        private boolean enabled = true;

        /**
         * Dict items endpoint configuration
         */
        private DictItems dictItems = new DictItems();

        /**
         * Dict names endpoint configuration
         */
        private DictNames dictNames = new DictNames();

        @Getter
        @Setter
        public static class DictItems {
            /**
             * Whether to enable dict items endpoint
             */
            private boolean enabled = true;

            /**
             * The path for the dict items endpoint.
             * Default is "/dict-i18n/dict-items".
             */
            private String path = "/dict-i18n/dict-items";
        }

        @Getter
        @Setter
        public static class DictNames {
            /**
             * Whether to enable dict items endpoint
             */
            private boolean enabled = true;

            /**
             * The path for the dict names endpoint.
             * Default is "/dict-i18n/dict-names".
             */
            private String path = "/dict-i18n/dict-names";
        }
    }

    @Getter
    @Setter
    public static class Enhancer {
        /**
         * Whether to enable global response enhancement.
         */
        private boolean enabled = true;

        /**
         * List of package names. Only responses with return types in these packages will be enhanced.
         * If not specified, defaults to the Spring component scanning base packages.
         */
        private List<String> includePackages = new ArrayList<>();

        /**
         * Fully qualified names of annotations.
         * If a class or method is annotated with any of these, it will be excluded from enhancement.
         */
        private List<String> excludeAnnotations = new ArrayList<>();
    }

}