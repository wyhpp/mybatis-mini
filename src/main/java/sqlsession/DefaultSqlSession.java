package sqlsession;

import base.MappedStatement;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import config.Configuration;
import executor.Executor;
import executor.resultset.DefaultResultHandler;
import executor.resultset.ResultHandler;
import lombok.Data;
import mapping.BoundSql;
import mapping.Environment;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangyuhao
 */
public class DefaultSqlSession implements SqlSession{
    /**
     * 配置类
     */
    private final Configuration configuration;

    private Logger logger = LoggerFactory.getLogger(DefaultSqlSession.class);
    /**
     * 执行器
     */
    private Executor executor;

    public DefaultSqlSession(Configuration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

//    @Override
//    public <T> T selectOne(String statement) {
//        return null;
//    }
//
//    @Override
//    public <T> T selectOne(String statement, Object param) {
//        MappedStatement mappedStatement = this.configuration.getMappedStatement(statement);
////        return (T) ("你被代理了！" + "\n方法：" + statement + "\n入参：" + param[0].toString() + "\n待执行sql:" + mappedStatement.getSql());
//
//        ResultHandler resultHandler = new DefaultResultHandler();
//        List<T> results = executor.query(mappedStatement, param, resultHandler, mappedStatement.getSqlSource().getBoundSql(param));
//        return results.get(0);
////        //交给执行器去做
////        Environment environment = configuration.getEnvironment();
////        try {
////            Connection connection = environment.getDataSource().getConnection();
////            logger.info("获取connection，name is:"+ connection);
////            BoundSql boundSql = mappedStatement.getBoundSql();
////            PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSql());
////            preparedStatement.setLong(1, Long.parseLong(((Object[]) param)[0].toString()));
////            ResultSet resultSet = preparedStatement.executeQuery();
////
////            List<T> objList = (List<T>) parseResultSetToBean(resultSet, Class.forName(boundSql.getResultType()));
////            return objList.get(0);
////        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchFieldException exception) {
////            exception.printStackTrace();
////        }
////        return null;
//    }

    @Override
    public <T> T selectOne(String statement) {
        return this.selectOne(statement, null);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        List<T> list = this.<T>selectList(statement, parameter);
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() > 1) {
            throw new RuntimeException("Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
        } else {
            return null;
        }
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter) {
        logger.debug("执行查询 statement：{} parameter：{}", statement,parameter);
        MappedStatement ms = configuration.getMappedStatement(statement);
        DefaultResultHandler resultHandler = new DefaultResultHandler();
        return executor.query(ms, parameter, resultHandler, ms.getSqlSource().getBoundSql(parameter));
    }

    @Override
    public int insert(String statement, Object parameter) {
        // 在 Mybatis 中 insert 调用的是 update
        return update(statement, parameter);
    }

    @Override
    public int update(String statement, Object parameter) {
        MappedStatement ms = configuration.getMappedStatement(statement);
        try {
            return executor.update(ms, parameter);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating database.  Cause: " + e);
        }
    }

    @Override
    public Object delete(String statement, Object parameter) {
        return update(statement, parameter);
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return this.configuration.getMap(type,this);
    }

    @Override
    public Configuration getConfiguration() {
        return this.configuration;
    }

//    private <T> List<T>  parseResultSetToBean(ResultSet rs,Class<T> clazz) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException {
//        List<T> result = new ArrayList<>();
//        while(rs.next()){
//            //创建bean
//            T bean = clazz.newInstance();
//            ResultSetMetaData metaData = rs.getMetaData();
//            int columnCount = metaData.getColumnCount();
//            for (int i = 0; i < columnCount; i++) {
//                String columnName = metaData.getColumnName(i+1);
//                Object value = rs.getObject(columnName);
//                Field field = clazz.getDeclaredField(columnName);
//                field.setAccessible(true);
//                field.set(bean,value);
//            }
//            result.add(bean);
//        }
//        return result;
//    }
}
