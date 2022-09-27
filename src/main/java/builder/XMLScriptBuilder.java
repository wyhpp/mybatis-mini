package builder;

import config.Configuration;
import mapping.SqlSource;

import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析脚本解析
 */
public class XMLScriptBuilder extends BaseBuilder {
    private final Class<?> parameterType;
    private final Element element;

    public XMLScriptBuilder(Configuration configuration, Class<?> parameterType, Element element) {
        super(configuration);
        this.parameterType = parameterType;
        this.element = element;
    }

    public SqlSource parseScriptNode() {
        List<String> contents = parseDynamicTags(element);
        return new RawSqlSource(configuration,contents.get(0), parameterType);
    }

    List<String> parseDynamicTags(Element element) {
        List<String> contents = new ArrayList<>();
        // element.getText 拿到 SQL
        String data = element.getText();
        contents.add(data);
        return contents;
    }
}
