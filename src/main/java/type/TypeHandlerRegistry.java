package type;

import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangyuhao
 */
public class TypeHandlerRegistry {
    private final Map<JdbcType, TypeHandler<?>> JDBC_TYPE_HANDLER_MAP = new EnumMap<>(JdbcType.class);
    private final Map<Type, Map<JdbcType, TypeHandler<?>>> typeHandlerMap = new HashMap<>();
    private final Map<Class<?>, TypeHandler<?>> allTypeHandlersMap = new HashMap<>();

    public TypeHandlerRegistry() {
        register(Long.class, new LongTypeHandler());
        register(String.class, new StringTypeHandler());

        register(String.class, JdbcType.CHAR, new StringTypeHandler());
        register(String.class, JdbcType.VARCHAR, new StringTypeHandler());
    }

    private void register(Type javaType, JdbcType jdbcType, TypeHandler<?> handler) {
        if (javaType != null) {
            Map<JdbcType, TypeHandler<?>> map = this.typeHandlerMap.get(javaType);
            if (map == null || map.isEmpty()) {
                map = new HashMap();
            }

            map.put(jdbcType, handler);
            this.typeHandlerMap.put(javaType, map);
        }

        this.allTypeHandlersMap.put(handler.getClass(), handler);
    }

    public <T> void register(Class<T> javaType, TypeHandler<? extends T> typeHandler) {
        this.register(javaType, null, typeHandler);
    }

    public boolean hasTypeHandler(Type javaType) {
        return !(this.typeHandlerMap.get(javaType) == null || this.typeHandlerMap.get(javaType).isEmpty());
    }
}
