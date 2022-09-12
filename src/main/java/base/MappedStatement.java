package base;

import lombok.Data;

@Data
public class MappedStatement {

    private String namespace;

    private String id;

    private String resultType;

    private String sql;

}
