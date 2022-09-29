package type;

import org.apache.ibatis.type.JdbcType;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 处理位置类型的入参
 * 当变量名字与原类中变量名不同时，无法识别该变量类型
 */
public class UnknownTypeHandler extends BaseTypeHandler<Object> {
    private final TypeHandlerRegistry typeHandlerRegistry;

    private final ObjectTypeHandler objectTypeHandler = new ObjectTypeHandler();

    public UnknownTypeHandler(TypeHandlerRegistry typeHandlerRegistry) {
        this.typeHandlerRegistry = typeHandlerRegistry;
    }

    @Override
    protected void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        TypeHandler handler = resolveTypeHandler(parameter, jdbcType);
        handler.setParameter(ps,i,parameter,jdbcType);
    }

    private TypeHandler<?> resolveTypeHandler(Object parameter, JdbcType jdbcType){
        if(parameter == null){
            return this.objectTypeHandler;
        }
        TypeHandler<?> typeHandler = this.typeHandlerRegistry.getTypeHandler(parameter.getClass(), jdbcType);
        if(typeHandler == null){
            return this.objectTypeHandler;
        }
        return typeHandler;
    }
}
