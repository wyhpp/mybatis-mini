package dataSource.druid;

import dataSource.DataSourceFactory;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;
import javax.sql.DataSource;
import java.util.Hashtable;
import java.util.Properties;

public class DruidDataSourceFactory implements DataSourceFactory {
    private DataSource dataSource;

    @Override
    public void setProperties(Properties var1) {
        if(this.dataSource == null){
            try {
                this.dataSource = com.alibaba.druid.pool.DruidDataSourceFactory.createDataSource(var1);
            } catch (Exception e) {
                throw new RuntimeException("create dataSource err",e);
            }
        }
    }

    @Override
    public DataSource getDataSource() {
        return this.dataSource;
    }
}
