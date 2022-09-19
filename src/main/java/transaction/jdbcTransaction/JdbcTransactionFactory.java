package transaction.jdbcTransaction;

import sqlsession.TransactionIsolationLevel;
import transaction.Transaction;
import transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author wangyuhao
 * jdbc事务工厂
 */
public class JdbcTransactionFactory implements TransactionFactory {
    public JdbcTransactionFactory() {
    }

    @Override
    public Transaction newTransaction(Connection conn) {
        return new JdbcTransaction(conn);
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new JdbcTransaction(dataSource,level,autoCommit);
    }
}
