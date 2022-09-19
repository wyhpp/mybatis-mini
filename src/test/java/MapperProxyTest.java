import Dao.ISchoolDao;
import Dao.IUserDao;
import binding.MapperProxyFactory;
import binding.MapperRegistry;
import org.apache.ibatis.io.Resources;
import org.junit.Test;
import sqlsession.DefaultSqlSessionFactory;
import sqlsession.SqlSession;
import sqlsession.SqlSessionFactory;
import sqlsession.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class MapperProxyTest {
    /**
     * 第二次
     */
    @Test
    public void mapperProxyTest() {
        MapperProxyFactory<IUserDao> factory = new MapperProxyFactory<>(IUserDao.class);
        Map<String, String> sqlSession = new HashMap<>();

//        sqlSession.put("Dao.IUserDao.queryUserName", "模拟执行 Mapper.xml 中 SQL 语句的操作：查询用户姓名");
//        sqlSession.put("Dao.IUserDao.queryUserAge", "模拟执行 Mapper.xml 中 SQL 语句的操作：查询用户年龄");
//        IUserDao userDao = factory.newInstance(sqlSession);
//
//        String res = userDao.queryUserName("10001");
//        System.out.println("测试结果" + res);
    }

    /**
     * 第三次
     */
    @Test
    public void mapperTest(){
//        // 1. 注册 Mapper
//        MapperRegistry registry = new MapperRegistry();
//        registry.addMappers("Dao");
//
//        // 2. 从 SqlSession 工厂获取 Session
//        SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(registry);
//        SqlSession sqlSession = sqlSessionFactory.openSession();
//
//        // 3. 获取映射器对象
//        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
//
//        // 4. 测试验证
//        String res = userDao.queryUserName("10001");
//        System.out.println("测试结果" + res);
    }

    /**
     * 第4次
     * @throws IOException
     */
    @Test
    public void test_SqlSessionFactory() throws IOException {
        Logger logger = Logger.getLogger("testLog");
        // 1. 从SqlSessionFactory中获取SqlSession
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        // 2. 获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        // 3. 测试验证
        String res = userDao.queryUserInfoById("10001");
        logger.info("测试结果:" + res);

    }
}
