package config;

import base.MappedStatement;
import builder.BaseBuilder;
import dataSource.DataSourceFactory;
import mapping.Environment;
import org.apache.ibatis.io.Resources;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import transaction.TransactionFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Properties;

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
            environmentElement(root.element("environments"));
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
                    XMLMapperBuilder mapperBuilder = new XMLMapperBuilder(this.configuration, reader.read(resourceAsStream),resource);
                    mapperBuilder.parse();
//                    for (MappedStatement mappedStatement : mapperBuilder.getMappedStatementList()) {
//                        this.configuration.addMappedStatement(mappedStatement);
//                    }
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

    /**
     * 解析environments节点
     * 标签下有mapper,package两种子标签
     * @param environments
     * <environments default="development">
     *         <environment id="development">
     *             <transactionManager type="JDBC" />
     *             <dataSource type="POOLED">
     *                 <property name="driver" value="${jdbc.driverClassName}" />
     *                 <property name="url" value="${jdbc.url}" />
     *                 <property name="username" value="${jdbc.username}" />
     *                 <property name="password" value="${jdbc.password}" />
     *              </dataSource>
     *         </environment>
     * </environments>
     */
    private void environmentElement(Element environments) throws Exception {
        String environment = environments.attributeValue("default");
        List<Element> environmentList = environments.elements("environment");

        for (Element env : environmentList) {
            String id = env.attributeValue("id");
            if (environment.equals(id)) {
                // 事务管理器
                TransactionFactory txFactory = (TransactionFactory) typeAliasRegistry.resolveAlias(env.element("transactionManager").attributeValue("type")).newInstance();
                // 数据源
                Element dataSourceElement = env.element("dataSource");
                DataSourceFactory dataSourceFactory = (DataSourceFactory) typeAliasRegistry.resolveAlias(dataSourceElement.attributeValue("type")).newInstance();
                List<Element> propertyList = dataSourceElement.elements("property");
                Properties props = new Properties();
                for (Element property : propertyList) {
                    props.setProperty(property.attributeValue("name"), property.attributeValue("value"));
                }
                dataSourceFactory.setProperties(props);
                DataSource dataSource = dataSourceFactory.getDataSource();
                // 构建环境
                Environment.Builder environmentBuilder = new Environment.Builder(id)
                        .transactionFactory(txFactory)
                        .dataSource(dataSource);
                configuration.setEnvironment(environmentBuilder.build());
            }
        }

    }
}
