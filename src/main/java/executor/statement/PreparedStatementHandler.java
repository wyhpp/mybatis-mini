package executor.statement;

import base.MappedStatement;
import config.Configuration;
import executor.Executor;
import executor.resultset.ResultHandler;
import mapping.BoundSql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author wangyuhao
 */
public class PreparedStatementHandler extends BaseStatementHandler {

    public PreparedStatementHandler(Configuration configuration, Executor executor,
                                    MappedStatement mappedStatement, Object parameterObject,
                                    ResultHandler resultHandler, BoundSql boundSql) {
        super(configuration, executor, mappedStatement, parameterObject, resultHandler, boundSql);
    }

    @Override
    protected Statement instantiateStatement(Connection connection) throws SQLException {
        String sql = boundSql.getSql();
        return connection.prepareStatement(sql);
    }

    @Override
    public void parameterize(Statement statement) throws SQLException {
        PreparedStatement ps = (PreparedStatement) statement;
        this.parameterHandler.setParameters(ps);
//        ps.setLong(1, Long.parseLong(((Object[]) parameterObject)[0].toString()));
    }

    @Override
    public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.executeQuery();
        List<E> results = null;
        results = resultHandler.handleResultSets(ps, mappedStatement.getResultType());

        return results;
    }

    @Override
    public int update(Statement statement) throws SQLException {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.executeUpdate();
        return ps.getUpdateCount();
    }
}
