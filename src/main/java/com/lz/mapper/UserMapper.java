package com.lz.mapper;

import com.lz.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface UserMapper {
    void saveUser(User user);
    void updateUser(long id,String password);
    void updateUserStatus(long id,Integer type);
    public List<User> getAllUser();
    User getUser(String username);

    User getUserById(@Param("userid") Long userid);
}
