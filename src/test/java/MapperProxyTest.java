import Dao.ISchoolDao;
import Dao.IUserDao;
import binding.MapperProxyFactory;
import binding.MapperRegistry;
import org.junit.Test;
import sqlsession.DefaultSqlSessionFactory;
import sqlsession.SqlSession;
import sqlsession.SqlSessionFactory;

import java.util.HashMap;
import java.util.Map;

public class MapperProxyTest {
    @Test
    public void mapperProxyTest() {
        MapperProxyFactory<IUserDao> factory = new MapperProxyFactory<>(IUserDao.class);
        Map<String, String> sqlSession = new HashMap<>();

//        sqlSession.put("Dao.IUserDao.queryUserName", "模拟执行 Mapper.xml 中 SQL 语句的操作：查询用户姓名");
//        sqlSession.put("Dao.IUserDao.queryUserAge", "模拟执行 Mapper.xml 中 SQL 语句的操作：查询用户年龄");
//        IUserDao userDao = factory.newInstance(sqlSession);

//        String res = userDao.queryUserName("10001");
//        System.out.println("测试结果" + res);
    }

    @Test
    public void mapperTest(){
        // 1. 注册 Mapper
        MapperRegistry registry = new MapperRegistry();
        registry.addMappers("Dao");

        // 2. 从 SqlSession 工厂获取 Session
        SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(registry);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        // 3. 获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        // 4. 测试验证
        String res = userDao.queryUserName("10001");
        System.out.println("测试结果" + res);
    }
}
