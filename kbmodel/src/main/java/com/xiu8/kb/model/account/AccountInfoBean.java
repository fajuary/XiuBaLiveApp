package com.xiu8.kb.model.account;

/**
 * Created by chunyang on 2018/8/29.
 */

public class AccountInfoBean {

    /**
     * amount : long,账号余额
     * time : integer,轮询时间(秒)
     */

    private long amount;
    private int time;

    public long getAmount() {
        return amount;
    }

    public int getTime() {
        return time;
    }

}
