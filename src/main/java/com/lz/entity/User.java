package com.lz.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 *
 * @author lize
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails{

    /**编号*/
    private Long id;

    /**昵称*/
    private String nickname;

    /**用户名*/
    private String username;

    /**密码*/
    private String password;

    /**头像地址*/
    private String avatar;

    /**用户类型*/
    private Integer type;

    private Boolean isOk;

    private Collection<? extends GrantedAuthority> authorities;

    /**级联关系*/
    private List<Blog> blogs = new ArrayList<>();

    public User(Long id, String nickname, String username, String password, String avatar, Integer type) {
        this.id = id;
        this.nickname = nickname;
        this.username = username;
        this.password = password;
        this.avatar = avatar;
        this.type = type;
        if(type == 1)
        {
            isOk = true;
        }
        else
        {
            isOk = false;
        }
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
