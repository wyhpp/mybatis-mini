package transaction.jdbcTransaction;


import sqlsession.TransactionIsolationLevel;
import transaction.Transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author wangyuhao
 * jdbc事务管理类
 */
public class JdbcTransaction implements Transaction {

    protected Connection connection;
    protected DataSource dataSource;
    protected TransactionIsolationLevel level;
    protected boolean autoCommit;

    public JdbcTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        this.dataSource = dataSource;
        this.level = level;
        this.autoCommit = autoCommit;
    }

    public JdbcTransaction(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Connection getConnection() throws SQLException {
        connection = dataSource.getConnection();
        connection.setTransactionIsolation(level.getLevel());
        connection.setAutoCommit(autoCommit);
        return connection;
    }

    @Override
    public void commit() throws SQLException {
        if (this.connection != null && !this.connection.getAutoCommit()) {
            this.connection.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (this.connection != null && !this.connection.getAutoCommit()) {
            this.connection.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        if (this.connection != null) {
            this.resetAutoCommit();
            this.connection.close();
        }
    }

    protected void resetAutoCommit() {
        try {
            if (!this.connection.getAutoCommit()) {
                this.connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
           e.printStackTrace();
        }

    }
}
