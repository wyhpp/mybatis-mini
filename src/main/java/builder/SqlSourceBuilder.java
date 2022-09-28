package builder;

import config.Configuration;
import mapping.ParameterMapping;
import mapping.SqlSource;
import org.apache.ibatis.builder.ParameterExpression;
import org.dom4j.Element;
import parsing.GenericTokenPaser;
import parsing.TokenHandler;

import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author wangyuhao
 */
public class SqlSourceBuilder extends BaseBuilder{

    protected SqlSourceBuilder(Configuration configuration) {
        super(configuration);
    }

    /**
     * 替换sql中的占位符
     * @param originalSql
     * @param parameterType
     * @param additionalParams
     * @return
     */
    public SqlSource parse(String originalSql, Class<?> parameterType, Map<String,Object> additionalParams){
        ParameterMappingTokenHandler handler = new ParameterMappingTokenHandler(configuration, parameterType, additionalParams);
        GenericTokenPaser paser = new GenericTokenPaser("#{","}",handler);
        String sql = paser.parse(originalSql);
        //交给静态sqlSource处理
        return new StaticSqlSource(sql,handler.getParameterMappingList(),configuration);
    }

    private static class ParameterMappingTokenHandler extends BaseBuilder implements TokenHandler {
        private List<ParameterMapping> parameterMappingList = new ArrayList<>();
        private Class<?> parameterType;
        private Map<String,Object> additionalParams;

        protected ParameterMappingTokenHandler(Configuration configuration,Class<?> parameterType,Map<String,Object> additionalParams) {
            super(configuration);
            this.parameterType = parameterType;
            this.additionalParams = additionalParams;
        }

        public List<ParameterMapping> getParameterMappingList() {
            return parameterMappingList;
        }

        @Override
        public String handleToken(String expression) {
            ParameterMapping parameterMapping = buildParameterMapping(expression);
            parameterMappingList.add(parameterMapping);
            return "?";
        }


        /**
         * 对每一个参数构建一个parameterMapping对象
         * @param content
         * @return
         */
        // 构建参数映射
        private ParameterMapping buildParameterMapping(String content) {
            // 先解析参数映射,就是转化成一个 HashMap | #{favouriteSection,jdbcType=VARCHAR}
            Map<String, String> propertiesMap = new ParameterExpression(content);
            String property = propertiesMap.get("property");
            Class<?> propertyType;
            if (typeHandlerRegistry.hasTypeHandler(parameterType)) {
                //基本类型会存在typeHandlerRegistry内
                propertyType = parameterType;
            } else if (property != null) {
                //如果是自己定义的类，map，list
                Field declaredField = null;
                Method method = null;
                try {
                    method = parameterType.getMethod("get" + property.substring(0, 1).toUpperCase() + property.substring(1));
                    declaredField = parameterType.getDeclaredField(property);
                } catch (NoSuchMethodException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
                if (method != null) {
                    propertyType = Objects.requireNonNull(declaredField).getDeclaringClass();
                } else {
                    propertyType = Object.class;
                }
            } else {
                propertyType = Object.class;
            }
            System.out.println("构建参数映射 property：{} propertyType：{}"+ property + propertyType);
            ParameterMapping.Builder builder = new ParameterMapping.Builder(configuration, property, propertyType);
            return builder.build();
        }
    }
}
