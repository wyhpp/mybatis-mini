package base;

import lombok.Data;
import mapping.BoundSql;

/**
 * @author wangyuhao
 */
@Data
public class MappedStatement {

    private String id;

    private String resultType;

    private String parameterType;

    private String resultMap;

    private SqlCommandType commandType;

    private BoundSql boundSql;

    public static class Builder {
        private MappedStatement mappedStatement = new MappedStatement();

        public Builder(String id, String sql, SqlCommandType commandType) {
            this.mappedStatement.id = id;
            this.mappedStatement.boundSql = new BoundSql(sql);
            this.mappedStatement.commandType = commandType;
        }

        public MappedStatement.Builder setResultType(String resultType){
            this.mappedStatement.boundSql.setResultType(resultType);
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
