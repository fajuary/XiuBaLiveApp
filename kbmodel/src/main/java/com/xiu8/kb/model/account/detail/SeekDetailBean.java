package com.xiu8.kb.model.account.detail;

import com.xiu8.base.BaseBean;

import java.util.List;

/**
 * Created by zhangpengfei on 2018/8/30.
 *
 * 不继承BaseBean会出现混淆错误
 *
 * 我的k币明细bean
 */

public class SeekDetailBean extends BaseBean {
    private int count;//总条数
    private int pageSize;//每页数量
    private int totalPage;//总页数
    private List<SeekKBInfoBean> list;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<SeekKBInfoBean> getList() {
        return list;
    }

    public void setList(List<SeekKBInfoBean> list) {
        this.list = list;
    }
}
