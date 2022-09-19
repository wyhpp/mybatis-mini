package base;

import lombok.Data;

/**
 * @author wangyuhao
 */
@Data
public class MappedStatement {

    private String id;

    private String resultType;

    private String sql;

    private String parameterType;

    private String resultMap;

    private SqlCommandType commandType;

    public static class Builder {
        private MappedStatement mappedStatement = new MappedStatement();

        public Builder(String id, String sql, SqlCommandType commandType) {
            this.mappedStatement.id = id;
            this.mappedStatement.sql = sql;
            this.mappedStatement.commandType = commandType;
        }
        //返回mappedStatement对象
        public MappedStatement build(){
            //判空
            assert this.mappedStatement.id != null;

            return this.mappedStatement;
        }


    }
}
