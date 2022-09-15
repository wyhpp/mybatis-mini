package base;

import lombok.Data;

/**
 * @author wangyuhao
 */
@Data
public class MappedStatement {

    private String namespace;

    private String id;

    private String resultType;

    private String sql;

}
