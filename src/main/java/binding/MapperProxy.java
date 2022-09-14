package binding;

import sqlsession.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * mapper接口代理类
 * @author wangyuhao
 */
public class MapperProxy<T> implements InvocationHandler {
    private final SqlSession sqlSession;
//    private Map<String,String> sqlSession;
    private final Class<T> mapperInterface;

    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(Object.class.equals(method.getDeclaringClass())){
            return method.invoke(this,args);
        }else{
            return sqlSession.selectOne(method.toString(),args);
        }
    }
}
