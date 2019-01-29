package vn.com.fwd.importtool.util;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

public class SpringPersistentUtils {
    public static DataSource getDataSource(DataSourceProperties properties) {
        if (StringUtils.isEmpty(properties.getJndiName())) {
            return properties.initializeDataSourceBuilder().build();
        } else {
            return new JndiDataSourceLookup().getDataSource(properties.getJndiName());
        }
    }
}
