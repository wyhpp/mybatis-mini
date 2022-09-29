package mapping;


import config.Configuration;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;
import type.TypeHandler;
import type.TypeHandlerRegistry;

import java.sql.JDBCType;

/**
 * @author wangyuhao
 */
@Data
public class ParameterMapping {

    private Configuration configuration;
    //待替换的变量
    private String property;
//    private ParameterMode mode;
    //参数类型
    private Class<?> javaType;
    //jdbc类型
    private JdbcType jdbcType;

    private TypeHandler<?> typeHandler;

    public static class Builder {
        private ParameterMapping parameterMapping = new ParameterMapping();

        public Builder(Configuration configuration, String property, Class<?> javaType) {
            this.parameterMapping.configuration = configuration;
            this.parameterMapping.property = property;
            this.parameterMapping.javaType = javaType;
//            this.parameterMapping.mode = ParameterMode.IN;
        }

        public ParameterMapping build(){
            resolveTypeHandler();
            return this.parameterMapping;
        }

        public ParameterMapping.Builder typeHandler(TypeHandler<?> typeHandler){
            this.parameterMapping.typeHandler = typeHandler;
            return this;
        }

        public ParameterMapping.Builder jdbcType(JdbcType jdbcType){
            this.parameterMapping.jdbcType = jdbcType;
            return this;
        }

        /**
         * 根据参数类型和jdbcType获取typeHandler
         */
        private void resolveTypeHandler(){
            if(this.parameterMapping.typeHandler == null && this.parameterMapping.javaType != null){
                TypeHandlerRegistry typeHandlerRegistry = this.parameterMapping.configuration.getTypeHandlerRegistry();
                this.parameterMapping.typeHandler = typeHandlerRegistry.
                        getTypeHandler(this.parameterMapping.javaType, this.parameterMapping.jdbcType);
            }
        }
    }
}
