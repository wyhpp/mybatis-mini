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
                if(this.method.returnsMany){
                    param = this.method.convertArgsToSqlCommandParam(args);
                    result = sqlSession.selectList(this.sqlCommand.getName(),param);
                }else{
                    param = this.method.convertArgsToSqlCommandParam(args);
                    result = sqlSession.selectOne(this.sqlCommand.getName(), param);
                }
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
     * ???????????????????????????????????????????????????
     */
    public static class MethodSignature {
        //??????--????????????????????????
        private SortedMap<Integer,String> names;
        //????????????????????????
        private boolean returnsMany;
        //???????????????
        private Class<?> returnType;

        public MethodSignature(Configuration configuration,Method method) {
            this.returnType = method.getReturnType();
            //?????????????????????????????????
            this.returnsMany = Collection.class.isAssignableFrom(this.returnType) || this.returnType.isArray();
            //????????????????????????????????????
            Class<?>[] paramTypes = method.getParameterTypes();
            Annotation[][] paramAnnotations = method.getParameterAnnotations();
            SortedMap<Integer, String> map = new TreeMap();

            int paramCount = paramAnnotations.length;
            //??????@param?????????????????????????????????
            for(int paramIndex = 0; paramIndex < paramCount; ++paramIndex) {
                String name = null;
                Annotation[] annotations = paramAnnotations[paramIndex];

                for(int i = 0; i < annotations.length; ++i) {
                    Annotation annotation = annotations[i];
                    //?????????@param?????????value???
                    if (annotation instanceof Param) {
                        name = ((Param)annotation).value();
                        break;
                    }
                }
                //???????????????????????????????????? arg0,arg1??????
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
                // ???????????????
                return null;
            } else if (paramCount == 1) {
                //?????????????????????????????????
                return args[names.firstKey()];
            } else {
                // ?????????????????????ParamMap?????????????????????????????????????????????
                //param  key:?????????  value:?????????
                final Map<String, Object> param = new HashMap<>();
                for (Map.Entry<Integer, String> entry : names.entrySet()) {
                    // 1.????????????#{0},#{1},#{2}...??????
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
