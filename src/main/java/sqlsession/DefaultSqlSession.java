package sqlsession;

import binding.MapperRegistry;

/**
 * @author wangyuhao
 */
public class DefaultSqlSession implements SqlSession{
    /**
     * 映射器
     */
    private MapperRegistry mapperRegistry;

    public DefaultSqlSession(MapperRegistry mapperRegistry) {
        this.mapperRegistry = mapperRegistry;
    }

    @Override
    public <T> T selectOne(String statement) {
        return null;
    }

    @Override
    public <T> T selectOne(String statement, Object param) {
        return (T) ("你被代理了！" + "方法：" + statement + " 入参：" + param);
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return mapperRegistry.getMap(type,this);
    }
}
