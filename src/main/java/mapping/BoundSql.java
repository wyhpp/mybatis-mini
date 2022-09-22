package mapping;

import org.apache.ibatis.mapping.ParameterMapping;

import java.util.List;

/**
 * @author wangyuhao
 */
public class BoundSql {

    private final String sql;
    private List<ParameterMapping> parameterMappings;
    private String resultType;
    public BoundSql(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getResultType() {
        return resultType;
    }
}
