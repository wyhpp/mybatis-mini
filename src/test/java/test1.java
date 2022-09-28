import Dao.IUserDao;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;
import pojo.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.logging.Logger;

public class test1 {

    public static void main(String[] args) throws IOException {
        String resource = "org/mybatis/example/mybatis-config.xml.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    }

    @Test
    public void testmybatis() throws IOException {
        Logger logger = Logger.getLogger("testLog");
        // 1. 获取映射器对象
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        // 2. 测试验证：对象参数
        User user1 = new User();
        user1.setUserId("10001");
        user1.setId(1L);
        User user = userDao.queryUserInfoById(1L,"10001");
        logger.info("测试结果：{}" + user.toString());
    }
}
