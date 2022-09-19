package mapping;


import transaction.TransactionFactory;

import javax.sql.DataSource;

/**
 * @author wangyuhao
 * Environment 节点信息
 */
public class Environment {
    private final String id;
    private final TransactionFactory transactionFactory;
    private final DataSource dataSource;

    public Environment(String id, TransactionFactory transactionFactory, DataSource dataSource) {
        if (id == null) {
            throw new IllegalArgumentException("Parameter 'id' must not be null");
        } else if (transactionFactory == null) {
            throw new IllegalArgumentException("Parameter 'transactionFactory' must not be null");
        } else {
            this.id = id;
            if (dataSource == null) {
                throw new IllegalArgumentException("Parameter 'dataSource' must not be null");
            } else {
                this.transactionFactory = transactionFactory;
                this.dataSource = dataSource;
            }
        }
    }

    public String getId() {
        return this.id;
    }

    public TransactionFactory getTransactionFactory() {
        return this.transactionFactory;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public static class Builder{
        private final String id;
        private TransactionFactory transactionFactory;
        private DataSource dataSource;

        public Builder(String id) {
            this.id = id;
        }

        public Environment.Builder transactionFactory(TransactionFactory transactionFactory) {
            this.transactionFactory = transactionFactory;
            return this;
        }

        public Environment.Builder dataSource(DataSource dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public Environment build(){
            return new Environment(this.id,this.transactionFactory,this.dataSource);
        }
    }
}
