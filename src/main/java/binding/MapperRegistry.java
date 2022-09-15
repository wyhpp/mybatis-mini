package binding;

import cn.hutool.core.lang.ClassScanner;
import sqlsession.SqlSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author wangyuhao
 * 获取已添加的mapperproxy
 */
public class MapperRegistry {
    /**
     * 已知mapperProxy
     */
    private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap();

    /**
     * 获取对应接口的代理对象
     * @param type
     * @param sqlSession
     * @param <T>
     * @return
     */
    public <T> T getMap(Class<T> type, SqlSession sqlSession){
        MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
        if(mapperProxyFactory == null){
            throw new RuntimeException("Type " + type + " is not known to the MapperRegistry.");
        }
        return mapperProxyFactory.newInstance(sqlSession);
    }

    /**
     * 添加单接口映射
     * @param type
     * @param <T>
     */
    public <T> void addMap(Class<T> type){
        //mapper必须是接口
        if(type.isInterface()){
            //重复添加则报错
            if(hasMapper(type)){
                throw new RuntimeException("Type " + type + " is already exist in MapperRegistry.");
            }
            knownMappers.put(type,new MapperProxyFactory<>(type));
        }
    }

    public <T> boolean hasMapper(Class<T> type) {
        return this.knownMappers.containsKey(type);
    }

    /**
     * 添加包目录下所有接口的映射
     * @param packageName
     */
    public void addMappers(String packageName) {
        Set<Class<?>> mapperSet = ClassScanner.scanPackage(packageName);
        for (Class<?> mapperClass : mapperSet) {
            addMap(mapperClass);
        }
    }

}
