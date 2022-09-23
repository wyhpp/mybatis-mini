package dataSource.druid;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.apache.ibatis.datasource.DataSourceFactory;

import javax.naming.Context;
import javax.naming.Name;
import javax.sql.DataSource;
import java.util.Hashtable;
import java.util.Properties;

/**
 * @author wangyuhao
 */
public class DruidDataSource implements DataSourceFactory {

    private Properties properties;

    @Override
    public void setProperties(Properties var1) {
        this.properties = var1;
    }

    @Override
    public DataSource getDataSource() {
        try {
            return DruidDataSourceFactory.createDataSource(this.properties);
        } catch (Exception e) {
            System.out.println("创建数据源失败");
            e.printStackTrace();
            return null;
        }
    }
}
