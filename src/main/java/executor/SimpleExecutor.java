package executor;

import base.MappedStatement;
import config.Configuration;
import executor.resultset.ResultHandler;
import executor.statement.StatementHandler;
import mapping.BoundSql;
import transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 *  模板模式
 * @author wangyuhao
 */
public class SimpleExecutor extends BaseExecutor{

    public SimpleExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    /**
     * 实现查询方法
     * @param ms mappedStatement
     * @param parameter 参数
     * @param resultHandler 结果处理器
     * @param boundSql 预处理sql语句
     * @param <E>
     * @return
     */
    @Override
    protected <E> List<E> doQuery(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        Configuration configuration = ms.getConfiguration();
        StatementHandler handler = configuration.newStatementHandler(this,ms,parameter,resultHandler,boundSql);
        Connection connection = null;
        try {
            connection = transaction.getConnection();
            Statement statement= handler.prepare(connection,100);
            handler.parameterize(statement);
            return handler.query(statement,resultHandler);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
