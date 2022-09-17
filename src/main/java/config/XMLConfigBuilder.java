package config;

import base.MappedStatement;
import builder.BaseBuilder;
import com.sun.org.apache.bcel.internal.util.ClassLoader;
import org.apache.ibatis.io.Resources;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;

/**
 * @author wangyuhao
 * 解析xml文件
 */
public class XMLConfigBuilder extends BaseBuilder {
    private Element root;

    public XMLConfigBuilder(Reader reader) {
        super(new Configuration());
        //dom4j处理xml
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(reader);
            root = document.getRootElement();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public Configuration parse(){
        try {
            mapperElement(root.element("mappers"));
        } catch (Exception e) {
            throw new RuntimeException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
        }
        return this.configuration;
    }

    /**
     * 根据mybatis-config内的mapper标签下的类名找到对应的mapper文件，
     * 然后再解析mapper中的诸如namespace,select标签等
     * 解析mappers标签
     * 标签下有mapper,package两种子标签
     * @param mappers
     */
    private void mapperElement(Element mappers) throws Exception {
        List<Element> mapperList = mappers.elements("mapper");

        for (Element element : mapperList) {
            String resource = null;
            String url = null;
            String mapperClass = null;
            //解析处理,获取sql对应的mappedStatement
            if ("package".equals(element.getName())){
                //获取package节点下name属性的值=（要扫描的包名）
                resource = element.attribute("name").getText();
                this.configuration.addMappers(resource);
            }else{
                //获取节点下的属性值
                Attribute attribute;
                if((attribute = element.attribute("resource")) != null){
                    resource = attribute.getText();
                }
                if((attribute = element.attribute("url")) != null){
                    url = attribute.getText();
                }
                if((attribute = element.attribute("class")) != null){
                    mapperClass = attribute.getText();
                }
                if (resource != null && url == null && mapperClass == null) {
                    //classPath相对路径资源，解析xml生成mappedStatement
                    InputStream resourceAsStream = Resources.getResourceAsStream(resource);
                    SAXReader reader = new SAXReader();
                    XMLMapperBuilder mapperBuilder = new XMLMapperBuilder(this.configuration, reader.read(resourceAsStream));
                    mapperBuilder.parse();
                    for (MappedStatement mappedStatement : mapperBuilder.getMappedStatementList()) {
                        this.configuration.addMappedStatement(mappedStatement);
                    }
                } else if (resource == null && url != null && mapperClass == null) {
                    //网络资源，先跳过
                    continue;
                } else {
                    if (resource != null || url != null || mapperClass == null) {
                        throw new RuntimeException("A mapper element may only specify a url, resource or class, but not more than one.");
                    }
                    //mapperClass != null
                    //注册mapper映射器
                    Class<?> mapperInterface = Class.forName(mapperClass);
                    this.configuration.addMapper(mapperInterface);
                }
            }
        }

    }
}
