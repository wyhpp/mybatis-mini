package config;

import base.MappedStatement;
import builder.BaseBuilder;
import com.sun.org.apache.bcel.internal.util.ClassLoader;
import org.apache.ibatis.io.Resources;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

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

    }

    /**
     * 解析mappers标签
     * 标签下有mapper,package两种子标签
     * @param mappers
     */
    private void mapperElement(Element mappers) throws ClassNotFoundException {
        List<Element> mapperList = mappers.elements("mapper");
        String resource;
        String url;
        String mapperClass;
        for (Element element : mapperList) {
            //解析处理,获取sql对应的mappedStatement
            MappedStatement statement = new MappedStatement();
            if ("package".equals(element.getName())){
                //获取package节点下name属性的值=（要扫描的包名）
                resource = element.attribute("name").getText();
                configuration.addMappers(resource);
            }else{
                //获取节点下的属性值
                resource = element.attribute("resource").getText();
                url = element.attribute("url").getText();
                mapperClass = element.attribute("class").getText();
                if (resource != null && url == null && mapperClass == null) {
                    //classPath相对路径资源，跳过
                    continue;
                } else if (resource == null && url != null && mapperClass == null) {
                    //网络资源，先跳过
                    continue;
                } else {
                    if (resource != null || url != null || mapperClass == null) {
                        throw new RuntimeException("A mapper element may only specify a url, resource or class, but not more than one.");
                    }
                    //注册mapper映射器
                    Class<?> mapperInterface = Class.forName(mapperClass);
                    this.configuration.addMapper(mapperInterface);
                }
            }
            //添加解析sql
            configuration.addMappedStatement(statement);

        }

    }
}
