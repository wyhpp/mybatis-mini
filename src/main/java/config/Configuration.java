package config;

import binding.MapperRegistry;
import lombok.Data;
import base.MappedStatement;


import java.util.Map;
import java.util.Properties;

/**
 * @author wangyuhao
 * Mybatis配置类
 */
@Data
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

    public Configuration() {}

    public void addMappedStatement(MappedStatement mappedStatement){
        mappedStatements.put(mappedStatement.getId(),mappedStatement);
    }

    public void addMapper(Class<?> mapperInterface){
        this.mapperRegistry.addMap(mapperInterface);
    }

    public void addMappers(String packageName){
        this.mapperRegistry.addMappers(packageName);
    }
}
