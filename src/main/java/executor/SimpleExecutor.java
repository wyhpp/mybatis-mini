package executor;

import base.MappedStatement;
import config.Configuration;
import executor.resultset.ResultHandler;
import mapping.BoundSql;
import transaction.Transaction;

import java.util.List;

/**
 *  模板模式
 * @author wangyuhao
 */
public class SimpleExecutor extends BaseExecutor{

    public SimpleExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    @Override
    protected <E> List<E> doQuery(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        return null;
    }
}
