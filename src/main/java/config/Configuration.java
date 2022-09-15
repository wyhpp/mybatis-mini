package config;

import lombok.AllArgsConstructor;
import lombok.Data;
import base.MappedStatement;


import java.util.Map;
import java.util.Properties;

/**
 * @author wangyuhao
 * Mybatis配置类
 */
@Data
public class Configuration {
    /**
     * properties标签属性
     */
    protected Properties variables;
    /**
     * MappedStatement集合，存放xml中的信息
     */
    protected Map<String, MappedStatement> mappedStatements;

    public Configuration(Properties variables, Map<String, MappedStatement> mappedStatements) {
        this.variables = variables;
        this.mappedStatements = mappedStatements;
    }

    public Configuration() {

    }
}
