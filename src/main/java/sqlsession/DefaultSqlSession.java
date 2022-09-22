package sqlsession;

import base.MappedStatement;
import config.Configuration;
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
//        return (T) ("你被代理了！" + "\n方法：" + statement + "\n入参：" + param[0].toString() + "\n待执行sql:" + mappedStatement.getSql());
        Environment environment = configuration.getEnvironment();

        try {
            Connection connection = environment.getDataSource().getConnection();
            BoundSql boundSql = mappedStatement.getBoundSql();
            PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSql());
            preparedStatement.setLong(1, Long.parseLong(((Object[]) param)[0].toString()));
            ResultSet resultSet = preparedStatement.executeQuery();

            List<T> objList = (List<T>) parseResultSetToBean(resultSet, Class.forName(boundSql.getResultType()));
            return objList.get(0);
        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchFieldException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return this.configuration.getMap(type,this);
    }

    private <T> List<T>  parseResultSetToBean(ResultSet rs,Class<T> clazz) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        List<T> result = new ArrayList<>();
        while(rs.next()){
            //创建bean
            T bean = clazz.newInstance();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                String columnName = metaData.getColumnName(i+1);
                Object value = rs.getObject(columnName);
                // TODO column_name要与fieldName一致，目前不支持驼峰
                Field field = clazz.getDeclaredField(columnName);
                field.setAccessible(true);
                field.set(bean,value);
            }
            result.add(bean);
        }
        return result;
    }
}
