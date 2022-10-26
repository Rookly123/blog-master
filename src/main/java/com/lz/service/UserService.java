package com.lz.service;

import com.lz.entity.User;

import java.util.List;

public interface UserService {
    User login( String username,String password);
    void saveUser(User user);
    void updateUser(long id,String password);
    void updateUserStatus(long id,Integer type);
    List<User> getAllUser();

    User getUser(String username);

    User getUserById(Long userid);
}
