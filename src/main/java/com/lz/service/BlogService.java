package com.lz.service;

import com.lz.entity.Blog;

import java.util.List;
import java.util.Map;

public interface BlogService {
    List<Blog> getAllBlog(Long userid);
    void saveBlog(Blog blog);
    Blog getBlogById(Long id,Long userid);
    void deleteBlog(Long id);
    void updateBlog(Blog blog);
    List<Blog> searchBlog(Blog blog);

    List<Blog> getBlogBymyself(Long id);

    Integer countBlog(Long userid);
    Map<String,List<Blog>> archiveBlog(Long userid);


    List<Blog> getBlogByTypeId(Long typeId,Long userid);
    List<Blog> getBlogByTagId(Long tagId,Long userid);


    List<Blog> getIndexBlog();

    List<Blog> getIndexBlog_footer();

    List<Blog> getRecommendBlog();
    List<Blog> searchIndexBlog(String query);
    Blog getDetailedBlog(Long id,Long userid);


}
