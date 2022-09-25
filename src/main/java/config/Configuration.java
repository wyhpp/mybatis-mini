package config;

import binding.MapperRegistry;
import base.MappedStatement;
import dataSource.druid.DruidDataSourceFactory;
import dataSource.unpooled.UnpooledDataSource;
import dataSource.unpooled.UnpooledDataSourceFactory;
import executor.Executor;
import executor.SimpleExecutor;
import lombok.Data;
import mapping.Environment;
import sqlsession.SqlSession;
import transaction.Transaction;
import transaction.jdbcTransaction.JdbcTransactionFactory;
import type.TypeAliasRegistry;


import java.util.HashMap;
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
    /**
     * 类型别名注册器
     */
    protected final TypeAliasRegistry typeAliasRegistry;
    /**
     * 默认执行器类型
     */
    protected String defaultExecutorType;

    protected Environment environment;

    public Configuration(Properties variables, Map<String, MappedStatement> mappedStatements, MapperRegistry mapperRegistry) {
        this.variables = variables;
        this.mappedStatements = mappedStatements;
        this.mapperRegistry = mapperRegistry;
        this.typeAliasRegistry = new TypeAliasRegistry();
    }

    public Configuration() {
        this.mapperRegistry = new MapperRegistry();
        this.mappedStatements = new HashMap<>();
        this.typeAliasRegistry = new TypeAliasRegistry();
        this.defaultExecutorType = "simple";
        //注册事务类型和对应的类
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
        typeAliasRegistry.registerAlias("DRUID", DruidDataSourceFactory.class);
        typeAliasRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);
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

    /**
     * 新建一个Executor
     * @return
     */
    public Executor newExecutor(String executorType, Transaction transaction){
        executorType=executorType==null? this.defaultExecutorType : executorType;
        executorType=executorType==null? "simple" : executorType;

        Executor executor = null;
        if("simple".equals(executorType)){
            executor = new SimpleExecutor(this,transaction);
        }
        return executor;
    }
}
