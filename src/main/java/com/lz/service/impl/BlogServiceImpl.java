package com.lz.service.impl;

import com.lz.NotFoundException;
import com.lz.constant.RedisKeyConstants;
import com.lz.entity.Blog;
import com.lz.entity.Tag;
import com.lz.entity.Type;
import com.lz.mapper.BlogMapper;
import com.lz.service.BlogService;
import com.lz.service.RedisService;
import com.lz.service.TagService;
import com.lz.util.MarkdownUtils;
import com.lz.vo.BlogView;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl implements BlogService {
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private TagService tagService;

    @Autowired
    private RedisService redisService;

    @PostConstruct
    private void saveBlogToRedis(){
        if(!redisService.hasKey(RedisKeyConstants.BLOG_VIEWS_MAP)) {
            Map<Integer, Integer> blogViewsMap = getBlogViewsMap();//获取所有博客的访问数
            redisService.saveMapToHash(RedisKeyConstants.BLOG_VIEWS_MAP,blogViewsMap);
        }
//        getAllBlog();
    }

    private Map<Integer, Integer> getBlogViewsMap() {
        List<BlogView> blogViewList = blogMapper.getBlogViewsList();
        Map<Integer, Integer> blogViewsMap = new HashMap<>(128);
        for (BlogView blogView : blogViewList) {
            blogViewsMap.put(blogView.getId().intValue(), blogView.getViews());
        }
        return blogViewsMap;
    }

    private Map<Long, Blog> getBlogsMap(Long userid) {
        List<Blog> blogsList = blogMapper.getAllBlog(userid);
        System.out.println(blogsList.size());
        Map<Long, Blog> blogViewsMap = new HashMap<>(128);
        for (Blog blog : blogsList) {
            blogViewsMap.put(blog.getId(), blog);
        }
        return blogViewsMap;
    }

    @Override
    public List<Blog> getAllBlog(Long userid) {
        if(!redisService.hasKey(RedisKeyConstants.BLOG_LIST+userid))//如果没有，取出所有博客存到redis，然后返回
        {
            Map<Long, Blog> blogsMap = getBlogsMap(userid);
            redisService.saveMapToHash(RedisKeyConstants.BLOG_LIST+userid,blogsMap);
            return new ArrayList<>(blogsMap.values());
        }
        else {
            return new ArrayList<>(redisService.getMapByHash(RedisKeyConstants.BLOG_LIST+userid).values());
        }
    }
    //博客数据应该即时更新，选择删除缓存
    @Override
    public void saveBlog(Blog blog) {
        if(blog.getId()==null){
            blog.setCreateTime(new Date());
            blog.setUpdateTime(new Date());
            blog.setViews(0);
            blogMapper.saveBlog(blog);
        }else{
            blog.setUpdateTime(new Date());
        }
        deleteBlogRedisCache();
    }

    @Override
    public Blog getBlogById(Long id,Long userid) {
        if(redisService.hasKey(RedisKeyConstants.BLOG_LIST))
        {
            Map mapByHash = redisService.getMapByHash(RedisKeyConstants.BLOG_LIST);
            if(mapByHash.containsKey(id))
            {
                return (Blog) mapByHash.get(id);
            }
            else
            {
                deleteBlogRedisCache();
                getAllBlog(userid);
            }
        }
        else
        {
            getAllBlog(userid);
        }
        return blogMapper.getBlogById(id);
    }

    @Override
    public void deleteBlog(Long id) {
        deleteBlogRedisCache();
        blogMapper.deleteBlog(id);
    }

    @Override
    public void updateBlog(Blog blog) {
        blog.setUpdateTime(new Date());
        deleteBlogRedisCache();
        blogMapper.updateBlog(blog);
    }

    //待定
    @Override
    public List<Blog> searchBlog(Blog blog) {
        return blogMapper.searchBlog(blog);
    }

    @Override
    public List<Blog> getBlogBymyself(Long userid) {
        if(redisService.hasKey(RedisKeyConstants.BLOG_VIEWS_MAP+"userid"+userid))
        {
            JSONArray o = redisService.getObjectByValue(RedisKeyConstants.BLOG_MY_LIST+"userid"+userid,JSONArray.class);
            List<Blog> blogBymyself = JSONArray.toList(o, Blog.class);
            return blogBymyself;
        }
        else {
            List<Blog> blogBymyself = blogMapper.getBlogBymyself(userid);
            redisService.saveObjectToValue(RedisKeyConstants.BLOG_MY_LIST+"userid"+userid,blogBymyself);
            return blogBymyself;
        }
    }



    @Override
    public Integer countBlog(Long userid) {
        if(redisService.hasKey(RedisKeyConstants.BLOG_LIST+userid))
        {
            return redisService.getMapByHash(RedisKeyConstants.BLOG_LIST+userid).size();
        }
        return blogMapper.countBlog(userid);
    }

    @Override
    public Map<String, List<Blog>> archiveBlog(Long userid) {
        if(redisService.hasKey(RedisKeyConstants.ARCHIVE_BLOG_MAP+userid))
        {
            return redisService.getMapByHash(RedisKeyConstants.ARCHIVE_BLOG_MAP+userid);
        }
        else {
            List<String> years = blogMapper.findGroupYear(userid);
            Map<String, List<Blog>> map = new HashMap<>();
            for (String year : years) {
                map.put(year, blogMapper.findByYear(year,userid));
            }
            redisService.saveMapToHash(RedisKeyConstants.ARCHIVE_BLOG_MAP+userid,map);
            return map;
        }
    }

    @Override
    public List<Blog> getBlogByTypeId(Long typeId,Long userid) {
        if(redisService.hasKey(RedisKeyConstants.CATEGORY_NAME_LIST+userid + typeId))
        {
            List ListByHash = redisService.getListByValue(RedisKeyConstants.CATEGORY_NAME_LIST +userid + typeId);
            return ListByHash;
        }
        else {
            List<Blog> blogByTypeId = blogMapper.getBlogByTypeId(typeId,userid);
            redisService.saveListToValue(RedisKeyConstants.CATEGORY_NAME_LIST+userid + typeId,blogByTypeId);
            return blogByTypeId;
        }
    }

    @Override
    public List<Blog> getBlogByTagId(Long tagId,Long userid) {
        if(redisService.hasKey(RedisKeyConstants.TAG_CLOUD_LIST + tagId))
        {
            List ListByHash = redisService.getListByValue(RedisKeyConstants.TAG_CLOUD_LIST + tagId);
            return ListByHash;
        }
        else {
            List<Blog> blogByTypeId = blogMapper.getBlogByTagId(tagId,userid);
            redisService.saveListToValue(RedisKeyConstants.TAG_CLOUD_LIST + tagId,blogByTypeId);
            return blogByTypeId;
        }
    }

    //后期待修改
    @Override
    public List<Blog> getIndexBlog() {
        return blogMapper.getIndexBlog();
    }

    @Override
    public List<Blog> getIndexBlog_footer() {
        return blogMapper.getFooterBlog();
    }

    @Override
    public List<Blog> getRecommendBlog() {
        if(redisService.hasKey(RedisKeyConstants.RECOMMENT_LIST))
        {
            List list = redisService.getListByValue(RedisKeyConstants.RECOMMENT_LIST);
            return list;
        }
        else
        {
            List<Blog> recommendBlog = blogMapper.getRecommendBlog();
            redisService.saveListToValue(RedisKeyConstants.RECOMMENT_LIST,recommendBlog);
            return recommendBlog;
        }
    }

    @Override
    public List<Blog> searchIndexBlog(String query) {
        return blogMapper.searchIndexBlog(query);
    }

    /**
     *     读数据，此时需要修改访问量，涉及写操作
     *     写操作先更新mysql，然后删除缓存
     *     缓存无法命中的时候重新从数据库中读数据
     */
    @Override
    public Blog getDetailedBlog(Long id,Long userid) {
        Blog blog=blogMapper.getDetailedBlog(id);
        if(blog==null){
            throw new NotFoundException("该博客不存在");
        }

        List tags = new ArrayList();
        if(blog.getTagIds()=="" || blog.getTagIds().equals("")){

        }else{
            String[] tagIds=blog.getTagIds().split(",");
            for(String tagId:tagIds){
                Long tagid=Long.parseLong(tagId);
                tags.add(tagService.getTagById(tagid,blog.getUserId()));
            }
            blog.setTags(tags);
        }

        String content=blog.getContent();
        blog.setContent(MarkdownUtils.markdownToHtmlExtensions(content));
        Map<Integer,Integer> map;
        if(!redisService.hasKey(RedisKeyConstants.BLOG_VIEWS_MAP))
        {
           map = getBlogViewsMap();
           map.put(id.intValue(),map.get(blog.getId().intValue()) + 1);
           redisService.saveMapToHash(RedisKeyConstants.BLOG_VIEWS_MAP,map);
        }
        else {
            map = redisService.getMapByHash(RedisKeyConstants.BLOG_VIEWS_MAP);
            map.put(id.intValue(),map.getOrDefault(blog.getId().intValue(),0) + 1);
            redisService.saveMapToHash(RedisKeyConstants.BLOG_VIEWS_MAP,map);
        }

        blog.setViews(blog.getViews() + 1);
//        blogMapper.updateViews(id,view + 1);
        return blog;
    }

    private void deleteBlogRedisCache(){
        redisService.deleteCacheByKey(RedisKeyConstants.BLOG_VIEWS_MAP);
        redisService.deleteCacheByKey(RedisKeyConstants.BLOG_LIST);
        redisService.deleteCacheByKey(RedisKeyConstants.ARCHIVE_BLOG_MAP);
        redisService.deleteCacheByKey(RedisKeyConstants.RECOMMENT_LIST);
    }
}