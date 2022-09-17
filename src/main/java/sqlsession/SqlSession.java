package sqlsession;

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
    <T> T selectOne(String statement, Object... param);

    /**
     * 获取映射器
     * @param type
     * @param <T>
     * @return
     */
    <T> T getMapper(Class<T> type);
}
