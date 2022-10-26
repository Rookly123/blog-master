package com.lz.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class PageInfo<T> {
    public Integer pages;//总页数
    public Integer nowPage;//当前页
    public Integer prePage;//上一页
    public Integer nextPage;//下一页
    public Integer num;//每页的记录数
    public Integer total;//总记录数
    public boolean hasPreviousPage;//是否有上一页
    public boolean hasNextPage;//是否有下一页
    public List list;
    public PageInfo(List list,int num,int nowPage)
    {
        this.total = list.size();
        this.list = list.subList( (nowPage - 1) * num , Math.min(nowPage  * num,total));
        this.pages = (this.total / num ) + (this.total % num > 0 ? 1 : 0);
        this.nowPage = nowPage;
        this.prePage = nowPage - 1;
        this.nextPage = nowPage + 1;
        this.num = num;
        this.hasPreviousPage = this.prePage > 0 ? true : false;
        this.hasNextPage = this.nextPage <= pages ? true : false;
    }
}