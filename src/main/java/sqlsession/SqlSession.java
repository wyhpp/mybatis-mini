package sqlsession;

import config.Configuration;

import java.util.List;

/**
 * @author wangyuhao
 */
public interface SqlSession {
    /**
     * 根据传入的sqlId获取一个封装对象
     * @param statement
     * @param <T>
     * @return
     */
    <T> T selectOne(String statement);

    /**
     * 根据传入的sqlId获取一个封装对象,允许传入参数
     * @param statement
     * @param param
     * @param <T>
     * @return
     */
    <T> T selectOne(String statement, Object param);

    <E> List<E> selectList(String statement, Object parameter);

    int insert(String statement, Object parameter);

    int update(String statement, Object parameter);

    Object delete(String statement, Object parameter);

    /**
     * 获取映射器
     * @param type
     * @param <T>
     * @return
     */
    <T> T getMapper(Class<T> type);

    Configuration getConfiguration();
}
