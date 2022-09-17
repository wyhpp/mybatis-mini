package sqlsession;

import base.MappedStatement;
import config.Configuration;

/**
 * @author wangyuhao
 */
public class DefaultSqlSession implements SqlSession{
    /**
     * 配置类
     */
    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T> T selectOne(String statement) {
        return null;
    }

    @Override
    public <T> T selectOne(String statement, Object... param) {
        MappedStatement mappedStatement = this.configuration.getMappedStatement(statement);
        return (T) ("你被代理了！" + "\n方法：" + statement + "\n入参：" + param[0].toString() + "\n待执行sql:" + mappedStatement.getSql());
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return this.configuration.getMap(type,this);
    }
}
