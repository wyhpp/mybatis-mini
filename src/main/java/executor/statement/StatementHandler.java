package executor.statement;

import executor.resultset.ResultHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 语句处理器
 * 进行准备语句、参数化传递、执行 SQL、封装结果的处理
 * 模板模式
 */
public interface StatementHandler {

    /** 准备语句 */
    Statement prepare(Connection connection,Integer transactionTimeout) throws SQLException;

    /**
     *处理参数
     */
    void parameterize(Statement statement) throws SQLException;

    /** 执行查询 */
    <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException;
}
