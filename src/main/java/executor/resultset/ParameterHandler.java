package executor.resultset;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author wangyuhao
 */
public interface ParameterHandler {
    void setParameters(PreparedStatement ps) throws SQLException;
}
