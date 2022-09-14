package sqlsession;

import binding.MapperRegistry;
import config.Configuration;

/**
 * @author wangyuhao
 * 默认sqlSession类
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory{

    private Configuration configuration;

    private final MapperRegistry mapperRegistry;

    public DefaultSqlSessionFactory(MapperRegistry mapperRegistry) {
        this.mapperRegistry = mapperRegistry;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(mapperRegistry);
    }
}
