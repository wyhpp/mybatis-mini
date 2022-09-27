package config;

import base.MappedStatement;
import base.SqlCommandType;
import builder.BaseBuilder;
import builder.SqlSourceBuilder;
import builder.XMLScriptBuilder;
import mapping.SqlSource;
import org.dom4j.Element;

import java.util.Locale;

public class XMLStatementBuilder extends BaseBuilder {
    protected String currentNamespace;
    
    protected Element element;
    protected XMLStatementBuilder(Configuration configuration,Element element,String currentNameSpace) {
        super(configuration);
        this.element = element;
        this.currentNamespace = currentNameSpace;
    }

    public void parseStatementNode() {
        String id = element.attributeValue("id");
        // 参数类型
        String parameterType = element.attributeValue("parameterType");
        Class<?> parameterTypeClass = typeAliasRegistry.resolveAlias(parameterType);
        // 结果类型
        String resultType = element.attributeValue("resultType");
        Class<?> resultTypeClass = typeAliasRegistry.resolveAlias(resultType);
        if(resultTypeClass == null){
            try {
                resultTypeClass = Class.forName(resultType);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        // 获取命令类型(select|insert|update|delete)
        String nodeName = element.getName();
        SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));

        // 获取默认语言驱动器
//        Class<?> langClass = configuration.getLanguageRegistry().getDefaultDriverClass();
//        LanguageDriver langDriver = configuration.getLanguageRegistry().getDriver(langClass);

//        SqlSource sqlSource = SqlSourceBuilder.createSqlSource(configuration, element, parameterTypeClass);
        XMLScriptBuilder xmlScriptBuilder = new XMLScriptBuilder(configuration, parameterTypeClass, element);
        SqlSource sqlSource = xmlScriptBuilder.parseScriptNode();

        MappedStatement mappedStatement = new MappedStatement.Builder(configuration, currentNamespace + "." + id, sqlSource,sqlCommandType, resultTypeClass).build();

        // 添加解析 SQL
        configuration.addMappedStatement(mappedStatement);
    }
}
