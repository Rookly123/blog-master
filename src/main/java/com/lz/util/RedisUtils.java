package com.lz.util;

import com.lz.constant.RedisKeyConstants;
import com.lz.mapper.BlogMapper;
import com.lz.service.BlogService;
import com.lz.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class RedisUtils {
    @Autowired
    public RedisService redisService;

    @Autowired
    public RedisTemplate redisTemplate;

    @Autowired
    public BlogMapper blogMapper;

    @Autowired
    public BlogService blogService;

    @Scheduled(fixedRate = 300000)
    public void dodo()
    {
        if(redisService.hasKey(RedisKeyConstants.BLOG_VIEWS_MAP))
        {
            Map mapByHash = redisService.getMapByHash(RedisKeyConstants.BLOG_VIEWS_MAP);
            for(Object entry : mapByHash.keySet())
            {
               Integer id = Integer.parseInt(entry.toString());
               Integer view = (Integer) mapByHash.get(entry);
               blogMapper.updateViews( (long) id,view + 1);
            }
        }

        Set<String> keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);
    }
}
