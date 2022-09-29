package type;

import org.apache.ibatis.type.JdbcType;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ObjectTypeHandler extends BaseTypeHandler<Object>{
    @Override
    protected void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i,parameter);
    }
}
