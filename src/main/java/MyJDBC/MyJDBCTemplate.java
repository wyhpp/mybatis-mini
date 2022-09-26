package MyJDBC;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 封装JDBC操作
 * 1.获取连接
 * 2.传入sql模板，sql参数,得到prepareStatement
 * 3.执行操作
 * 4.释放连接
 * 5.返回结果
 * @author wangyuhao
 * @param <T>
 */
public class MyJDBCTemplate<T> {
    protected final Log logger = LogFactory.getLog(this.getClass());
    /**
     * 更新操作
     * @param sql
     * @param params
     * @return
     */
    public int update(String sql,List<Object> params) throws SQLException {
        //获取连接
        Connection connection = getConnection();
        //传入sql模板,获取ps
        PreparedStatement ps = getPreparedStatement(sql, params, connection);
        //执行操作（策略模式）
        class UpdatePolicy implements StatementCallback<Integer> {

            @Override
            public Integer doInStatement(Statement sm) throws SQLException {
                int rows = sm.executeUpdate(sql);
                if (logger.isTraceEnabled()){
                    logger.trace("SQL update affect " + rows + "rows");
                }
                return rows;
            }
        }
        Integer affRows = doExecute(new UpdatePolicy(), ps);

        //释放资源
        closeConnection(connection,ps,null);

        return affRows;
    }

    /**
     * 查询操作
     * @param sql
     * @param params
     * @return
     */
    public List<T> query(String sql, List<Object> params, RowMapper<T> rowMapper) throws SQLException {
        //获取连接
        Connection connection = getConnection();
        //传入sql模板,获取ps
        PreparedStatement ps = getPreparedStatement(sql, params, connection);
        //执行操作（策略模式）
        class QueryPolicy implements StatementCallback<List<T>> {
            @Override
            public List<T> doInStatement(Statement sm) throws SQLException {
                ResultSet rs = null;
                List<T> res;
                try{
                    rs = sm.executeQuery(sql);
                    res = parseResultSetToBean(rs, rowMapper);
                }finally {
                    if(rs != null){
                        rs.close();
                    }
                }
//                if (logger.isTraceEnabled()){
//                    logger.trace("SQL update affect " + rows + "rows");
//                }
                return res;
            }
        }

        List<T> result = doExecute(new QueryPolicy(), ps);

        //释放资源
        closeConnection(connection,ps,null);

        return result;
    }

    public List<T> query(String sql,List<Object> params,Class<T> clazz) throws SQLException {
        //获取连接
        Connection connection = getConnection();
        //传入sql模板,获取ps
        PreparedStatement ps = getPreparedStatement(sql, params, connection);
        //执行操作（策略模式）
        class QueryPolicy implements StatementCallback<List<T>> {
            @Override
            public List<T> doInStatement(Statement sm) throws SQLException {
                ResultSet rs = null;
                List<T> res = null;
                try{
                    rs = sm.executeQuery(sql);
                    res = parseResultSetToBean(rs,clazz);
                } catch (NoSuchFieldException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                } finally {
                    if(rs != null){
                        rs.close();
                    }
                }
                return res;
            }
        }

        List<T> result = doExecute(new QueryPolicy(), ps);

        //释放资源
        closeConnection(connection,ps,null);

        return result;
    }

    private static Connection getConnection() throws SQLException {
        //可抽象到配置文件中
        String url = "jdbc:mysql://localhost:3306/demo";
        String user = "root";
        String password = "123456";
        return DriverManager.getConnection(url, user, password);
    }

    private PreparedStatement getPreparedStatement(String sql,List<Object> params,Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        for (int i = 0; i < params.size(); i++) {
            preparedStatement.setObject(i+1,params.get(i));
        }

        return preparedStatement;
    }

    /**
     * 执行sql语句
     * @param action
     * @param <T>
     * @return
     */
    private <T> T doExecute(StatementCallback<T> action, Statement sm) throws SQLException {
        return action.doInStatement(sm);
    }

    private void closeConnection(Connection con,Statement sm,ResultSet rs) throws SQLException {
        if(rs != null){
            rs.close();
        }
        if(sm != null){
            sm.close();
        }
        if(con != null){
            con.close();
        }
    }

    private List<T> parseResultSetToBean(ResultSet rs, RowMapper<T> rowMapper) throws SQLException {
        List<T> result = new ArrayList<>();
        while(rs.next()){
            T obj = rowMapper.mapRow(rs);
            result.add(obj);
        }
        return result;
    }

    public static <T> List<T> parseResultSetToBean(ResultSet rs,Class<T> clazz) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException {
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
