package vn.com.fwd.importtool.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class PersistentConfig {
	@Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties authorizationDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("ods.datasource")
    public DataSourceProperties odsDataSourceProperties() {
        return new DataSourceProperties();
    }
}
