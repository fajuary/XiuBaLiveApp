package com.seek.contacts.module.contacts.bean;

import com.xiu8.base.BaseBean;

/**
 * Created by yangxu on 2018/6/7.
 */

public class ContactsFindBean extends BaseBean {



    private boolean isApply;
    /**
     * remark : 张三
     * mobile : 15000000000
     * type : 0
     * totalCount : 3
     */

    private String remark;
    private String mobile;
    private int type;
    private int totalCount;

    public boolean isApply() {
        return isApply;
    }

    public ContactsFindBean setApply(boolean apply) {
        isApply = apply;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
