package cn.silwings.dicti18n.loader.sql.schema;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public class SchemaInitRunner implements ApplicationRunner {

    private final SchemaInitializer schemaInitializer;

    public SchemaInitRunner(final SchemaInitializer schemaInitializer) {
        this.schemaInitializer = schemaInitializer;
    }

    @Override
    public void run(final ApplicationArguments args) {
        this.schemaInitializer.initialize();
    }
}
