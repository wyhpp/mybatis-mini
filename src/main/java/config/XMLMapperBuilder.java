package config;

import base.MappedStatement;
import base.SqlCommandType;
import builder.BaseBuilder;
import org.apache.ibatis.io.Resources;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * @author wangyuhao
 * 解析mapper.xml文件的类，仿照XMLConfigBuilder实现
 */
public class XMLMapperBuilder extends BaseBuilder {

    //存放mapper.xml InputStream读取出来的document文件
    private final Document document;

//    private List<MappedStatement> mappedStatementList;

    private String resource;

    private String currentNamespace;

    /**
     * 临时增加getter,最终放入configuration里面传递
     * @return
     */
//    public List<MappedStatement> getMappedStatementList() {
//        return mappedStatementList;
//    }

    protected XMLMapperBuilder(Configuration configuration, Document document,String resource) {
        super(configuration);
        this.document = document;
        this.resource = resource;
//        mappedStatementList = new ArrayList<>();
    }

    /**
     * 解析mapper.xml生成mappedStatement
     */
    public void parse(){
        //获取根节点
        Element rootElement = document.getRootElement();
        //未加载过这个mapper
        if(!configuration.isResourceLoaded(resource)){
            configurationElement(rootElement);
            //标记加载
            configuration.addResources(resource);
            // 绑定映射器到namespace   namespace=mapper接口的类路径
            try {
                configuration.addMapper(Class.forName(currentNamespace));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

//        //获取命名空间
//        String namespace = rootElement.attribute("namespace").getData().toString();
//        List<Element> elementList = new ArrayList<>();
//        //获取select,update,delete,insert等标签
//        elementList.addAll(rootElement.elements("select"));
//        elementList.addAll(rootElement.elements("insert"));
//        elementList.addAll(rootElement.elements("update"));
//        elementList.addAll(rootElement.elements("delete"));
//
//        Iterator<Element> iter = elementList.iterator();
//
//        while(iter.hasNext()){
//            Element next = iter.next();
//            //解析节点内的元素
//            String id = next.attribute("id").getData().toString();
//            String resultType = next.attribute("resultType").getData().toString();
////            String resultMap = next.attribute("resultMap").toString();
//            String parameterType = next.attribute("parameterType").toString();
//            SqlCommandType sqlCommandType = SqlCommandType.valueOf(next.getName().toUpperCase(Locale.ENGLISH));
//            String sql = next.getData().toString();
//            MappedStatement.Builder builder = new MappedStatement.Builder(configuration,namespace + "." + id,sql,sqlCommandType)
//                    .setResultType(resultType);
//            MappedStatement mappedStatement = builder.build();
//            this.mappedStatementList.add(mappedStatement);
//        }

    }

    private void configurationElement(Element element){
        //获取命名空间
        String namespace = element.attributeValue("namespace");
        currentNamespace = namespace;
        if ("".equals(namespace)) {
            throw new RuntimeException("Mapper's namespace cannot be empty");
        }
        buildStatementFromContext(element.elements("select"),
                element.elements("update"),
                element.elements("insert"),
                element.elements("delete")
        );
    }

    /**
     * 解析select|insert|update|delete标签
     * @param lists
     */
    @SafeVarargs
    private final void buildStatementFromContext(List<Element>... lists) {
        for (List<Element> list : lists) {
            for (Element element : list) {
                final XMLStatementBuilder statementParser = new XMLStatementBuilder(configuration, element,currentNamespace);
                statementParser.parseStatementNode();
            }
        }
    }
}
