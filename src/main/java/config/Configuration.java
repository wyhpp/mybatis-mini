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
@AllArgsConstructor
public class Configuration {
    /**
     * properties标签属性
     */
    protected Properties variables;
    /**
     * MappedStatement集合，存放xml中的信息
     */
    protected final Map<String, MappedStatement> mappedStatements;

}
