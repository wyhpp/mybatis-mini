package dataSource.pooled;

import dataSource.unpooled.UnpooledDataSourceFactory;

import javax.sql.DataSource;

public class PooledDataSourceFactory extends UnpooledDataSourceFactory {
    @Override
    public DataSource getDataSource() {
        PooledDataSource pooledDataSource = new PooledDataSource(
                properties.getProperty("driver"),properties.getProperty("url"),
                properties.getProperty("username"),properties.getProperty("password"));
        pooledDataSource.getDataSource().setDriverProperties(properties);
        return pooledDataSource;
    }
}
