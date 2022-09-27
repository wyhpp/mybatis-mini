package builder;

import config.Configuration;
import mapping.BoundSql;
import mapping.ParameterMapping;
import mapping.SqlSource;

import java.util.List;

public class StaticSqlSource implements SqlSource {
    private final String sql;
    private final List<ParameterMapping> parameterMappings;
    private final Configuration configuration;

    public StaticSqlSource(String sql, List<ParameterMapping> parameterMappings, Configuration configuration) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
        this.configuration = configuration;
    }

    @Override
    public BoundSql getBoundSql(Object paramObject) {
        return new BoundSql(sql,parameterMappings,paramObject);
    }
}
