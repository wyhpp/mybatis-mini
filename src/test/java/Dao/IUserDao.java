package Dao;

import org.apache.ibatis.annotations.Param;
import pojo.User;

import java.util.List;

public interface IUserDao {

    String queryUserName(String uId);

    Integer queryUserAge(String uId);

    User queryUserInfoById(@Param(value = "id") Long id,@Param(value = "userId1") String userId1);

//    User queryUserInfoById(Long id,String userId1);
    User queryUserInfo(User user);

    int updateUserInfo(User user);

    int insertUserInfo(User user);

    List<User> queryUserInfoList();
}
