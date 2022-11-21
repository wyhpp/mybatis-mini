package executor.resultset;

import base.MappedStatement;
import mapping.BoundSql;
import mapping.ParameterMapping;
import org.apache.ibatis.type.JdbcType;
import type.TypeHandler;
import type.TypeHandlerRegistry;
import util.ReflectUtil;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author wangyuhao
 */
public class DefaultParameterHandler implements ParameterHandler{
    private final BoundSql boundSql;
    private final TypeHandlerRegistry typeHandlerRegistry;
    private final Object parameterObject;
    private final MappedStatement mappedStatement;

    public DefaultParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        this.mappedStatement = mappedStatement;
        this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
        this.parameterObject = parameterObject;
        this.boundSql = boundSql;
    }

    @Override
    public void setParameters(PreparedStatement ps) throws SQLException {
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (null != parameterMappings) {
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                String propertyName = parameterMapping.getProperty();
                Object value = null;
                if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                    value = parameterObject;
                } else {
                    try {
                        //如果是多个参数，parameterObject被封装成map
                        value = ReflectUtil.getValue(parameterObject, propertyName);
                    } catch (InvocationTargetException | IntrospectionException | IllegalAccessException e) {
                        e.printStackTrace();
                        throw new RuntimeException("implement get method err");
                    }
                }
                JdbcType jdbcType = parameterMapping.getJdbcType();

                // 设置参数
//                logger.info("根据每个ParameterMapping中的TypeHandler设置对应的参数信息 value：{}", JSON.toJSONString(value));
                TypeHandler typeHandler = parameterMapping.getTypeHandler();
                if (typeHandler == null){
                    throw new RuntimeException("typeHandler为空,name is "+parameterMapping.getProperty());
                }
                typeHandler.setParameter(ps, i + 1, value, jdbcType);
            }
        }
    }
}
