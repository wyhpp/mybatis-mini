package dataSource.pooled;

import java.util.ArrayList;
import java.util.List;

/**
 * 线程池状态
 */
public class PoolState {
    protected PooledDataSource pooledDataSource;
    protected List<PooledConnection> idleConnections = new ArrayList<>();
    protected List<PooledConnection> activeConnections = new ArrayList<>();
    //已过期连接
    protected int claimedOverdueConnectionCount = 0;

    protected long accumulatedCheckoutTimeOfOverdueConnections = 0L;

    protected long accumulatedCheckoutTime = 0L;

    protected int hadToWaitCount = 0;

    protected long accumulatedWaitTime = 0L;

    protected int requestCount = 0;

    protected long accumulatedRequestTime = 0L;

    protected int badConnectionCount = 0;

    public PoolState(PooledDataSource pooledDataSource) {
        this.pooledDataSource = pooledDataSource;
    }
}
