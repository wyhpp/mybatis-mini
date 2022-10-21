# Mybatis-mini
# 个人实现Mybatis框架

## 1. Mybatis执行和类流程图
![4d766e2661580a26d9b1acf9e48c249f.png](../_resources/4d766e2661580a26d9b1acf9e48c249f.png)




## 2. 解析配置XML/SqlSessionBuilder

```java

Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
```

`SqlSessionFactory`采用建造者模式，`build`方法传入xml配置文件并且完成解析，将标签对应属性放入`configuration`属性中。整个流程通过`configuration`串联。


## 3. XMLConfigBuilder
### 1. environmentElement

```java
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
```

1. 类型注册机根据配置信息创建事务工厂
2. 数据源工厂根据配置获取数据源，并且设置数据库连接信息
3. 构建环境`Environment`，设置事务工厂和dataSource
4. `configuration`设置`Environment`

### mapperElement

```java
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
```

1. 解析mappers标签下配置的mapper.xml文件内容
2. 根据命名空间namespace去重，防止重复解析
3. 将update|select|delete|insert  节点下属性 parameterType，resultType等解析包装成mappedStatement对象（含有configuration，id，参数类型，返回结果类型，原始sql等）
4. 原始sql，占位符替换：将sql封装到RawSql中，并解析`#{`和`}`，替换占位符为“?”，将传入参数放入参数列表中，等待之后设置到preparedStatement

```java
// 构建参数映射
//对每一个参数构建一个parameterMapping对象
        private ParameterMapping buildParameterMapping(String content) {
            // 先解析参数映射,就是转化成一个 HashMap | #{favouriteSection,jdbcType=VARCHAR}
            Map<String, String> propertiesMap = new ParameterExpression(content);
            String property = propertiesMap.get("property");
            Class<?> propertyType;
            if (typeHandlerRegistry.hasTypeHandler(parameterType)) {
                //基本类型会存在typeHandlerRegistry内
                propertyType = parameterType;
            } else if (property != null) {
                //如果是自己定义的类，map，list
                Field declaredField = null;
                Method method = null;
                try {
                    method = parameterType.getMethod("get" + property.substring(0, 1).toUpperCase() + property.substring(1));
                    declaredField = parameterType.getDeclaredField(property);
                } catch (NoSuchMethodException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
                if (method != null) {
                    propertyType = (Class<?>) Objects.requireNonNull(declaredField).getGenericType();
                } else {
                    propertyType = Object.class;
                }
            } else {
                propertyType = Object.class;
            }
            String jdbcType = propertiesMap.get("jdbcType");
            JdbcType type = null;
            if(jdbcType != null){
                type = JdbcType.valueOf(jdbcType);
            }
            System.out.println("构建参数映射 property：{} propertyType：{}"+ property + propertyType);
            ParameterMapping.Builder builder = new ParameterMapping.Builder(configuration, property, propertyType)
                    .jdbcType(type);
            return builder.build();
        }
    }
```
