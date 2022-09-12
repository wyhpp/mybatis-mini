package sqlsession;

import java.io.InputStream;

/**
 * @author wangyuhao
 * 建造者模式
 * factory建造者
 */
public class SqlSessionFactoryBuilder {

    public void SqlSessionFactoryBuilder(){

    }

    /**
     * 根据config.xml获取配置信息
     * @param inputStream
     * @return
     */
    public SqlSessionFactory build(InputStream inputStream){
        return new DeafaultSqlSession();
    }
}
