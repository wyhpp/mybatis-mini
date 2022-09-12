package MyJDBC;

import com.sun.istack.internal.Nullable;

import java.sql.SQLException;
import java.sql.Statement;

public interface StatementCallback<T> {
    /**
     * 根据传入动作执行sql操作
     * @param sm
     * @return
     * @throws SQLException
     */
    @Nullable
    T doInStatement(Statement sm) throws SQLException;
}
