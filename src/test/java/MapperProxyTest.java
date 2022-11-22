import Dao.ISchoolDao;
import Dao.IUserDao;
import binding.MapperProxyFactory;
import binding.MapperRegistry;
import org.apache.ibatis.io.Resources;
import org.junit.Test;
import pojo.User;
import sqlsession.DefaultSqlSessionFactory;
import sqlsession.SqlSession;
import sqlsession.SqlSessionFactory;
import sqlsession.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
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
    public void mapperTest() {
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
     * 第4，5次
     *
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
//        User user = userDao.queryUserInfoById(1L);
//        logger.info("测试结果:" + user.toString());

    }

    /**
     * 第6,7次
     *
     * @throws IOException
     */
    @Test
    public void test_Datasource() throws IOException {
        Logger logger = Logger.getLogger("testLog");
        // 1. 从SqlSessionFactory中获取SqlSession
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        // 2. 获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        // 3. 测试验证
        for (int i = 0; i < 50; i++) {
            User user1 = new User();
            user1.setUserId("10001");
            user1.setId(1L);
            User user = userDao.queryUserInfo(user1);
            logger.info("测试结果:" + user.toString());
        }
//            User user2 = userDao.queryUserInfoById(1L,"10001");
//        logger.info("测试结果:" + user.toString());
//        user1.setUserId("20002");
//        user1.setUserName("mm");
//        user1.setUserHead("head");
//        userDao.insertUserInfo(user1);
//        User user2 = userDao.queryUserInfo(user1);
//        logger.info("测试结果:" + user2.toString());
//        user1.setUserName("gg");
//        userDao.updateUserInfo(user1);
//        User user3 = userDao.queryUserInfo(user1);
//        logger.info("测试结果:" + user3.toString());
//        }

//        List<User> users = userDao.queryUserInfoList();
//        for (User user2 : users) {
//            System.out.println(user2);
//        }
    }
}
