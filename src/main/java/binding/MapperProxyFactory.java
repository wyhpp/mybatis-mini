package binding;

import sqlsession.SqlSession;

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author wangyuhao
 */
public class MapperProxyFactory<T> {
    private Class<T> mapperInterface;

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public T newInstance(SqlSession sqlSession){
        //生成代理类对象
        MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession,mapperInterface);
        //生成Mapper接口的代理对象
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface},mapperProxy);
    }
}
