package builder;

import config.Configuration;
import mapping.ParameterMapping;
import mapping.SqlSource;
import org.apache.ibatis.builder.ParameterExpression;
import org.dom4j.Element;
import parsing.GenericTokenPaser;
import parsing.TokenHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            Class<?> propertyType = parameterType;
            ParameterMapping.Builder builder = new ParameterMapping.Builder(configuration, property, propertyType);
            return builder.build();
        }
    }
}
