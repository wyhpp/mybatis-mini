# Mybatis-mini
# 个人实现Mybatis框架

## 1. Mybatis执行和类流程图

![image](https://gitee.com/wyhpp/mybatis-mini/blob/master/pic/fe2afe9d6b894ce682826743fecdb51b.png)


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
/**
* MappedStatement类
**/

public class MappedStatement {

    private String id;

    private Class<?> resultType;

    private String parameterType;

    private String resultMap;

    private SqlCommandType commandType;

    private SqlSource sqlSource;

    private Configuration configuration;
}
```


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


- 构建ParameterMapping时使用反射调用参数属性的get方法。
- 如果该参数是注册在`TypeHandler`中的基本数据类形，则直接有对应的数据类型
- 如果是自定义的类中的属性,list,map等，通过反射获取对应属性的类型构建

## 3.解析完成，openSeesion

1. 工厂方法开启session，新建Excutor执行器
2. Excutor实际执行sql语句
3. 使用statementHandler对语句进行处理

### 1.PrepareStatementHandler

1. 准备语句 prepare
	1. 实例化statement
	2. 设置连接超时时间，返回大小等查询参数
3. 参数化 parameterize ,交给parameterHandler
4. 执行查询 query | update

### 2.ParameterHandler

- `setParamters`设置参数
- 获取sql中对应的parameterMappingList，对于非基本类型，反射获取值
- 根据参数类型javaType获取对应TypeHandler设置参数值到preparedStatement

```java
@Override
    public void setParameters(PreparedStatement ps) throws SQLException {
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (null != parameterMappings) {
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                String propertyName = parameterMapping.getProperty();
                Object value = null;
                if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                    value = parameterObject;
                } else {
                    try {
                        //如果是多个参数，parameterObject被封装成map
                        value = ReflectUtil.getValue(parameterObject, propertyName);
                    } catch (InvocationTargetException | IntrospectionException | IllegalAccessException e) {
                        e.printStackTrace();
                        throw new RuntimeException("implement get method err");
                    }
                }
                JdbcType jdbcType = parameterMapping.getJdbcType();

                // 设置参数
//                logger.info("根据每个ParameterMapping中的TypeHandler设置对应的参数信息 value：{}", JSON.toJSONString(value));
                TypeHandler typeHandler = parameterMapping.getTypeHandler();
                if (typeHandler == null){
                    throw new RuntimeException("typeHandler为空,name is "+parameterMapping.getProperty());
                }
                typeHandler.setParameter(ps, i + 1, value, jdbcType);
            }
        }
    }
```


## 4.获取映射器，getMapper

```java
IUserDao userDao = sqlSession.getMapper(IUserDao.class);
```

- `getMapper`获取`MapperRegistry`中接口对应的代理对象工厂生成的代理对象

```java
/**
* 代理工厂类
**/
public class MapperProxyFactory<T> {
    private Class<T> mapperInterface;

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public T newInstance(SqlSession sqlSession){
        //生成代理类对象
        MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession,mapperInterface);
        //生成Mapper接口的代理对象
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface},mapperProxy);
    }
}
```

```java
public class MapperProxy<T> implements InvocationHandler{
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(Object.class.equals(method.getDeclaringClass())){
            return method.invoke(this,args);
        }else{
//            return sqlSession.selectOne(method.getDeclaringClass().getName() + "." + method.getName(),args);
            MapperMethod mapperMethod = cachedMapperMethod(method);
            return mapperMethod.execute(this.sqlSession,args);
        }
    }
}
```


- **mybatis核心，java动态代理**
- 调用代理类的`newProsyInstance`方法
- 接口代理类`MapperProxy`继承`InvocationHandler`，重写其`invoke`方法，在代理对象调用方法时调用。
- 最终调用`sqlSession`中的对应方法
- 调用`excutor`中的方法，`excutor`对整个sql执行流程进行封装


## 5.执行代理接口的对应方法

```java
IUserDao userDao = sqlSession.getMapper(IUserDao.class);
User user1 = new User();
user1.setUserId("10001");
user1.setId(1L);
User user = userDao.queryUserInfo(user1);

```


## 6.后记

Mybatis框架的整体执行流程还是挺复杂的，尤其是参数的设置和封装。

几乎所有的类之间都是依靠`configuration`来传递配置信息。

从配置文件解析开始，到执行完成sql返回结果，Mybatis做了一大堆的封装。

目前实现了框架的基本功能，整个结构基本都是按照Mybatis源码的结构来设置的。参数注解设置也完成了一部分，能按照注解解析参数。还没实现resultMap的映射，动态sql解析，缓存等。





