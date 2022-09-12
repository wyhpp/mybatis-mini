package MyJDBC;

import java.sql.ResultSet;

public interface RowMapper<T>{
    /**
     * 将结果转换为bean
     * @param resultSet
     * @return
     */
    T mapRow(ResultSet resultSet);
}
