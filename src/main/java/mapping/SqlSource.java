package mapping;

public interface SqlSource {
    public BoundSql getBoundSql(Object paramObject);
}
