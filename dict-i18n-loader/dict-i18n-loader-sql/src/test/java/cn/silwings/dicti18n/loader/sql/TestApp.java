package cn.silwings.dicti18n.loader.sql;

import cn.silwings.dicti18n.loader.sql.init.mjdbc.MockJdbcTemplate;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestApp {

    @Bean
    public MockJdbcTemplate mockJdbcTemplate() {
        return new MockJdbcTemplate();
    }

}
