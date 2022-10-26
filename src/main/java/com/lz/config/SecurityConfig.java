package com.lz.config;

import com.lz.util.MySessionExpiredStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) //开启权限注解,默认是关闭的
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MySessionExpiredStrategy sessionExpiredStrategy;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/css/**","/js/**","/images/**","/lib/**","/login","/logout","/logoutsuccess","/useradd","/addCheck","/loginCheck","/getCaptchaImg").permitAll()
                .antMatchers("/admin/users").hasAnyAuthority("admin")
                .anyRequest().authenticated()
                .and().
                formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/loginCheck")
                .successForwardUrl("/loginCheck")
                .failureForwardUrl("/loginCheck")
                .and()
                .sessionManagement() // 添加 Session管理器
                .invalidSessionUrl("/login") // Session失效后跳转到这个链接
//                .maximumSessions(1)
//                .maxSessionsPreventsLogin(false)
//                .expiredSessionStrategy(sessionExpiredStrategy)
//                .and()等待完善
                .and().logout().logoutUrl("/logout")
                .invalidateHttpSession(true)
                //清除认证信息
                .clearAuthentication(true)
                .and().csrf().disable();
    }
}
