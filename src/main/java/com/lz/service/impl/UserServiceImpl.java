package com.lz.service.impl;

import com.lz.entity.User;
import com.lz.mapper.UserMapper;
import com.lz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class UserServiceImpl implements UserService,UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Transactional
    @Override
    public User login(String username, String password) {
        if(userDetailsService.loadUserByUsername(username) == null)
        {
            return  null;
        }
        User user = userMapper.getUser(username);
        if(user == null)
        {
            return null;
        }
        String oldPassword = user.getPassword();
        if(encoder.matches(password,oldPassword))
        {
            return user;

        }
        else {
            return null;
        }

    }

    @Override
    public void saveUser(User user) {
        userMapper.saveUser(user);
    }

    @Override
    public void updateUser(long id, String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        userMapper.updateUser(id,encoder.encode(password));
    }

    @Override
    public void updateUserStatus(long id, Integer type) {
        userMapper.updateUserStatus(id,type);
    }

    @Override
    public List<User> getAllUser() {
        List<User> allUser = userMapper.getAllUser();
        return allUser;
    }

    @Override
    public User getUser(String username) {
        return  userMapper.getUser(username);
    }

    @Override
    public User getUserById(Long userid) {
        return userMapper.getUserById(userid);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        User user = getUser(s);
        if(user.getType() == 1)
        {
            return new org.springframework.security.core.userdetails.User(s,user.getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList("admin,user"));
        }
        return new org.springframework.security.core.userdetails.User(s,user.getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList("user"));
    }
}
