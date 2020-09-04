package com.seek.contacts.module.contacts.bean;

import com.xiu8.base.BaseBean;

import java.util.List;

/**
 * Created by yangxu on 2018/6/13.
 */

public class ContactsInfoBean extends BaseBean {
    int count;
    int pageSize;
    int totalPage;
    List<ContactsFindBean> list;
    public int getCount() {
        return count;
    }

    public ContactsInfoBean setCount(int count) {
        this.count = count;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public ContactsInfoBean setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public ContactsInfoBean setTotalPage(int totalPage) {
        this.totalPage = totalPage;
        return this;
    }

    public List<ContactsFindBean> getList() {
        return list;
    }

    public ContactsInfoBean setList(List<ContactsFindBean> list) {
        this.list = list;
        return this;
    }
}
