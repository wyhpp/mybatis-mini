package dataSource.pooled;

import dataSource.unpooled.UnpooledDataSourceFactory;

import javax.sql.DataSource;

public class PooledDataSourceFactory extends UnpooledDataSourceFactory {
    @Override
    public DataSource getDataSource() {
        PooledDataSource pooledDataSource = new PooledDataSource();
        pooledDataSource.getDataSource().setDriver(properties.getProperty("driver"));
        pooledDataSource.getDataSource().setUrl(properties.getProperty("url"));
        pooledDataSource.getDataSource().setUsername(properties.getProperty("username"));
        pooledDataSource.getDataSource().setPassword(properties.getProperty("password"));
        pooledDataSource.getDataSource().setDriverProperties(properties);
        return pooledDataSource;
    }
}
