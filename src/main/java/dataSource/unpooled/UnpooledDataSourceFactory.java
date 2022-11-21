package dataSource.unpooled;

import dataSource.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

public class UnpooledDataSourceFactory implements DataSourceFactory {

    private UnpooledDataSource unpooledDataSource = new UnpooledDataSource();

    protected Properties properties;

    @Override
    public void setProperties(Properties var1) {
        properties = var1;
    }

    @Override
    public DataSource getDataSource() {
        unpooledDataSource.setDriver(properties.getProperty("driver"));
        unpooledDataSource.setUrl(properties.getProperty("url"));
        unpooledDataSource.setUsername(properties.getProperty("username"));
        unpooledDataSource.setPassword(properties.getProperty("password"));
        unpooledDataSource.setDriverProperties(properties);
        return this.unpooledDataSource;
    }
}
