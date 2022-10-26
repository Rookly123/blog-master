package com.lz.service;

import com.lz.entity.Blog;
import com.lz.entity.Tag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TagService {
    public List<Tag> getAllTag(Long userid);
    public List<Tag> getAllTags(Long userid);
    public Tag getTagById(@Param("id") Long id,@Param("userid")Long userid);
    public Tag getTagByName(String name,Long userid);
    public void saveTag(Tag tag,Long userid);
    public void updateTag(Tag tag,Long userid);
    public void deleteTag(Long id,Long userid);

}
