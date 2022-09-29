package binding;

import base.MappedStatement;
import base.SqlCommandType;
import config.Configuration;
import lombok.Data;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.BindingException;
import sqlsession.SqlSession;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class MapperMethod {
    private final MapperMethod.SqlCommand sqlCommand;
    private final MapperMethod.MethodSignature method;
    public MapperMethod(Class<?> mapperInterface, Method method, Configuration configuration) {
        this.sqlCommand = new MapperMethod.SqlCommand(mapperInterface,method,configuration);
        this.method = new MethodSignature(configuration,method);
    }

    public Object execute(SqlSession sqlSession,Object[] args){
        Object result;
        Object param;
        switch (this.sqlCommand.getType()){
            case INSERT:
                param = this.method.convertArgsToSqlCommandParam(args);
                result = sqlSession.insert(this.sqlCommand.getName(),param);
                break;
            case UPDATE:
                param = this.method.convertArgsToSqlCommandParam(args);
                result = sqlSession.update(this.sqlCommand.getName(),param);
                break;
            case DELETE:
                param = this.method.convertArgsToSqlCommandParam(args);
                result = sqlSession.delete(this.sqlCommand.getName(),param);
                break;
            case SELECT:
//                if (this.method.returnsVoid() && this.method.hasResultHandler()) {
//                    this.executeWithResultHandler(sqlSession, args);
//                    result = null;
//                } else if (this.method.returnsMany()) {
//                    result = this.executeForMany(sqlSession, args);
//                } else if (this.method.returnsMap()) {
//                    result = this.executeForMap(sqlSession, args);
//                } else if (this.method.returnsCursor()) {
//                    result = this.executeForCursor(sqlSession, args);
//                } else {
                    param = this.method.convertArgsToSqlCommandParam(args);
                    result = sqlSession.selectOne(this.sqlCommand.getName(), param);
//                    if (this.method.returnsOptional() && (result == null || !this.method.getReturnType().equals(result.getClass()))) {
//                        result = Optional.ofNullable(result);
//                    }
//                }
                break;
            default:
                throw new BindingException("Unknown execution method for: " + this.sqlCommand.getName());
        }
        return result;
    }

    /**
     * 方法签名，对方法信息和返回类型封装
     */
    public static class MethodSignature {
        //存放--变量位置：变量名
        private SortedMap<Integer,String> names;

        public MethodSignature(Configuration configuration,Method method) {
            //获取方法参数位置和参数名
            Class<?>[] paramTypes = method.getParameterTypes();
            Annotation[][] paramAnnotations = method.getParameterAnnotations();
            SortedMap<Integer, String> map = new TreeMap();

            int paramCount = paramAnnotations.length;
            //获取@param注解的数量作为入参数量
            for(int paramIndex = 0; paramIndex < paramCount; ++paramIndex) {
                String name = null;
                Annotation[] annotations = paramAnnotations[paramIndex];

                for(int i = 0; i < annotations.length; ++i) {
                    Annotation annotation = annotations[i];
                    //如果是@param，获取value值
                    if (annotation instanceof Param) {
                        name = ((Param)annotation).value();
                        break;
                    }
                }
                //没有注解时，放入参数名为 arg0,arg1……
                if (name == null){
                    List<String> parameterNames = Arrays.stream(method.getParameters()).map(Parameter::getName).collect(Collectors.toList());
                    name = parameterNames.get(paramIndex);
                }
                map.put(paramIndex, name);
            }

            names = map;
        }

        public Object convertArgsToSqlCommandParam(Object[] args) {
            final int paramCount = names.size();
            if (args == null || paramCount == 0) {
                // 如果没参数
                return null;
            } else if (paramCount == 1) {
                //一个参数时直接返回参数
                return args[names.firstKey()];
            } else {
                // 否则，返回一个ParamMap，修改参数名，参数名就是其位置
                //param  key:变量名  value:参数值
                final Map<String, Object> param = new HashMap<>();
                for (Map.Entry<Integer, String> entry : names.entrySet()) {
                    // 1.先加一个#{0},#{1},#{2}...参数
                    param.put(entry.getValue(), args[entry.getKey()]);
                }
                return param;
            }
        }

    }


    @Data
    public static class SqlCommand{
        private final String name;
        private final SqlCommandType type;

        public SqlCommand(Class<?> mapperInterface, Method method, Configuration configuration) {
            String methodName = method.getName();
            Class<?> declaringClass = method.getDeclaringClass();
            MappedStatement ms = configuration.getMappedStatement(mapperInterface.getName() + "." + methodName);
            if(ms == null){
                this.name = null;
                this.type = null;
            }else{
                this.name = ms.getId();
                this.type = ms.getCommandType();
                if(this.type == SqlCommandType.UNKOWN){
                    throw new RuntimeException("Unknown execution method for :" + this.name);
                }
            }
        }
    }
}
