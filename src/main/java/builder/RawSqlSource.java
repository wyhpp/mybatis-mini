package builder;

import config.Configuration;
import mapping.BoundSql;
import mapping.SqlSource;

import java.util.HashMap;

/**
 * 构造StaticSqlSource
 * @author wangyuhao
 */
public class RawSqlSource implements SqlSource {
    private SqlSource sqlSource;

    public RawSqlSource(Configuration configuration,String sql,Class<?> parameterType) {
        SqlSourceBuilder builder = new SqlSourceBuilder(configuration);
        parameterType = parameterType==null? Object.class:parameterType;
        this.sqlSource = builder.parse(sql,parameterType,new HashMap<>());
    }

    public SqlSource getSqlSource() {
        return sqlSource;
    }

    @Override
    public BoundSql getBoundSql(Object paramObject) {
        return null;
    }
}
