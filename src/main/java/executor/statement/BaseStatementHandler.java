package executor.statement;

import base.MappedStatement;
import config.Configuration;
import executor.Executor;
import executor.resultset.ResultHandler;
import mapping.BoundSql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class BaseStatementHandler implements StatementHandler {

    protected final Configuration configuration;
    protected final Executor executor;
    protected final MappedStatement mappedStatement;

    protected final Object parameterObject;
    protected final ResultHandler resultHandler;

    protected BoundSql boundSql;

    protected BaseStatementHandler(Configuration configuration, Executor executor, MappedStatement mappedStatement, Object parameterObject, ResultHandler resultHandler,BoundSql boundSql) {
        this.configuration = configuration;
        this.executor = executor;
        this.mappedStatement = mappedStatement;
        this.parameterObject = parameterObject;
        this.resultHandler = resultHandler;
        this.boundSql = boundSql;
    }

    @Override
    public Statement prepare(Connection connection,Integer transactionTimeout) throws SQLException{
        Statement statement = null;
        try {
            statement = this.instantiateStatement(connection);
            statement.setQueryTimeout(transactionTimeout);
            statement.setFetchSize(10000);
            return statement;
        } catch (SQLException e) {
            this.closeStatement(statement);
            throw e;
        }

    }

    /**
     * 实例化Statement
     * @param connection
     * @return
     */
    protected abstract Statement instantiateStatement(Connection connection) throws SQLException;

    protected void closeStatement(Statement statement){
        if(statement != null){
            try {
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
