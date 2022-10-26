package com.lz.service.impl;

import com.lz.constant.RedisKeyConstants;
import com.lz.entity.Type;
import com.lz.mapper.TypeMapper;
import com.lz.service.RedisService;
import com.lz.service.TypeService;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TypeServiceImpl implements TypeService {
    @Autowired
    private TypeMapper typeMapper;

    @Autowired
    private RedisService redisService;

    @Transactional
    @Override
    public List<Type> getAllType(Long userid) {
        if(redisService.hasKey(RedisKeyConstants.CATEGORY_NAME_LIST+"userid"+userid))
        {
            JSONArray o = redisService.getObjectByValue(RedisKeyConstants.CATEGORY_NAME_LIST+"userid"+userid,JSONArray.class);

            List<Type> listByValue = JSONArray.toList(o, Type.class); //将json转成需要的对象
            return listByValue;
        }
        else
        {
            List<Type> list = typeMapper.getAllType(userid);
            JSONArray jsonArray= JSONArray.fromObject(list); // 将数据转成json字符串
            redisService.saveObjectToValue(RedisKeyConstants.CATEGORY_NAME_LIST+"userid"+userid,jsonArray);
            return list;
        }
    }

    @Transactional
    @Override
    public Type getTypeById(Long id,Long userid) {
        if(redisService.hasKey(RedisKeyConstants.CATEGORY_NAME_LIST+id+" " +userid)) {
            JSONArray o = redisService.getObjectByValue(RedisKeyConstants.CATEGORY_NAME_LIST+id+"userid" +userid,JSONArray.class);
            List<Type> listByValue = JSONArray.toList(o, Type.class); //将json转成需要的对象
            if(listByValue.contains(id)) {
                return listByValue.get(id.intValue());
            }
            else
            {
                redisService.deleteCacheByKey(RedisKeyConstants.CATEGORY_NAME_LIST+id+"userid" +userid);
                getAllType(userid);
            }
        }
        else
        {
            getAllType(userid);
        }
        return typeMapper.getTypeById(id,userid);
    }

    @Transactional
    @Override
    public Type getTypeByName(String name,Long userid) {
        return typeMapper.getTypeByName(name,userid);
    }

    @Transactional
    @Override
    public void saveType(Type type,Long userid) {
        deleteCache(userid);
        type.setUser_id(userid);
        typeMapper.saveType(type);
    }

    @Override
    public void updateType(Type type,Long userid) {
        deleteCache(userid);
        type.setUser_id(userid);
        redisService.deleteCacheByKey(RedisKeyConstants.CATEGORY_NAME_LIST+type.getId() + "userid" +userid);
        typeMapper.updateType(type);
    }

    @Override
    public void deleteType(Long id,Long userid) {
        deleteCache(userid);
        typeMapper.deleteType(id,userid);
    }

    public void deleteCache(Long userid)
    {
        redisService.deleteCacheByKey(RedisKeyConstants.CATEGORY_NAME_LIST+"userid"+userid);
    }
}
