package sqlsession;
;

/**
 * @author wangyuhao
 * sqlSession工厂
 * 不用建造者SqlSessionFactoryBuilder了
 */
public interface SqlSessionFactory {

    /**
     *
     * @return
     */
    SqlSession openSession();
}
