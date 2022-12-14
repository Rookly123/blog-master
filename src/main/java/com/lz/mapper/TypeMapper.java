package com.lz.mapper;

import com.lz.entity.Type;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface TypeMapper {
    public List<Type> getAllType(@Param("userid") Long userid);
    public Type getTypeById(@Param("id")Long id,@Param("userid") Long userid);
    public Type getTypeByName(@Param("name")String name ,@Param("userid") Long userid);
    public void saveType(Type type);
    public void updateType(Type type);
    public void deleteType(Long id ,@Param("userid") Long userid);


}
