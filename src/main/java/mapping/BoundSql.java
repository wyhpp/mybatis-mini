package mapping;

import java.util.List;

/**
 * @author wangyuhao
 */
public class BoundSql {

    private final String sql;
    private final List<ParameterMapping> parameterMappings;
    private final Object paramObject;

//    private String resultType;

    public BoundSql(String sql, List<ParameterMapping> parameterMappings, Object paramObject) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
        this.paramObject = paramObject;
    }

    public String getSql() {
        return sql;
    }

    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }

//    public void setResultType(String resultType) {
//        this.resultType = resultType;
//    }
//
//    public String getResultType() {
//        return resultType;
//    }
}
