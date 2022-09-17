package sqlsession;

import config.Configuration;
import config.XMLConfigBuilder;

import java.io.InputStream;
import java.io.Reader;

/**
 * @author wangyuhao
 * 建造者模式
 * factory建造者
 */
public class SqlSessionFactoryBuilder {

    public void SqlSessionFactoryBuilder(){

    }

//    /**
//     * 根据config.xml获取配置信息
//     * @param inputStream
//     * @return
//     */
//    public SqlSessionFactory build(InputStream inputStream){
//        return new DefaultSqlSessionFactory();
//    }

    public SqlSessionFactory build(Reader reader) {
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(reader);
        return build(xmlConfigBuilder.parse());
    }

    public SqlSessionFactory build(Configuration config) {
        return new DefaultSqlSessionFactory(config);
    }
}
