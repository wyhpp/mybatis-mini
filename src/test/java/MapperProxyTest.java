import Dao.IUserDao;
import binding.MapperProxyFactory;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MapperProxyTest {
    @Test
    public void mapperProxyTest() {
        MapperProxyFactory<IUserDao> factory = new MapperProxyFactory<>(IUserDao.class);
        Map<String, String> sqlSession = new HashMap<>();

        sqlSession.put("Dao.IUserDao.queryUserName", "模拟执行 Mapper.xml 中 SQL 语句的操作：查询用户姓名");
        sqlSession.put("Dao.IUserDao.queryUserAge", "模拟执行 Mapper.xml 中 SQL 语句的操作：查询用户年龄");
        IUserDao userDao = factory.newInstance(sqlSession);

        String res = userDao.queryUserName("10001");
        System.out.println("测试结果" + res);
    }
}
