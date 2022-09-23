package dataSource;

import javax.naming.spi.ObjectFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author wangyuhao
 */
public interface DataSourceFactory{
    void setProperties(Properties var1);

    DataSource getDataSource();
}
