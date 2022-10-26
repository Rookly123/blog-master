package com.lz.service;

import com.lz.entity.Type;

import java.util.List;

public interface TypeService {
    public List<Type> getAllType(Long userid);
    public Type getTypeById(Long id,Long userid);
    public Type getTypeByName(String name,Long userid);
    public void saveType(Type type,Long userid);
    public void updateType(Type type,Long userid);
    public void deleteType(Long id,Long userid);
}
