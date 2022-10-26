package com.lz;

import com.lz.entity.Blog;
import com.lz.mapper.BlogMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@SpringBootTest
class BlogApplicationTests {
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private BlogMapper blogMapper;

    @Test
    public void set()
    {
        List<Blog> blogs = blogMapper.getAllBlog(Long.valueOf(1));
        redisTemplate.opsForValue().set("myKey", Collections.singletonList(blogs));
        System.out.println(redisTemplate.opsForValue().get("myKey"));
    }
}