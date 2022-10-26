package com.lz.mapper;

import com.lz.entity.Blog;
import com.lz.vo.BlogView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface BlogMapper {
    List<Blog> getAllBlog(@Param("userid")Long userid);
    void saveBlog(Blog blog);
    Blog getBlogById(@Param("id") Long id);
    void deleteBlog(Long id);
    Integer updateBlog(Blog blog);
    List<Blog> searchBlog(Blog blog);

    Integer countBlog(@Param("userid")Long userid);
    List<String> findGroupYear(Long userid);
    List<Blog> findByYear(@Param("year")String year,@Param("userid")Long userid);


    List<Blog> getBlogByTypeId(@Param("typeId")Long typeId,@Param("userid")Long userid);
    List<Blog> getBlogByTagId(@Param("tagId")Long tagId,@Param("userid")Long userid);

    List<Blog> getIndexBlog();
    List<Blog> getFooterBlog();

    List<Blog> getBlogBymyself(@Param("userid") Long userid);
    List<Blog> getRecommendBlog();
    List<Blog> searchIndexBlog(@Param("query")String query);
    Blog getDetailedBlog(@Param("id")Long id);
    void updateViews(@Param("id")Long id,@Param("views") Integer views);

    List<BlogView> getBlogViewsList();

    //获取用户已发布博客
    List<Blog> getUserBlog(long id);

    //获取用户全部博客
    List<Blog> getUserBlogAdmin(long id);
}
