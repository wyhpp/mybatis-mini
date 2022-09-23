package pojo;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class User {
    private Long id;

    private String userId;

    private  String userHead;

    private Date createTime;

    private Date updateTime;

    private String userName;
}
