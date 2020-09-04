package com.seek.contacts.module.contacts.bean;

import com.xiu8.base.BaseBean;

import java.util.List;

/**
 * Created by chunyang on 2018/6/14.
 */

public class RecommendContact extends BaseBean {

    private int count;
    private int totalPage;
    private int pageSize;
    private List<ContactFriend> list;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<ContactFriend> getList() {
        return list;
    }

    public void setList(List<ContactFriend> list) {
        this.list = list;
    }
}
