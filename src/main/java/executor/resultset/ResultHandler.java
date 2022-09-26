package executor.resultset;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author wangyuhao
 */
public interface ResultHandler {
    public <E> List<E> handleResultSets(Statement statement,Class<?> resultType) throws SQLException;
}
