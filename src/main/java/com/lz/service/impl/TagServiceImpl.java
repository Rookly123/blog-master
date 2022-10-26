package com.lz.service.impl;

import com.lz.constant.RedisKeyConstants;
import com.lz.entity.Tag;
import com.lz.entity.Type;
import com.lz.mapper.TagMapper;
import com.lz.service.RedisService;
import com.lz.service.TagService;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisService redisService;

//    @PostConstruct
    @Transactional
    @Override
    public List<Tag> getAllTag(Long userid) {
        if(redisService.hasKey(RedisKeyConstants.TAG_CLOUD_LIST))
        {
            JSONArray o = redisService.getObjectByValue(RedisKeyConstants.TAG_CLOUD_LIST,JSONArray.class);
            List<Tag> listByTag = JSONArray.toList(o,Tag.class);
            return listByTag;
        }
        else
        {
            List<Tag> list = tagMapper.getAllTag(userid);
            JSONArray jsonArray = JSONArray.fromObject(list);
            redisService.saveObjectToValue(RedisKeyConstants.TAG_CLOUD_LIST,jsonArray);
            return list;
        }
    }

    @Override
    public List<Tag> getAllTags(Long userid) {
        if(redisService.hasKey(RedisKeyConstants.TAGS_CLOUD_LIST+userid))
        {
            JSONArray o = redisService.getObjectByValue(RedisKeyConstants.TAGS_CLOUD_LIST+userid,JSONArray.class);
            List<Tag> listByTag = JSONArray.toList(o,Tag.class);
            return listByTag;
        }
        else
        {
            List<Tag> list = tagMapper.getAllTag(userid);
            JSONArray jsonArray = JSONArray.fromObject(list);
            redisService.saveObjectToValue(RedisKeyConstants.TAGS_CLOUD_LIST+userid,jsonArray);
            return list;
        }
    }

    @Transactional
    @Override
    public Tag getTagById(Long id,Long userid) {
        if(redisService.hasKey(RedisKeyConstants.TAG_CLOUD_LIST+userid)) {
            JSONArray o = redisService.getObjectByValue(RedisKeyConstants.TAG_CLOUD_LIST+userid,JSONArray.class);
            List<Tag> listByValue = JSONArray.toList(o, Tag.class); //将json转成需要的对象
            if(listByValue.contains(id)) {
                return listByValue.get(id.intValue());
            }
            else
            {
                redisService.deleteCacheByKey(RedisKeyConstants.TAG_CLOUD_LIST+userid);
                getAllTag(userid);
            }
        }
        else
        {
            getAllTag(userid);
        }
        return tagMapper.getTagById(id,userid);
    }

    @Transactional
    @Override
    public Tag getTagByName(String name,Long userid) {
        return tagMapper.getTagByName(name,userid);
    }

    @Transactional
    @Override
    public void saveTag(Tag tag,Long userid) {
        deleteCache(userid);
        tag.setUser_id(userid);
        tagMapper.saveTag(tag);
    }

    @Override
    public void updateTag(Tag tag,Long userid) {
        deleteCache(userid);
        tag.setUser_id(userid);
        tagMapper.updateTag(tag);
    }

    @Override
    public void deleteTag(Long id,Long userid) {
        deleteCache(userid);
        tagMapper.deleteTag(id,userid);
    }

    public void deleteCache(Long userid)
    {
        redisService.deleteCacheByKey(RedisKeyConstants.TAG_CLOUD_LIST+userid);
        redisService.deleteCacheByKey(RedisKeyConstants.TAGS_CLOUD_LIST+userid);
    }

}
