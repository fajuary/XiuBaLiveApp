package com.xiu8.kb.model.account.detail;

import com.xiu8.base.BaseBean;

import java.io.Serializable;

/**
 * Created by zhangpengfei on 2018/8/29.
 * 不继承BaseBean会出现混淆错误
 * seek币列表属性bean
 */

public class SeekKBInfoBean extends BaseBean {
    //消费描述
    private String remark;
    //消费时间戳
    private long time;
    //消费金额
    private int amount;
    //0=收入，1=支出
    private int type;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
