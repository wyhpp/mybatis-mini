package sqlsession;

import binding.MapperRegistry;
import config.Configuration;
import executor.Executor;
import mapping.Environment;
import transaction.Transaction;
import transaction.TransactionFactory;

/**
 * @author wangyuhao
 * 默认sqlSession类
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory{

    private Configuration configuration;


    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        //暂时不需要事务
        Transaction tx = null;
        final Environment environment = configuration.getEnvironment();
        TransactionFactory transactionFactory = environment.getTransactionFactory();
        tx = transactionFactory.newTransaction(configuration.getEnvironment().getDataSource(), TransactionIsolationLevel.READ_COMMITTED, true);
        //创建执行器
        Executor executor = this.configuration.newExecutor("simple",tx);
        //创建DefaultSqlSession
        return new DefaultSqlSession(this.configuration,executor);
    }
}
