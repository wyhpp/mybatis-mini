package mapping;


import config.Configuration;

public class ParameterMapping {

    private Configuration configuration;
    //待替换的变量
    private String property;
//    private ParameterMode mode;
    //参数类型
    private Class<?> javaType;

    public static class Builder {
        private ParameterMapping parameterMapping = new ParameterMapping();

        public Builder(Configuration configuration, String property, Class<?> javaType) {
            this.parameterMapping.configuration = configuration;
            this.parameterMapping.property = property;
            this.parameterMapping.javaType = javaType;
//            this.parameterMapping.mode = ParameterMode.IN;
        }

        public ParameterMapping build(){
            return this.parameterMapping;
        }
    }
}
