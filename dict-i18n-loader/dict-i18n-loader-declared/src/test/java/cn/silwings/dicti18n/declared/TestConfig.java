package cn.silwings.dicti18n.declared;

import cn.silwings.dicti18n.loader.scan.DictScanner;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
@EnableAutoConfiguration
public class TestConfig {

    @Bean
    public DictScanner dictScanner() {
        return new DictScanner();
    }

}
