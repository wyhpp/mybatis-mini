package config;

import binding.MapperRegistry;
import base.MappedStatement;
import sqlsession.SqlSession;


import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author wangyuhao
 * Mybatis配置类
 */
public class Configuration {
    /**
     * properties标签属性
     */
    protected Properties variables;
    /**
     * MappedStatement集合，存放xml中的信息
     */
    protected Map<String, MappedStatement> mappedStatements;
    /**
     * mapper映射器
     */
    protected MapperRegistry mapperRegistry;

    public Configuration(Properties variables, Map<String, MappedStatement> mappedStatements, MapperRegistry mapperRegistry) {
        this.variables = variables;
        this.mappedStatements = mappedStatements;
        this.mapperRegistry = mapperRegistry;
    }

    public Configuration() {
        this.mapperRegistry = new MapperRegistry();
        this.mappedStatements = new HashMap<>();
    }

    public void addMappedStatement(MappedStatement mappedStatement){
        this.mappedStatements.put(mappedStatement.getId(),mappedStatement);
    }

    public void addMapper(Class<?> mapperInterface){
        this.mapperRegistry.addMap(mapperInterface);
    }

    public void addMappers(String packageName){
        this.mapperRegistry.addMappers(packageName);
    }

    public <T> T getMap(Class<T> type, SqlSession sqlSession){
        return this.mapperRegistry.getMap(type,sqlSession);
    }

    /**
     * 获取mappedStatement
     * @param id
     * @return
     */
    public MappedStatement getMappedStatement(String id) {
        return this.mappedStatements.get(id);
    }
}
