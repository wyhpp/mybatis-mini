package parsing;

/**
 * 处理sql占位符接口
 */
public interface TokenHandler {
    String handleToken(String expression);
}
