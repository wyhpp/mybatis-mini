package executor;

import base.MappedStatement;
import executor.resultset.ResultHandler;
import mapping.BoundSql;
import transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 * @author wangyuhao
 */
public interface Executor {
    ResultHandler NO_RESULT_HANDLER = null;

    <E> List<E> query(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql);

    int update(MappedStatement ms, Object parameter) throws SQLException;

//    Transaction getTransaction();

    void commit(boolean required) throws SQLException;

//    void rollback(boolean required) throws SQLException;
//
//    void close(boolean forceRollback);
}
