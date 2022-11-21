package dataSource.pooled;

import lombok.Data;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

@Data
public class PooledConnection implements InvocationHandler {
    private final Connection realConnection;
    private final Connection proxyConnection;
    private final PooledDataSource dataSource;
    private long checkoutTimestamp;
    private long lastUsedTimestamp;
    private long createdTimestamp;
    private boolean valid = true;
    private int connectionTypeCode;
    private int hashcode;

    public PooledConnection(Connection realConnection, PooledDataSource dataSource) {
        this.realConnection = realConnection;
        this.dataSource = dataSource;
        this.hashcode = realConnection.hashCode();
        this.createdTimestamp = System.currentTimeMillis();
        this.lastUsedTimestamp = System.currentTimeMillis();
        this.proxyConnection = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(),new Class[]{Connection.class},this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        // 如果是调用 CLOSE 关闭链接方法，则将链接加入连接池中，并返回null
        if ("close".hashCode() == methodName.hashCode() && "close".equals(methodName)) {
            this.dataSource.pushConnection(this);
            return null;
        } else {
            if (!Object.class.equals(method.getDeclaringClass())) {
                // 除了toString()方法，其他方法调用之前要检查connection是否还是合法的,不合法要抛出SQLException
                checkConnection();
            }
            // 其他方法交给connection去调用
            return method.invoke(realConnection, args);
        }
    }

    private void checkConnection() throws SQLException {
        if(!this.isValid()){
            throw new SQLException("Error accessing pooled connection, connection is invalid");
        }
    }

    public Connection getRealConnection(){
        return this.realConnection;
    }

    public long getCheckoutTime() {
        return System.currentTimeMillis()-checkoutTimestamp;
    }

    public void invalidate(){
        this.valid = false;
    }

    public boolean isValid(){
        try {
            return this.valid && this.realConnection != null && !this.realConnection.isClosed();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return this.valid;
    }

    public int getRealHashCode(){
        return this.realConnection == null ? 0 : this.hashcode;
    }
}
