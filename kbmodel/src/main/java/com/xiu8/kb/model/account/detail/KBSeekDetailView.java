package com.xiu8.kb.model.account.detail;

import com.xiu8.base.mvp.view.IBaseView;

import java.util.List;

/**
 * Created by zhangpengfei on 2018/8/29.
 * k币明细页面view层
 */

public interface KBSeekDetailView extends IBaseView {

    /**
     * 显示余额
     * @param amount
     */
    void onAccountResult(long amount);

    /**
     *  //GET  /kb/account/detail 二期--货币--用户K币收支明细接口
     * @param kbListInfoList
     * @param nomore 为true表示没有更多数据了，false表示还有数据
     */
    void getLoadMoreKbList(List<SeekKBInfoBean> kbListInfoList, boolean nomore);

    /**
     * 刷新获取到的数据列表
     * @param kbListInfoList
     */
    void getRefreshKblist(List<SeekKBInfoBean> kbListInfoList);

    /**
     * 没有更多数据了
     */
    void noMoreDataList();

    /**
     * 空页面展示
     */
    void showEmptyPage();

    /**
     *
     * @param type 刷新还是加载更多 0：表示刷新 1：表示加载更多
     * @param errorMsg 错误提示
     */
    void onKBListError(int type,String errorMsg);
}
