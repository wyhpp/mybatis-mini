package executor.resultset;

import MyJDBC.MyJDBCTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DefaultResultHandler implements ResultHandler{

    @Override
    public <E> List<E> handleResultSets(Statement statement,Class<?> resultType) throws SQLException {
        ResultSet resultSet = statement.getResultSet();
        if(resultSet != null){
            //处理结果集
            List<E> results = null;
            try {
                results = (List<E>) MyJDBCTemplate.parseResultSetToBean(resultSet, resultType);
            } catch (InstantiationException | IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
            return results;
        }
        return null;
    }
}
