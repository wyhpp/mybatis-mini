package sqlsession;

import binding.MapperRegistry;
import config.Configuration;

/**
 * @author wangyuhao
 * 默认sqlSession类
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory{

    private Configuration configuration;


    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(this.configuration);
    }
}
