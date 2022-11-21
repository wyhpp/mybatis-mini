package binding;

import sqlsession.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * mapper接口代理类
 * @author wangyuhao
 */
public class MapperProxy<T> implements InvocationHandler {
    private final SqlSession sqlSession;
    private final Class<T> mapperInterface;
    //缓存MappedMethod
    private Map<Method,MapperMethod> cachedMethod;

    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
        this.cachedMethod = new HashMap<>();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(Object.class.equals(method.getDeclaringClass())){
            return method.invoke(this,args);
        }else{
//            return sqlSession.selectOne(method.getDeclaringClass().getName() + "." + method.getName(),args);
            MapperMethod mapperMethod = cachedMapperMethod(method);
            return mapperMethod.execute(this.sqlSession,args);
        }
    }

    private MapperMethod cachedMapperMethod(Method method){
        MapperMethod mapperMethod = this.cachedMethod.get(method);
        if(mapperMethod == null){
            mapperMethod = new MapperMethod(mapperInterface,method,this.sqlSession.getConfiguration());
            cachedMethod.put(method,mapperMethod);
        }
        return mapperMethod;
    }
}
