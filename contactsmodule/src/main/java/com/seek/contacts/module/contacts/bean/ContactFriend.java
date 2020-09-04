package com.seek.contacts.module.contacts.bean;

import com.xiu8.base.BaseBean;

/**
 * Created by chunyang on 2018/6/14.
 */

public class ContactFriend extends BaseBean {

    /**
     * userId : long,用户id
     * avatar : string,用户头像
     * nick : string,昵称
     * remark : string,通讯录备注名
     */

    private long userId;
    private String avatar;
    private String nick;
    private String remark;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
