package base;

import builder.RawSqlSource;
import config.Configuration;
import lombok.Data;
import mapping.BoundSql;
import mapping.SqlSource;

/**
 * @author wangyuhao
 */
@Data
public class MappedStatement {

    private String id;

    private Class<?> resultType;

    private String parameterType;

    private String resultMap;

    private SqlCommandType commandType;

//    private BoundSql boundSql;
    private SqlSource sqlSource;

    private Configuration configuration;

    public static class Builder {
        private MappedStatement mappedStatement = new MappedStatement();

        public Builder(Configuration configuration, String id, SqlSource sqlSource, SqlCommandType commandType, Class<?> resultTypeClass) {
            this.mappedStatement.configuration = configuration;
            this.mappedStatement.id = id;
            if(sqlSource instanceof RawSqlSource){
                this.mappedStatement.sqlSource = ((RawSqlSource) sqlSource).getSqlSource();
            }else{
                this.mappedStatement.sqlSource = sqlSource;
            }
//            this.mappedStatement.boundSql = new BoundSql(sql, parameterMappings, paramObject);
            this.mappedStatement.commandType = commandType;
            this.mappedStatement.resultType = resultTypeClass;
        }

        public MappedStatement.Builder setResultType(String resultType) throws ClassNotFoundException {
            this.mappedStatement.setResultType(Class.forName(resultType));
            return this;
        }
        //返回mappedStatement对象
        public MappedStatement build(){
            //判空
            assert this.mappedStatement.id != null;

            return this.mappedStatement;
        }


    }
}
