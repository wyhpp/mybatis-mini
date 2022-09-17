package config;

import base.MappedStatement;
import base.SqlCommondType;
import builder.BaseBuilder;
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

    private List<MappedStatement> mappedStatementList;

    /**
     * 临时增加getter,最终放入configuration里面传递
     * @return
     */
    public List<MappedStatement> getMappedStatementList() {
        return mappedStatementList;
    }

    protected XMLMapperBuilder(Configuration configuration, Document document) {
        super(configuration);
        this.document = document;
        mappedStatementList = new ArrayList<>();
    }

    /**
     * 解析mapper.xml生成mappedStatement
     */
    public void parse(){
        //获取根节点
        Element rootElement = document.getRootElement();
        //获取命名空间
        String namespace = rootElement.attribute("namespace").getData().toString();
        List<Element> elementList = new ArrayList<>();
        //获取select,update,delete,insert等标签
        elementList.addAll(rootElement.elements("select"));
        elementList.addAll(rootElement.elements("insert"));
        elementList.addAll(rootElement.elements("update"));
        elementList.addAll(rootElement.elements("delete"));

        Iterator<Element> iter = elementList.iterator();

        while(iter.hasNext()){
            Element next = iter.next();
            //解析节点内的元素
            String id = next.attribute("id").getData().toString();
            String resultType = next.attribute("resultType").getData().toString();
//            String resultMap = next.attribute("resultMap").toString();
            String parameterType = next.attribute("parameterType").toString();
            SqlCommondType sqlCommandType = SqlCommondType.valueOf(next.getName().toUpperCase(Locale.ENGLISH));
            String sql = next.getData().toString();
            MappedStatement.Builder builder = new MappedStatement.Builder(namespace + "." + id,sql,sqlCommandType);
            MappedStatement mappedStatement = builder.build();
            this.mappedStatementList.add(mappedStatement);
        }

    }
}
